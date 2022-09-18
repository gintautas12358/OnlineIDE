package edu.tum.ase.darkmode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableEurekaClient
public class DarkmodeApplication {
    private boolean currentDarkModeStatus = false;
    private long lastRequestTime = 0;
    private long cooldownTime = 3000;

    public static void main(String[] args) {
        SpringApplication.run(DarkmodeApplication.class, args);
    }

    @RequestMapping(path = "/dark-mode/toggle", method = {RequestMethod.GET, RequestMethod.HEAD})
    public void toggleDarkMode() {
        long time = System.currentTimeMillis();
        if (time > lastRequestTime + cooldownTime) {
            currentDarkModeStatus = !currentDarkModeStatus;
            lastRequestTime = time;
        }
    }

    @RequestMapping(path = "/dark-mode", method = RequestMethod.GET)
    public boolean getDarkMode() {
        return currentDarkModeStatus;
    }

}
