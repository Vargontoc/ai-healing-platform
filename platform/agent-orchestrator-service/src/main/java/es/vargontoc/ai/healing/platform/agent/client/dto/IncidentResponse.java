package es.vargontoc.ai.healing.platform.agent.client.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class IncidentResponse {
    private Long id;
    private String serviceName;
    private String errorType;
    private String description;
    private String status;
    private LocalDateTime detectedAt;
    private LocalDateTime lastSeen;
    private Integer occurrences;
}
