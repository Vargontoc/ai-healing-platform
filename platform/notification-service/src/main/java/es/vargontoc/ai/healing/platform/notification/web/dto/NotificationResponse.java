package es.vargontoc.ai.healing.platform.notification.web.dto;

import es.vargontoc.ai.healing.platform.notification.domain.NotificationType;
import es.vargontoc.ai.healing.platform.notification.domain.Severity;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record NotificationResponse(
    String id,
    String userId,
    NotificationType type,
    String title,
    String message,
    Severity severity,
    boolean read,
    LocalDateTime readAt,
    LocalDateTime createdAt,
    String relatedResourceType,
    String relatedResourceId,
    String relatedResourceUrl
) {}
