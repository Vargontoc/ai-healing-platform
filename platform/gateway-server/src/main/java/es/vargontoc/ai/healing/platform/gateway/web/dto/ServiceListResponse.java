package es.vargontoc.ai.healing.platform.gateway.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Response containing the list of all services")
public record ServiceListResponse(
        @Schema(description = "List of services") List<ServiceInfo> services) {
}
