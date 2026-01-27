package es.vargontoc.ai.healing.platform.document.analyser.web.dto;

import java.util.Map;

public record AnalysisResponse(
    String result,
    Map<String, Object> metadata
) {}
