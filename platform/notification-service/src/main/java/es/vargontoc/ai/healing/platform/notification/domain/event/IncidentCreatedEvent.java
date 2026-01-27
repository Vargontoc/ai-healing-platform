package es.vargontoc.ai.healing.platform.notification.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncidentCreatedEvent {
    private String id;
    private String title;
    private String description;
    private String severity; // INFO, WARNING, CRITICAL
    private String status;
    private LocalDateTime timestamp;
}
