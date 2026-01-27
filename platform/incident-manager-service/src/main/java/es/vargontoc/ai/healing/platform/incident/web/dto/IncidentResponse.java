package es.vargontoc.ai.healing.platform.incident.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Response containing incident details")
public record IncidentResponse(
        @Schema(description = "Unique identifier", example = "1") Long id,
        @Schema(description = "Name of the affected service", example = "payment-service") String serviceName,
        @Schema(description = "Type of error", example = "NullPointerException") String errorType,
        @Schema(description = "Detailed description of the error", example = "NPE at PaymentController.process:45") String description,
        @Schema(description = "Current status", example = "OPEN") String status,
        @Schema(description = "When the incident was first detected") LocalDateTime detectedAt,
        @Schema(description = "When the incident was last seen") LocalDateTime lastSeen,
        @Schema(description = "Number of times this incident has occurred", example = "5") Integer occurrences,
        @Schema(description = "User assigned to this incident", example = "john.doe") String assignedTo,
        @Schema(description = "User who last updated the incident", example = "system") String updatedBy,
        @Schema(description = "When the incident was last updated") LocalDateTime updatedAt) {
}
