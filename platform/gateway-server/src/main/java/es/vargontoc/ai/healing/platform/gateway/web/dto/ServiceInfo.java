package es.vargontoc.ai.healing.platform.gateway.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Information about a registered microservice")
public record ServiceInfo(
        @Schema(description = "Name of the service", example = "incident-manager-service") String name,

        @Schema(description = "Current status of the service", example = "UP") String status,

        @Schema(description = "Number of active instances", example = "1") int instances,

        @Schema(description = "List of service URLs", example = "[\"http://localhost:8081\"]") List<String> urls) {
}
