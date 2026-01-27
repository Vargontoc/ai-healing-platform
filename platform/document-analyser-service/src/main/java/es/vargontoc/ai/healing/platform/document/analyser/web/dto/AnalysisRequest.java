package es.vargontoc.ai.healing.platform.document.analyser.web.dto;

public record AnalysisRequest(
        String content,
        String type,
        String serviceId) {
}
