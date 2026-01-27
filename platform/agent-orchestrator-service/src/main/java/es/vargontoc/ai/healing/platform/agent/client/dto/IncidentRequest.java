package es.vargontoc.ai.healing.platform.agent.client.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IncidentRequest {
    private String serviceName;
    private String errorType;
    private String description;
    private String status;
}
