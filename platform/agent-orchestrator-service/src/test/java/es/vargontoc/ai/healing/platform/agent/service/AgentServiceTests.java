package es.vargontoc.ai.healing.platform.agent.service;

import es.vargontoc.ai.healing.platform.agent.client.IncidentClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgentServiceTests {

    @Mock(answer = org.mockito.Answers.RETURNS_DEEP_STUBS)
    private ChatClient chatClient;

    @Mock
    private IncidentClient incidentClient;

    @InjectMocks
    private AgentService agentService;

    @Test
    void shouldAnalyzeIncident() {
        // Arrange
        Long incidentId = 1L;
        es.vargontoc.ai.healing.platform.agent.client.dto.IncidentResponse incidentResponse = new es.vargontoc.ai.healing.platform.agent.client.dto.IncidentResponse();
        incidentResponse.setId(1L);
        incidentResponse.setDescription("Error");

        String expectedAnalysis = "AI Analysis Result";

        when(incidentClient.getIncident(incidentId)).thenReturn(incidentResponse);

        when(chatClient.prompt().user(anyString()).call().content()).thenReturn(expectedAnalysis);

        // Act
        String result = agentService.analyzeIncident(incidentId);

        // Assert
        assertEquals(expectedAnalysis, result);
    }
}
