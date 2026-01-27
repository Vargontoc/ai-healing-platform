package es.vargontoc.ai.healing.platform.notification.repository;

import es.vargontoc.ai.healing.platform.notification.domain.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {
    
    Page<Notification> findAllByUserId(String userId, Pageable pageable);
    
    Page<Notification> findAllByUserIdAndReadOrderByCreatedAtDesc(String userId, boolean read, Pageable pageable);

    long countByUserIdAndReadFalse(String userId);

    // To verify existence of user notifications regardless of read status
    long countByUserId(String userId);
}
