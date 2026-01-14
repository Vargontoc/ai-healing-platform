package es.vargontoc.platform.agent.web;

import es.vargontoc.platform.agent.domain.AgentLooper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/agents")
public class AgentController {

    private final AgentLooper agentLooper;

    public AgentController(AgentLooper agentLooper) {
        this.agentLooper = agentLooper;
    }

    @GetMapping("/start")
    public ResponseEntity<Map<String, String>> startAgent(@RequestParam String goal) {
        // Trigger agent async or sync? For MVP sync is easier to debug, but real agents
        // take time.
        // Let's do it sync for now as the prompt is simple.
        try {
            String result = agentLooper.run(goal);
            return ResponseEntity.ok(Map.of(
                    "goal", goal,
                    "status", "COMPLETED",
                    "result", result));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "goal", goal,
                    "status", "ERROR",
                    "error", e.getMessage()));
        }
    }
}
