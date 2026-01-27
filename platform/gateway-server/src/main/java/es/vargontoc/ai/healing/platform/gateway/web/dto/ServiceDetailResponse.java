package es.vargontoc.ai.healing.platform.gateway.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;

@Schema(description = "Detailed information about a microservice including health and metrics")
public record ServiceDetailResponse(
        @Schema(description = "Name of the service", example = "payment-service") String name,
        @Schema(description = "Aggregated status", example = "UP") String status,
        @Schema(description = "Number of active instances", example = "2") int instances,
        @Schema(description = "List of instance URLs", example = "[\"http://localhost:8081\"]") List<String> urls,
        @Schema(description = "Health status details") ServiceHealth health,
        @Schema(description = "Performance metrics") ServiceMetrics metrics) {

    public record ServiceHealth(
            @Schema(description = "Overall health status", example = "UP") String status,
            @Schema(description = "Detailed health indicators") Map<String, Object> details) {
    }

    public record ServiceMetrics(
            @Schema(description = "Uptime in milliseconds", example = "3600000") long uptime,
            @Schema(description = "Total requests processed", example = "1500") long totalRequests,
            @Schema(description = "Failed requests count", example = "50") long failedRequests) {
    }
}
