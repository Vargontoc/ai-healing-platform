package es.vargontoc.ai.healing.platform.incident.web;

import es.vargontoc.ai.healing.platform.incident.service.IncidentService;
import es.vargontoc.ai.healing.platform.incident.web.dto.IncidentRequest;
import es.vargontoc.ai.healing.platform.incident.web.dto.IncidentResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IncidentController.class)
@SuppressWarnings("null")
class IncidentControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IncidentService service;

    @Test
    void shouldCreateIncident() throws Exception {
        IncidentResponse response = new IncidentResponse(1L, "service-A", "ERR", "Desc", "OPEN", LocalDateTime.now(),
                LocalDateTime.now(), 1);
        when(service.createIncident(any(IncidentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/incidents")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {
                                "serviceName": "service-A",
                                "errorType": "ERR",
                                "description": "Desc"
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.serviceName").value("service-A"));
    }
}
