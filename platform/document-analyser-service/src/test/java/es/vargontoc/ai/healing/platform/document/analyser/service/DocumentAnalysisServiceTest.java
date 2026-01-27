package es.vargontoc.ai.healing.platform.document.analyser.service;

import es.vargontoc.ai.healing.platform.document.analyser.web.dto.AnalysisRequest;
import es.vargontoc.ai.healing.platform.document.analyser.web.dto.AnalysisResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import org.mockito.Answers;

import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class DocumentAnalysisServiceTest {

    @Mock
    private ChatClient.Builder chatClientBuilder;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChatClient chatClient;

    @Mock
    private DocumentSearchService documentSearchService;

    private DocumentAnalysisService documentAnalysisService;

    @BeforeEach
    void setUp() {
        // Mock Builder to return ChatClient
        given(chatClientBuilder.build()).willReturn(chatClient);

        // Mock ChatClient fluent API via Deep Stubs
        given(chatClient.prompt().user(any(Consumer.class)).call().content()).willReturn("AI Analysis Result");

        documentAnalysisService = new DocumentAnalysisService(chatClientBuilder, documentSearchService);
    }

    @Test
    void shouldAnalyzeWithContextWhenServiceIdIsProvided() {
        // Given
        String content = "Error log content";
        String serviceId = "order-service";
        String retrievedContext = "Related error in previous incident";

        AnalysisRequest request = new AnalysisRequest(content, "ERROR", serviceId);

        given(documentSearchService.search(content, serviceId)).willReturn(List.of(retrievedContext));

        // When
        AnalysisResponse response = documentAnalysisService.analyze(request);

        // Then
        assertThat(response.result()).isEqualTo("AI Analysis Result");
        verify(documentSearchService).search(content, serviceId);

        // Verify that the prompt user consumer was called (advanced verification would
        // capture the lambda)
        verify(chatClient.prompt(), times(1)).user(any(Consumer.class));
    }

    @Test
    void shouldAnalyzeWithoutContextWhenServiceIdIsMissing() {
        // Given
        String content = "Error log content";
        AnalysisRequest request = new AnalysisRequest(content, "ERROR", null);

        // When
        documentAnalysisService.analyze(request);

        // Then
        verify(documentSearchService, times(0)).search(any(), any());
    }
}
