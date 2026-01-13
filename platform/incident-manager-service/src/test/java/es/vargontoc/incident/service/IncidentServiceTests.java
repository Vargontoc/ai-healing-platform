package es.vargontoc.incident.service;

import es.vargontoc.incident.domain.Incident;
import es.vargontoc.incident.repository.IncidentRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class IncidentServiceTests {

    @Autowired
    private IncidentService service;

    @Autowired
    private IncidentRepository repository;

    @Test
    void shouldCreateNewIncident() {
        Incident incident = service.reportIncident("test-service", "NullPointerException at line 1");

        Assertions.assertNotNull(incident.getId());
        Assertions.assertEquals(1, incident.getOccurrenceCount());
    }

    @Test
    void shouldDeduplicateIncidents() {
        service.reportIncident("test-service", "SameError");
        Incident duplicate = service.reportIncident("test-service", "SameError");

        Assertions.assertEquals(2, duplicate.getOccurrenceCount());
        Assertions.assertEquals(1, repository.count());
    }
}
