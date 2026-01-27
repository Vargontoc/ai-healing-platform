package es.vargontoc.ai.healing.platform.agent.web;

import es.vargontoc.ai.healing.platform.agent.domain.AgentInstance;
import es.vargontoc.ai.healing.platform.agent.domain.AgentLog;
import es.vargontoc.ai.healing.platform.agent.service.AgentInstanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/agents")
@RequiredArgsConstructor
public class AgentInstanceController {

    private final AgentInstanceService agentInstanceService;

    @GetMapping
    public ResponseEntity<Map<String, List<AgentInstance>>> getAllAgents() {
        return ResponseEntity.ok(Map.of("content", agentInstanceService.getAllAgents()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgentInstance> getAgent(@PathVariable String id) {
        return agentInstanceService.getAgent(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<Map<String, Object>> startAgent(@PathVariable String id) {
        AgentInstance agent = agentInstanceService.startAgent(id);
        return ResponseEntity.ok(Map.of(
                "id", agent.getId(),
                "message", "Agent started successfully",
                "status", agent.getStatus()));
    }

    @PostMapping("/{id}/stop")
    public ResponseEntity<Map<String, Object>> stopAgent(@PathVariable String id) {
        AgentInstance agent = agentInstanceService.stopAgent(id);
        return ResponseEntity.ok(Map.of(
                "id", agent.getId(),
                "message", "Agent stopped successfully",
                "status", agent.getStatus()));
    }

    @GetMapping("/{id}/logs")
    public ResponseEntity<Map<String, Object>> getAgentLogs(
            @PathVariable String id,
            @PageableDefault(size = 100, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<AgentLog> logs = agentInstanceService.getAgentLogs(id, pageable);

        return ResponseEntity.ok(Map.of(
                "logs", logs.getContent(),
                "total", logs.getTotalElements(),
                "hasMore", logs.hasNext(),
                "page", logs.getNumber(),
                "size", logs.getSize()));
    }
}
