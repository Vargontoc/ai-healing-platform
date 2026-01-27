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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
                LocalDateTime.now(), 1, null, "system", LocalDateTime.now());
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

    @Test
    void shouldUpdateStatus() throws Exception {
        IncidentResponse response = new IncidentResponse(1L, "service-A", "ERR", "Desc", "CLOSED", LocalDateTime.now(),
                LocalDateTime.now(), 1, null, "system", LocalDateTime.now());

        when(service.updateStatus(eq(1L), eq("CLOSED"), any(), eq("system"))).thenReturn(response);

        mockMvc.perform(put("/api/v1/incidents/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {
                                "status": "CLOSED",
                                "comment": "Closing incident"
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CLOSED"));
    }

    @Test
    void shouldAssignIncident() throws Exception {
        IncidentResponse response = new IncidentResponse(1L, "service-A", "ERR", "Desc", "OPEN", LocalDateTime.now(),
                LocalDateTime.now(), 1, "john.doe", "system", LocalDateTime.now());

        when(service.assignIncident(eq(1L), eq("john.doe"), eq("system"))).thenReturn(response);

        mockMvc.perform(put("/api/v1/incidents/1/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {
                                "assignedTo": "john.doe"
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignedTo").value("john.doe"));
    }
}
