package es.vargontoc.ai.healing.platform.notification.web.dto;

import es.vargontoc.ai.healing.platform.notification.domain.NotificationType;
import java.util.Map;

public record UnreadCountResponse(
    long count,
    Map<NotificationType, Long> byType
) {}
