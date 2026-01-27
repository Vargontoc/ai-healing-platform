package es.vargontoc.ai.healing.platform.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;

@SpringBootTest
class GatewayApplicationTests {

    @MockitoBean
    ReactiveRedisConnectionFactory reactiveRedisConnectionFactory;

    @MockitoBean
    org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository clientRegistrationRepository;

    @Test
    void contextLoads() {
    }
}
