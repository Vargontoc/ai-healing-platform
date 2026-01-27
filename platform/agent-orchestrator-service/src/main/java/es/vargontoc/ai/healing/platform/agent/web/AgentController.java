package es.vargontoc.ai.healing.platform.agent.web;

import es.vargontoc.ai.healing.platform.agent.service.AgentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/agent")
@RequiredArgsConstructor
public class AgentController {

    private final AgentService service;

    @PostMapping("/analyze/{incidentId}")
    public ResponseEntity<String> analyze(@PathVariable Long incidentId) {
        return ResponseEntity.ok(service.analyzeIncident(incidentId));
    }
}
