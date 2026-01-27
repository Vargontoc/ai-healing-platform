package es.vargontoc.ai.healing.platform.agent.service;

import es.vargontoc.ai.healing.platform.agent.client.IncidentClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import es.vargontoc.ai.healing.platform.agent.client.dto.IncidentResponse;

@Service
@Slf4j
public class AgentService {

    private final ChatClient chatClient;
    private final IncidentClient incidentClient;

    public AgentService(ChatClient.Builder builder, IncidentClient incidentClient) {
        this.chatClient = builder.build();
        this.incidentClient = incidentClient;
    }

    public String analyzeIncident(Long incidentId) {
        IncidentResponse incident = incidentClient.getIncident(incidentId);
        return chatClient.prompt().user("Analyze this incident: " + incident).call().content();
    }
}
