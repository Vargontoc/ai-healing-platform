package es.vargontoc.ai.healing.platform.incident.service;

import es.vargontoc.ai.healing.platform.incident.web.dto.IncidentRequest;
import es.vargontoc.ai.healing.platform.incident.web.dto.IncidentResponse;
import es.vargontoc.ai.healing.platform.incident.web.dto.IncidentStatsResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class IncidentServiceTests {

    @Autowired
    private IncidentService service;

    @Test
    void shouldCreateAndRetrieveIncident() {
        IncidentRequest request = new IncidentRequest("test-service", "ERROR", "Something went wrong",
                "Full stacktrace", "OPEN");
        IncidentResponse response = service.createIncident(request);

        assertNotNull(response.id());
        assertEquals("test-service", response.serviceName());
        assertEquals("OPEN", response.status());

        IncidentResponse fetched = service.getIncident(response.id());
        assertEquals(response.id(), fetched.id());
    }

    @Test
    void shouldCalculateStatistics() {
        service.createIncident(new IncidentRequest("service-A", "ERR1", "Desc", "stack", "OPEN"));
        service.createIncident(new IncidentRequest("service-A", "ERR2", "Desc", "stack", "RESOLVED"));
        service.createIncident(new IncidentRequest("service-B", "ERR1", "Desc", "stack", "OPEN"));

        IncidentStatsResponse stats = service.getStatistics();

        assertEquals(3, stats.totalIncidents());
        assertEquals(2, stats.byStatus().get("OPEN"));
        assertEquals(1, stats.byStatus().get("RESOLVED"));
        assertEquals(2, stats.byService().get("service-A"));
        assertEquals(1, stats.byService().get("service-B"));
    }

    @Test
    void shouldRetrieveStacktrace() {
        IncidentRequest request = new IncidentRequest("service-C", "ERR_STACK", "Desc with stack",
                "java.lang.NPE at line 10", "OPEN");
        IncidentResponse response = service.createIncident(request);

        var stacktrace = service.getStacktrace(response.id());
        assertEquals(response.id().toString(), stacktrace.incidentId());
        assertEquals("java.lang.NPE at line 10", stacktrace.stacktrace());
    }
}
