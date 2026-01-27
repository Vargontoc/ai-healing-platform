package es.vargontoc.ai.healing.platform.notification.repository;

import es.vargontoc.ai.healing.platform.notification.domain.Notification;
import es.vargontoc.ai.healing.platform.notification.domain.NotificationType;
import es.vargontoc.ai.healing.platform.notification.domain.Severity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    void shouldFindNotificationsByUserId() {
        // Given
        Notification n1 = createNotification("user1", "Title 1", false);
        Notification n2 = createNotification("user1", "Title 2", true);
        Notification n3 = createNotification("user2", "Title 3", false);

        notificationRepository.save(n1);
        notificationRepository.save(n2);
        notificationRepository.save(n3);

        // When
        Page<Notification> result = notificationRepository.findAllByUserId("user1", PageRequest.of(0, 10));

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).extracting("title").containsExactlyInAnyOrder("Title 1", "Title 2");
    }

    @Test
    void shouldCountUnreadNotifications() {
        // Given
        notificationRepository.save(createNotification("user1", "Unread 1", false));
        notificationRepository.save(createNotification("user1", "Unread 2", false));
        notificationRepository.save(createNotification("user1", "Read 1", true));

        // When
        long count = notificationRepository.countByUserIdAndReadFalse("user1");

        // Then
        assertThat(count).isEqualTo(2);
    }

    private Notification createNotification(String userId, String title, boolean read) {
        return Notification.builder()
                .userId(userId)
                .title(title)
                .message("Message")
                .type(NotificationType.SYSTEM)
                .severity(Severity.INFO)
                .read(read)
                .build();
    }
}
