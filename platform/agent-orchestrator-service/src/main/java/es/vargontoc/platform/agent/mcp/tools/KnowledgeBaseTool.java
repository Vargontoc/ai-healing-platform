package es.vargontoc.platform.agent.mcp.tools;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;

@Configuration
public class KnowledgeBaseTool {

    private final org.springframework.ai.vectorstore.VectorStore vectorStore;

    public KnowledgeBaseTool(org.springframework.ai.vectorstore.VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Bean
    @Description("Query the knowledge base for similar incidents or solutions")
    public Function<QueryRequest, String> queryKnowledgeBase() {
        return request -> {
            java.util.List<org.springframework.ai.document.Document> documents = vectorStore
                    .similaritySearch(request.query);
            if (documents.isEmpty()) {
                return "No matching documents found in knowledge base for query: " + request.query;
            }
            return documents.stream()
                    .map(doc -> "Content: " + doc.getContent() + "\nMetadata: " + doc.getMetadata())
                    .collect(java.util.stream.Collectors.joining("\n---\n"));
        };
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonClassDescription("Request to query the knowledge base")
    public record QueryRequest(
            @JsonProperty(required = true, value = "query") @JsonPropertyDescription("The search query associated with the failure") String query) {
    }
}
