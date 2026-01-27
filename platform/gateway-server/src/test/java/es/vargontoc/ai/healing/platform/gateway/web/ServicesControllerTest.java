package es.vargontoc.ai.healing.platform.gateway.web;

import es.vargontoc.ai.healing.platform.gateway.web.dto.ServiceListResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.net.URI;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.security.test.context.support.WithMockUser;

@WebFluxTest(ServicesController.class)
@WithMockUser
class ServicesControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private DiscoveryClient discoveryClient;

    @Test
    void shouldReturnServicesList() {
        // Arrange
        String serviceName = "test-service";
        ServiceInstance instance = mock(ServiceInstance.class);
        when(instance.getUri()).thenReturn(URI.create("http://localhost:8081"));

        when(discoveryClient.getServices()).thenReturn(List.of(serviceName));
        when(discoveryClient.getInstances(serviceName)).thenReturn(List.of(instance));

        // Act & Assert
        webTestClient.get()
                .uri("/api/v1/services")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ServiceListResponse.class)
                .value(response -> {
                    assert response.services().size() == 1;
                    assert response.services().get(0).name().equals(serviceName);
                    assert response.services().get(0).status().equals("UP");
                    assert response.services().get(0).instances() == 1;
                    assert response.services().get(0).urls().contains("http://localhost:8081");
                });
    }

    @Test
    void shouldReturnEmptyListWhenNoServicesRegistered() {
        // Arrange
        when(discoveryClient.getServices()).thenReturn(List.of());

        // Act & Assert
        webTestClient.get()
                .uri("/api/v1/services")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ServiceListResponse.class)
                .value(response -> {
                    assert response.services().isEmpty();
                });
    }
}
