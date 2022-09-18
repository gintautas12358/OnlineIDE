#include <sys/socket.h>
/*struct sockaddr, socket, bind, listen, accept, AF_INET, SOCK_STREAM
 */

#include <netinet/in.h>
/*struct sockaddr_in, IPPROTO_TCP, INADDR_ANY
 */

#include <string.h>
/* strlen */

#include <unistd.h> 
/* write, sleep, close */

int main (int argc, char **argv) {
	//initialize socket (flie) handles and constants
	int listen_fd = 0;
       	int conn_fd = 0;
       	int server_port = 5000;
       	char response_buffer[128] = "HTTP/1.1 200 OK\n"
	       	"Content-Length: 12\n"
	       	"Content-Type: text/html\n"
	       	"\n"
	       	"Hello World!"; 

	struct sockaddr_in server_addr;

	// Clear structure
	memset(&server_addr, 0, sizeof(server_addr));
	server_addr.sin_family = AF_INET;
	server_addr.sin_addr.s_addr = htonl(INADDR_ANY);
	server_addr.sin_port = htons(server_port);

	listen_fd = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
	bind(listen_fd, (struct sockaddr *) &server_addr, sizeof(server_addr));
       
	listen(listen_fd, 10); 

	while (1) {
		 conn_fd = accept(listen_fd, (struct sockaddr *) NULL, NULL);

		 write(conn_fd, response_buffer, strlen(response_buffer));
		 close(conn_fd);

		 sleep(1);
		
	}
}
