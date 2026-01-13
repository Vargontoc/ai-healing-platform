package es.vargontoc.incident.web;

import es.vargontoc.incident.domain.Incident;
import es.vargontoc.incident.service.IncidentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/incidents")
public class IncidentController {

    private final IncidentService service;

    public IncidentController(IncidentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Incident> reportIncident(@RequestBody Map<String, String> payload) {
        String serviceName = payload.get("serviceName");
        String stacktrace = payload.get("stacktrace");

        if (serviceName == null || stacktrace == null) {
            return ResponseEntity.badRequest().build();
        }

        Incident incident = service.reportIncident(serviceName, stacktrace);
        return ResponseEntity.ok(incident);
    }
}
