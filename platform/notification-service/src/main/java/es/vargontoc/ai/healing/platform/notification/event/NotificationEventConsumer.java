package es.vargontoc.ai.healing.platform.notification.event;

import es.vargontoc.ai.healing.platform.notification.domain.Notification;
import es.vargontoc.ai.healing.platform.notification.domain.NotificationType;
import es.vargontoc.ai.healing.platform.notification.domain.Severity;
import es.vargontoc.ai.healing.platform.notification.domain.event.IncidentCreatedEvent;
import es.vargontoc.ai.healing.platform.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private final NotificationService notificationService;

    @Bean
    public Consumer<IncidentCreatedEvent> incidentCreated() {
        return event -> {
            log.info("Received IncidentCreatedEvent: {}", event);
            
            // Map event to notification
            // Assuming for now generic user or system wide... 
            // BUT requirements said filtering by user is possible. 
            // Ideally incident created event might not have assignee yet? 
            // Or maybe it does. Let's assume we notify "all" or specific admins, 
            // BUT for MVP let's notify a generic 'admin' or use the event data if available.
            // Let's assume for now we assign to "admin-user" or similar if no assignee.
            
            // NOTE: Requirement just says "Eventos recibidos... se persistena".
            // Let's create a notification for "SYSTEM" for now or use a placeholder user.
            
            Notification notification = Notification.builder()
                    .userId("admin") // TODO: Determine target user based on incident assignment policy
                    .type(NotificationType.INCIDENT)
                    .title("New Incident: " + event.getTitle())
                    .message("A new incident has been reported: " + event.getDescription())
                    .severity(Severity.valueOf(event.getSeverity())) // Make sure Enums match or map safely
                    .relatedResourceType("INCIDENT")
                    .relatedResourceId(event.getId())
                    .build();

            notificationService.createNotification(notification);
        };
    }
}
