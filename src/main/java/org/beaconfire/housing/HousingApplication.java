package org.beaconfire.housing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication()
@EnableEurekaClient
public class HousingApplication {

    public static void main(String[] args) {
        SpringApplication.run(HousingApplication.class, args);
    }

}
