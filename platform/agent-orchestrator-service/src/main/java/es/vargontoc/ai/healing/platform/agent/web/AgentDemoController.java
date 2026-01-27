package es.vargontoc.ai.healing.platform.agent.web;

import es.vargontoc.ai.healing.platform.agent.service.AgentOrchestrationService;
import es.vargontoc.ai.healing.platform.agent.web.dto.ValidationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/agents")
@RequiredArgsConstructor
public class AgentDemoController {

    private final AgentOrchestrationService orchestrationService;

    @PostMapping("/analyze-and-fix")
    public ResponseEntity<String> analyzeAndFix(@RequestBody ValidationRequest request) {
        String result = orchestrationService.runAnalysisAndFixFlow(request.getServiceId(), request.getTestClassName());
        return ResponseEntity.ok(result);
    }
}
