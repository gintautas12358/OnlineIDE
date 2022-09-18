#include <stdio.h>
#include <string.h>
#include "lwip/netif.h" /* struct netif, netif_add, netif_set_up, netif_set_default */
#include "lwip/dhcp.h" /* struct dhcp, dhcp_set_struct, dhcp_start */
#include "lwip/tcp.h" /* struct tcp_pcb, tcp_new, tcp_bind, tcp_write, tcp_output, tcp_close, tcp_connect, TCP_WRITE_FLAG_COPY */
#include "lwip/sys.h" /* sys_init; S32DS (linker) will provide port-specific impl. (e.g., `SDK/middleware/tcpip/tcpip_stack/ports/OS/sys_arch.c`) */
#include "lwip/init.h" /* lwip_init */
#include "lwip/timeouts.h" /* sys_check_timeouts */
#include "lwip/ip_addr.h" /* ip_addr_t, ipaddr_aton, ip_addr_set_zero  */

/* device specific driver code */
/* generated by S32DS in `SDK/middleware/tcpip/tcpip_stack/ports/netif/enetif.h` */
#include "enetif.h" /* enet_ethernetif_init, enet_poll_interface, enet_ethernetif_shutdown */
/* for volatile global variable buttonPressed */
#include "tcp_client.h"

/* boolean value to determine, if user botton 2 was pressed; volatile keyword necessary due to interrupt context */
volatile int buttonPressed = 0;

/* globales variables for netifs */
/* THE ethernet interface */
struct netif netif;

/* DHCP struct for the ethernet netif */
extern struct dhcp netif_dhcp;

/* TCP/IP protocol control block */
static struct tcp_pcb *pcb;

/**
 * Set up network interface and start DHCP negotiation for it.
 */
static void enetif_init(void) {
	ip_addr_t ipaddr, netmask, gw;
	err_t err;

	/* fill all IP address structures with zeroes */
#define NETIF_ADDRS &ipaddr, &netmask, &gw,
	ip_addr_set_zero(&gw);
	ip_addr_set_zero(&ipaddr);
	ip_addr_set_zero(&netmask);

	/* add a network interface to the list of lwIP netifs */
	netif_add(&netif, NETIF_ADDRS NULL, enet_ethernetif_init, netif_input);

	/* set a network interface as the default network interface */
	netif_set_default(&netif);

	/* set a statically allocated struct dhcp to work with */
	dhcp_set_struct(&netif, &netif_dhcp);

	/* bring an interface up, available for processing traffic */
	netif_set_up(&netif);

	/* start DHCP negotiation for network interface */
	err = dhcp_start((struct netif *) &netif);
}

static err_t connection_callback(void *arg, struct tcp_pcb *tpcb, err_t err) {

	const *message = "HEAD /dark-mode/toggle HTTP/1.1\r\nHost: 35.188.164.106:8000\r\n\r\n";
	u16_t message_len = strlen(message) + 1; // + 1 for null terminator

	err_t error = tcp_write(tpcb, message, message_len, TCP_WRITE_FLAG_COPY);

	if (error != ERR_OK) {
		printf("error while writing.");
	}

	error = tcp_output(tpcb);

	if (error != ERR_OK) {
		printf("error while trying to output.");
	}

	error = tcp_close(tpcb);

	if (error != ERR_OK) {
		printf("error while closing.");
	}

	return 0;

}

static void create_tcp_connection() {

	ip_addr_t remote_ip; // server IP that we want to connect to
	ip_addr_t local_ip; // obtained via DHCP

	uint16_t remote_port = 8000;
	uint16_t local_port = 0;

	ipaddr_aton("35.188.164.106", &remote_ip); // transforms IP address (ip4) to a ip_ddr_t type, this is the server ip
	ip_addr_set(&local_ip, &netif.ip_addr);

	pcb = tcp_new();

	if (pcb) {
		printf("pcb is not null.");
	}

	err_t err_bind = tcp_bind(pcb, &local_ip, local_port);

	if (err_bind != ERR_OK) {
		printf("error while binding.");
	}

	err_t err_connect = tcp_connect(pcb, &remote_ip, remote_port,
			connection_callback);

	if (err_connect != ERR_OK) {
		printf("error while connecting.");
	} else {
		// reset volatile interrupt flag
		buttonPressed = 0;
	}
}

/**
 * Initialize networking and run main program loop.
 */
void start_tcp_client() {
	/* init system architecture layer (initialization of timers) */
	sys_init();

	/* init lwIP single-threaded core: initialize the network stack */
	lwip_init();

	/* init network (ethernet) interface */
	enetif_init();

	for (;;) {
		/* handle timers */
		sys_check_timeouts();

		/* poll ethernet packets and feed into network interface */
		(void) enet_poll_interface(&netif);

		/* if there is an IP address set for netif, we create a TCP connection */
		if (netif.ip_addr.addr && buttonPressed == 1) {
			create_tcp_connection();
		}

	}
	/* release the network interface */
	enet_ethernetif_shutdown(&netif);
}

