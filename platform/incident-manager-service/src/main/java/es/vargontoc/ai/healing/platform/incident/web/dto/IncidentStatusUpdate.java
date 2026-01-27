package es.vargontoc.ai.healing.platform.incident.web.dto;

public record IncidentStatusUpdate(
        String status,
        String comment) {
}
