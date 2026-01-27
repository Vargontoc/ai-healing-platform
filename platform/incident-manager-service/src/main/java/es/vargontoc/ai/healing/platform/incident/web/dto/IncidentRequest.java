package es.vargontoc.ai.healing.platform.incident.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request to create a new incident")
public record IncidentRequest(
                @Schema(description = "Name of the affected service", example = "payment-service") String serviceName,
                @Schema(description = "Type of error", example = "NullPointerException") String errorType,
                @Schema(description = "Detailed description of the error", example = "NPE at PaymentController.process:45") String description,
                @Schema(description = "Stacktrace of the error", example = "java.lang.NullPointerExceptionat es.vargontoc...") String stacktrace,
                @Schema(description = "Initial status (optional, defaults to OPEN)", example = "OPEN", defaultValue = "OPEN") String status) {
}
