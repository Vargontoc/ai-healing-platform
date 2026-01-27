package es.vargontoc.ai.healing.platform.agent;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.ai.chat.client.ChatClient;

@SpringBootTest
class AgentOrchestratorApplicationTests {

    @MockitoBean
    ChatClient chatClient;

    @MockitoBean
    org.springframework.ai.vectorstore.VectorStore vectorStore;

    @MockitoBean
    org.springframework.ai.embedding.EmbeddingModel embeddingModel;

    @MockitoBean
    es.vargontoc.ai.healing.platform.agent.client.IncidentClient incidentClient;

    @Test
    void contextLoads() {
    }
}
