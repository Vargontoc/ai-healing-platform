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

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Services", description = "API to retrieve registered microservices information")
public class ServicesController {

    private final DiscoveryClient discoveryClient;

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
}
