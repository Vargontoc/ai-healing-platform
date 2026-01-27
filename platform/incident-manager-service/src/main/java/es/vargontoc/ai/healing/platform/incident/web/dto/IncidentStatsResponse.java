package es.vargontoc.ai.healing.platform.incident.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;

@Schema(description = "Statistical summary of incidents")
public record IncidentStatsResponse(
                @Schema(description = "Total number of incidents registered", example = "150") long totalIncidents,
                @Schema(description = "Count of incidents grouped by status", example = "{\"OPEN\": 10, \"CLOSED\": 140}") Map<String, Long> byStatus,
                @Schema(description = "Count of incidents grouped by service", example = "{\"payment-service\": 50, \"user-service\": 100}") Map<String, Long> byService) {
}
