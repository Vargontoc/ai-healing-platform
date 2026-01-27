package es.vargontoc.ai.healing.platform.incident.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response containing the stacktrace of an incident")
public record StacktraceResponse(
        @Schema(description = "Incident ID", example = "123") String incidentId,
        @Schema(description = "Stacktrace content", example = "java.lang.NullPointerException: ...") String stacktrace) {
}
