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
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
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

    @MockitoBean
    private WebClient.Builder webClientBuilder;

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

    @SuppressWarnings("unchecked")
    @Test
    void shouldReturnServiceDetails() {
        // Arrange
        String serviceName = "detailed-service";
        ServiceInstance instance = mock(ServiceInstance.class);
        when(instance.getUri()).thenReturn(URI.create("http://localhost:9090"));

        when(discoveryClient.getInstances(serviceName)).thenReturn(List.of(instance));

        // Mock WebClient chain
        WebClient webClient = mock(WebClient.class);
        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        // Mock responses for different endpoints based on order or argument match
        // (simplified)
        // Since we are using anyString for uri, we need to be careful. The controller
        // calls uri("/actuator/health") then others.
        // We can use thenAnswer or argument matchers.

        // Let's use specific matchers if possible, but the interfaces are generic.
        // We can mock the uri call specifically.

        WebClient.RequestHeadersSpec healthSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec healthResponse = mock(WebClient.ResponseSpec.class);
        when(requestHeadersUriSpec.uri("/actuator/health")).thenReturn(healthSpec);
        when(healthSpec.retrieve()).thenReturn(healthResponse);
        when(healthResponse.bodyToMono(Map.class))
                .thenReturn(Mono.just(Map.of("status", "UP", "details", Map.of("db", "up"))));

        WebClient.RequestHeadersSpec uptimeSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec uptimeResponse = mock(WebClient.ResponseSpec.class);
        when(requestHeadersUriSpec.uri("/actuator/metrics/process.uptime")).thenReturn(uptimeSpec);
        when(uptimeSpec.retrieve()).thenReturn(uptimeResponse);
        when(uptimeResponse.bodyToMono(Map.class))
                .thenReturn(Mono.just(Map.of("measurements", List.of(Map.of("statistic", "VALUE", "value", 100.0)))));

        WebClient.RequestHeadersSpec requestsSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec requestsResponse = mock(WebClient.ResponseSpec.class);
        when(requestHeadersUriSpec.uri("/actuator/metrics/http.server.requests")).thenReturn(requestsSpec);
        when(requestsSpec.retrieve()).thenReturn(requestsResponse);
        when(requestsResponse.bodyToMono(Map.class))
                .thenReturn(Mono.just(Map.of("measurements", List.of(Map.of("statistic", "COUNT", "value", 50.0)))));

        // Act & Assert
        webTestClient.get()
                .uri("/api/v1/services/" + serviceName)
                .header("X-API-KEY", "test-key")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ServiceDetailResponse.class)
                .value(response -> {
                    assert response.name().equals(serviceName);
                    assert response.status().equals("UP");
                    assert response.instances() == 1;
                    assert response.urls().contains("http://localhost:9090");
                    assert response.metrics().uptime() == 100000L; // 100.0 * 1000
                    assert response.metrics().totalRequests() == 50L;
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
                .header("X-API-KEY", "test-key")
                .exchange()
                .expectStatus().isNotFound();
    }
}
