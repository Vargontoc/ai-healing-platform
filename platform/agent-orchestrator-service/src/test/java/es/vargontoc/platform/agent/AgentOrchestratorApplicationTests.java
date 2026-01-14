package es.vargontoc.platform.agent;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.test.context.TestConfiguration;

@SpringBootTest(properties = "spring.autoconfigure.exclude=org.springframework.ai.vectorstore.pgvector.PgVectorStoreAutoConfiguration")
class AgentOrchestratorApplicationTests {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public VectorStore vectorStore(EmbeddingModel embeddingModel) {
            return new SimpleVectorStore(embeddingModel);
        }
    }

    @Test
    void contextLoads() {
    }

}
