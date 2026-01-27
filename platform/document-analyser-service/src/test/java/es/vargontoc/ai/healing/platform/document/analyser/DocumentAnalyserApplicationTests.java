package es.vargontoc.ai.healing.platform.document.analyser;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.autoconfigure.exclude=org.springframework.ai.vectorstore.pgvector.PgVectorStoreAutoConfiguration")
class DocumentAnalyserApplicationTests {

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private org.springframework.ai.chat.client.ChatClient.Builder chatClientBuilder;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private org.springframework.ai.chat.client.ChatClient chatClient;

    @Test
    void contextLoads() {
        org.mockito.BDDMockito.given(chatClientBuilder.build()).willReturn(chatClient);
    }

}
