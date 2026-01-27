package es.vargontoc.ai.healing.platform.document.analyser.service;

import es.vargontoc.ai.healing.platform.document.analyser.web.dto.AnalysisRequest;
import es.vargontoc.ai.healing.platform.document.analyser.web.dto.AnalysisResponse;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DocumentAnalysisService {

    private final ChatClient chatClient;
    private final DocumentSearchService documentSearchService;

    public DocumentAnalysisService(ChatClient.Builder builder, DocumentSearchService documentSearchService) {
        this.chatClient = builder.build();
        this.documentSearchService = documentSearchService;
    }

    public AnalysisResponse analyze(AnalysisRequest request) {
        String content = request.content();
        String type = request.type();

        String serviceId = request.serviceId();

        // RAG: Retrieve context if serviceId is provided
        String context = "";
        if (serviceId != null && !serviceId.isBlank()) {
            java.util.List<String> results = documentSearchService.search(content, serviceId);
            if (!results.isEmpty()) {
                context = String.join("\n\n", results);
            } else {
                context = "No relevant context found.";
            }
        } else {
            context = "No service ID provided for context retrieval.";
        }

        String promptTemplate = "SUMMARY".equalsIgnoreCase(type) ? AnalysisPromptTemplates.SUMMARY_PROMPT
                : AnalysisPromptTemplates.ERROR_ANALYSIS_PROMPT;

        String finalContext = context;
        String result = chatClient.prompt()
                .user(u -> u.text(promptTemplate)
                        .param("content", content)
                        .param("context", finalContext))
                .call()
                .content();

        return new AnalysisResponse(result, Map.of(
                "timestamp", System.currentTimeMillis(),
                "type", type != null ? type : "UNKNOWN",
                "model", "openai-gpt-4o"));
    }
}
