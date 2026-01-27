package es.vargontoc.ai.healing.platform.gateway.web;

import es.vargontoc.ai.healing.platform.gateway.web.dto.ServiceListResponse;
import es.vargontoc.ai.healing.platform.gateway.web.dto.ServiceDetailResponse;
import org.junit.jupiter.api.Test;
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

import org.springframework.context.annotation.Import;
import es.vargontoc.ai.healing.platform.gateway.config.GatewayLocalSecurityProperties;

import org.springframework.test.context.TestPropertySource;

@WebFluxTest(ServicesController.class)
@Import(GatewayLocalSecurityProperties.class)
@WithMockUser
@TestPropertySource(properties = "gateway.security.local.valid-keys=test-key")
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
                .header("X-API-KEY", "test-key")
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
                .header("X-API-KEY", "test-key")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ServiceListResponse.class)
                .value(response -> {
                    assert response.services().isEmpty();
                });
    }

    @Test
    void shouldReturnServiceDetails() {
        // Arrange
        String serviceName = "detailed-service";
        ServiceInstance instance = mock(ServiceInstance.class);
        when(instance.getUri()).thenReturn(URI.create("http://localhost:9090"));

        when(discoveryClient.getInstances(serviceName)).thenReturn(List.of(instance));

        // Act & Assert
        webTestClient.get()
                .uri("/api/v1/services/" + serviceName)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ServiceDetailResponse.class)
                .value(response -> {
                    assert response.name().equals(serviceName);
                    assert response.status().equals("UP");
                    assert response.instances() == 1;
                    assert response.urls().contains("http://localhost:9090");
                    assert response.metrics().uptime() == 3600000L; // Mocked value
                });
    }

    @Test
    void shouldReturn404WhenServiceNotFound() {
        // Arrange
        String serviceName = "unknown-service";
        when(discoveryClient.getInstances(serviceName)).thenReturn(List.of());

        // Act & Assert
        webTestClient.get()
                .uri("/api/v1/services/" + serviceName)
                .exchange()
                .expectStatus().isNotFound();
    }
}
