package es.vargontoc.platform.agent.skills;

import es.vargontoc.platform.agent.mcp.tools.KnowledgeBaseTool;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = "spring.autoconfigure.exclude=org.springframework.ai.vectorstore.pgvector.PgVectorStoreAutoConfiguration")
class KnowledgeBaseToolTest {

    @Autowired
    private KnowledgeBaseTool knowledgeBaseTool;

    @MockBean
    private VectorStore vectorStore;

    @Test
    void shouldReturnDocumentsWhenFound() {
        // Mock VectorStore to return a dummy document
        Document doc = new Document("Found solution for OOM");
        when(vectorStore.similaritySearch(anyString())).thenReturn(List.of(doc));

        Function<KnowledgeBaseTool.QueryRequest, String> queryFunction = knowledgeBaseTool.queryKnowledgeBase();
        String result = queryFunction.apply(new KnowledgeBaseTool.QueryRequest("how to fix OOM"));

        assertTrue(result.contains("Found solution for OOM"), "Should return found document content");
    }

    @Test
    void shouldReturnEmptyMessageWhenNotFound() {
        when(vectorStore.similaritySearch(anyString())).thenReturn(Collections.emptyList());

        Function<KnowledgeBaseTool.QueryRequest, String> queryFunction = knowledgeBaseTool.queryKnowledgeBase();
        String result = queryFunction.apply(new KnowledgeBaseTool.QueryRequest("unknown issue"));

        assertTrue(result.contains("No matching documents found"), "Should return not found message");
    }
}
