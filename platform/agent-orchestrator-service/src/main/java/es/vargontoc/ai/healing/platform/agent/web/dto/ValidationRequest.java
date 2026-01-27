package es.vargontoc.ai.healing.platform.agent.web.dto;

import lombok.Data;

@Data
public class ValidationRequest {
    private String serviceId; // e.g., "incident-manager-service"
    private String testClassName; // e.g., "IncidentServiceTest"
}
