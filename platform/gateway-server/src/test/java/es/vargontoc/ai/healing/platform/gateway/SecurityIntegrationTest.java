package es.vargontoc.ai.healing.platform.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(properties = {
        "spring.cloud.gateway.routes[0].id=test-route",
        "spring.cloud.gateway.routes[0].uri=http://localhost:8080",
        "spring.cloud.gateway.routes[0].predicates[0]=Path=/api/test/**"
})
@AutoConfigureWebTestClient
class SecurityIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    ReactiveRedisConnectionFactory reactiveRedisConnectionFactory;

    @MockitoBean
    org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository clientRegistrationRepository;

    @Test
    void shouldRejectUnauthenticatedRequests() {
        webTestClient.get()
                .uri("/api/test/resource")
                .exchange()
                .expectStatus().isUnauthorized();
    }
}
