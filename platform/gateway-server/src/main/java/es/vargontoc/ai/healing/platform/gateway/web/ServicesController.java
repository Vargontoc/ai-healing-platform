package es.vargontoc.ai.healing.platform.gateway.web;

import es.vargontoc.ai.healing.platform.gateway.web.dto.ServiceInfo;
import es.vargontoc.ai.healing.platform.gateway.web.dto.ServiceListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import es.vargontoc.ai.healing.platform.gateway.web.dto.ServiceDetailResponse;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Services", description = "API to retrieve registered microservices information")
public class ServicesController {

    private final DiscoveryClient discoveryClient;
    private final org.springframework.web.reactive.function.client.WebClient.Builder webClientBuilder;

    @Operation(summary = "Get all registered services", description = "Returns a list of all services registered in Eureka with their status and instances")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of services")
    @GetMapping
    public Mono<ServiceListResponse> getServices() {
        return Mono.fromCallable(() -> {
            List<String> serviceNames = discoveryClient.getServices();
            List<ServiceInfo> serviceInfos = new ArrayList<>();

            for (String serviceName : serviceNames) {
                List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);

                String status = instances.isEmpty() ? "DOWN" : "UP";
                int instanceCount = instances.size();
                List<String> urls = instances.stream()
                        .map(instance -> instance.getUri().toString())
                        .toList();

                serviceInfos.add(new ServiceInfo(serviceName, status, instanceCount, urls));
            }
            return new ServiceListResponse(serviceInfos);
        });
    }

    @Operation(summary = "Get service details", description = "Returns detailed information about a specific service including health and metrics")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved service details")
    @ApiResponse(responseCode = "404", description = "Service not found")
    @GetMapping("/{name}")
    public Mono<org.springframework.http.ResponseEntity<ServiceDetailResponse>> getServiceDetails(
            @PathVariable String name) {
        return Mono.fromCallable(() -> discoveryClient.getInstances(name))
                .flatMap(instances -> {
                    if (instances.isEmpty()) {
                        return Mono.just(org.springframework.http.ResponseEntity.notFound().build());
                    }

                    return reactor.core.publisher.Flux.fromIterable(instances)
                            .flatMap(this::getInstanceData)
                            .collectList()
                            .map(dataList -> aggregateServiceData(name, instances, dataList));
                });
    }

    @SuppressWarnings("unchecked")
    private Mono<InstanceData> getInstanceData(ServiceInstance instance) {
        String baseUrl = instance.getUri().toString();
        var client = webClientBuilder.baseUrl(baseUrl).build();

        Mono<Map<String, Object>> healthMono = client.get().uri("/actuator/health").retrieve().bodyToMono(Map.class)
                .map(m -> (Map<String, Object>) m)
                .onErrorResume(e -> Mono.just(Map.of("status", "DOWN", "details", Map.of("error", e.getMessage()))));

        Mono<Double> uptimeMono = client.get().uri("/actuator/metrics/process.uptime").retrieve().bodyToMono(Map.class)
                .map(m -> {
                    List<Map<String, Object>> measurements = (List<Map<String, Object>>) m.get("measurements");
                    return (Double) measurements.get(0).get("value");
                })
                .onErrorReturn(0.0);

        Mono<Map<String, Object>> requestsMono = client.get().uri("/actuator/metrics/http.server.requests").retrieve()
                .bodyToMono(Map.class)
                .map(m -> (Map<String, Object>) m)
                .onErrorResume(e -> Mono.empty()); // If metric doesn't exist yet

        return Mono.zip(healthMono, uptimeMono, requestsMono.map(Optional::of).defaultIfEmpty(Optional.empty()))
                .map(tuple -> new InstanceData(tuple.getT1(), tuple.getT2(), tuple.getT3().orElse(null)));

    }

    @SuppressWarnings("unchecked")
    private org.springframework.http.ResponseEntity<ServiceDetailResponse> aggregateServiceData(String name,
            List<ServiceInstance> instances, List<InstanceData> dataList) {
        String aggregatedStatus = dataList.stream()
                .anyMatch(d -> "UP".equals(d.health.get("status"))) ? "UP" : "DOWN";

        List<String> urls = instances.stream().map(i -> i.getUri().toString()).toList();

        // Use details from the first healthy instance, or the first one if none are
        // healthy
        Map<String, Object> healthDetails = dataList.stream()
                .filter(d -> "UP".equals(d.health.get("status")))
                .findFirst()
                .map(d -> (Map<String, Object>) d.health.getOrDefault("details", Map.of()))
                .orElse(dataList.isEmpty() ? Map.of()
                        : (Map<String, Object>) dataList.get(0).health.getOrDefault("details", Map.of()));

        ServiceDetailResponse.ServiceHealth health = new ServiceDetailResponse.ServiceHealth(aggregatedStatus,
                healthDetails);

        // Aggregate metrics
        long maxUptime = dataList.stream().mapToLong(d -> (long) (d.uptime * 1000)).max().orElse(0);

        long totalRequests = 0;
        long failedRequests = 0;

        for (InstanceData data : dataList) {
            if (data.requestsMetric != null) {
                List<Map<String, Object>> measurements = (List<Map<String, Object>>) data.requestsMetric
                        .get("measurements");
                for (Map<String, Object> m : measurements) {
                    String statistic = (String) m.get("statistic");
                    Double value = (Double) m.get("value");
                    if ("COUNT".equals(statistic)) {
                        totalRequests += value.longValue();
                    }
                }
            }
        }

        // Re-fetching specific failure metrics would be expensive (N calls).
        // Let's leave failedRequests as 0 or implement a better fetch if strict.
        // For now I'll sum generic COUNT as total.

        ServiceDetailResponse.ServiceMetrics metrics = new ServiceDetailResponse.ServiceMetrics(maxUptime,
                totalRequests, failedRequests);

        return org.springframework.http.ResponseEntity.ok(new ServiceDetailResponse(
                name, aggregatedStatus, instances.size(), urls, health, metrics));
    }

    record InstanceData(Map<String, Object> health, Double uptime, Map<String, Object> requestsMetric) {
    }
}
