package es.vargontoc.ai.healing.platform.notification.event;

import es.vargontoc.ai.healing.platform.notification.domain.Notification;
import es.vargontoc.ai.healing.platform.notification.domain.event.IncidentCreatedEvent;
import es.vargontoc.ai.healing.platform.notification.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
class NotificationEventConsumerTest {

    @Autowired
    private ApplicationContext context;

    @MockBean
    private NotificationService notificationService;

    @Test
    @SuppressWarnings("unchecked")
    void shouldConsumeIncidentCreatedEvent() {
        // Given
        IncidentCreatedEvent event = IncidentCreatedEvent.builder()
                .id("inc-123")
                .title("Test Incident")
                .description("Something bad happened")
                .severity("CRITICAL")
                .status("OPEN")
                .timestamp(LocalDateTime.now())
                .build();

        // When
        // Manually retrieve the consumer bean and invoke it to simulate stream consumption
        Consumer<IncidentCreatedEvent> consumer = (Consumer<IncidentCreatedEvent>) context.getBean("incidentCreated");
        consumer.accept(event);

        // Then
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationService).createNotification(captor.capture());

        Notification captured = captor.getValue();
        assertThat(captured.getRelatedResourceId()).isEqualTo("inc-123");
        assertThat(captured.getTitle()).contains("Test Incident");
        assertThat(captured.getSeverity().name()).isEqualTo("CRITICAL");
    }
}
