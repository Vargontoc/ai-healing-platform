package es.vargontoc.incident;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class IncidentManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(IncidentManagerApplication.class, args);
    }

}
