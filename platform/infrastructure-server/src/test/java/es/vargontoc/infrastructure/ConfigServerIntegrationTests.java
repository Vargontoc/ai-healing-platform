package es.vargontoc.infrastructure;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("native")

class ConfigServerIntegrationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        restTemplate = restTemplate.withBasicAuth("admin", "password");
    }

    @Test
    void shouldServeGatewayConfiguration() {
        ResponseEntity<String> response = restTemplate.getForEntity("/gateway-server/default", String.class);
        Assertions.assertTrue(response.getStatusCode().is2xxSuccessful());
        Assertions.assertTrue(response.getBody().contains("gateway"));
        Assertions.assertTrue(response.getBody().contains("8080"));
    }

    @Test
    void shouldServeAgentOrchestratorConfiguration() {
        ResponseEntity<String> response = restTemplate.getForEntity("/agent-orchestrator-service/default",
                String.class);
        System.out.println("RESPONSE BODY: " + response.getBody());
        Assertions.assertTrue(response.getStatusCode().is2xxSuccessful());
        Assertions.assertTrue(response.getBody().contains("8081"));
        Assertions.assertTrue(response.getBody().contains("jdbc:postgresql"));
    }
}
