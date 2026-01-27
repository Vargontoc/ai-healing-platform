package es.vargontoc.ai.healing.platform.notification.web;

import es.vargontoc.ai.healing.platform.notification.domain.NotificationType;
import es.vargontoc.ai.healing.platform.notification.service.NotificationService;
import es.vargontoc.ai.healing.platform.notification.web.dto.NotificationResponse;
import es.vargontoc.ai.healing.platform.notification.web.dto.UnreadCountResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Test
    void shouldGetNotifications() throws Exception {
        // Given
        NotificationResponse response = NotificationResponse.builder()
                .id("1")
                .title("Test")
                .type(NotificationType.SYSTEM)
                .build();
        
        when(notificationService.getUserNotifications(eq("user1"), any(), any()))
                .thenReturn(new PageImpl<>(List.of(response)));

        // When & Then
        mockMvc.perform(get("/api/v1/notifications")
                        .header("X-User-Id", "user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Test"));
    }

    @Test
    void shouldGetUnreadCount() throws Exception {
        // Given
        when(notificationService.getUnreadCount("user1"))
                .thenReturn(new UnreadCountResponse(5, Map.of()));

        // When & Then
        mockMvc.perform(get("/api/v1/notifications/unread-count")
                        .header("X-User-Id", "user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(5));
    }
}
