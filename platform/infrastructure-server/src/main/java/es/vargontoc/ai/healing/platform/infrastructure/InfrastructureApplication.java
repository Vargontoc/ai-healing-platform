package es.vargontoc.ai.healing.platform.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
@EnableConfigServer
public class InfrastructureApplication {

    public static void main(String[] args) {
        SpringApplication.run(InfrastructureApplication.class, args);
    }

}
