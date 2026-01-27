package es.vargontoc.ai.healing.platform.agent.service;

import es.vargontoc.ai.healing.platform.agent.client.IncidentClient;
import es.vargontoc.ai.healing.platform.agent.client.dto.IncidentRequest;
import es.vargontoc.ai.healing.platform.agent.domain.AgentExecutionHistory;
import es.vargontoc.ai.healing.platform.agent.domain.AgentStatus;
import es.vargontoc.ai.healing.platform.agent.domain.AgentType;
import es.vargontoc.ai.healing.platform.agent.repository.AgentExecutionRepository;
import es.vargontoc.ai.healing.platform.agent.tools.TestRunnerTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AgentOrchestrationService {

    private final TestRunnerTool testRunnerTool;
    private final IncidentClient incidentClient;
    private final AgentExecutionRepository historyRepository;
    private final ChatClient chatClient;
    private final AgentInstanceService agentInstanceService;
    private final AgentLogger agentLogger;

    public AgentOrchestrationService(TestRunnerTool testRunnerTool,
            IncidentClient incidentClient,
            AgentExecutionRepository historyRepository,
            ChatClient.Builder builder,
            AgentInstanceService agentInstanceService,
            AgentLogger agentLogger) {
        this.testRunnerTool = testRunnerTool;
        this.incidentClient = incidentClient;
        this.historyRepository = historyRepository;
        this.chatClient = builder.build();
        this.agentInstanceService = agentInstanceService;
        this.agentLogger = agentLogger;
    }

    public String runAnalysisAndFixFlow(String serviceId, String testClassName) {
        String agentId = "agent-" + serviceId;
        agentInstanceService.registerAgent(agentId, "Analyzer for " + serviceId, AgentType.INCIDENT_ANALYZER);

        try {
            agentInstanceService.startAgent(agentId);
        } catch (Exception e) {
            // Provide a way to force start or just log if already running (though
            // startAgent handles checks)
            agentInstanceService.updateStatus(agentId, AgentStatus.RUNNING, "Starting new analysis flow");
        }

        agentLogger.info(agentId, "Starting Agent Flow for service: " + serviceId + " test: " + testClassName);
        log.info("Starting Agent Flow for service: {} test: {}", serviceId, testClassName);

        StringBuilder flowLog = new StringBuilder();

        try {
            // 1. Run Tests (Expect Failure)
            agentLogger.info(agentId, "Step 1: Running Tests...");
            flowLog.append("1. Running Tests... ");
            String testOutput = testRunnerTool.runTests(serviceId); // Or specific test
            flowLog.append("Done.\n");

            if (!testOutput.contains("BUILD FAILURE")) {
                String msg = "Test Passed! No fix needed.";
                agentLogger.info(agentId, msg);
                flowLog.append(msg);
                agentInstanceService.stopAgent(agentId);
                return flowLog.toString();
            } else {
                agentLogger.warn(agentId, "Tests failed. Proceeding to incident creation.");
            }

            // 2. Create Incident
            agentLogger.info(agentId, "Step 2: Creating Incident...");
            flowLog.append("2. Creating Incident... ");
            String errorDescription = "Test failure in " + testClassName; // Simplified extraction
            try {
                incidentClient.createIncident(IncidentRequest.builder()
                        .serviceName(serviceId)
                        .errorType("TestFailure")
                        .description(errorDescription)
                        .status("OPEN")
                        .build());
                flowLog.append("Created.\n");
                agentLogger.info(agentId, "Incident created successfully.");
            } catch (Exception e) {
                String errorMsg = "Failed to create incident: " + e.getMessage();
                flowLog.append(errorMsg).append("\n");
                agentLogger.error(agentId, errorMsg, null);
            }

            // 3. Search History (Persistent Memory)
            agentLogger.info(agentId, "Step 3: Checking Memory...");
            flowLog.append("3. Checking Memory... ");
            String contextId = serviceId + ":TestFailure";
            List<AgentExecutionHistory> history = historyRepository.findByContextIdOrderByTimestampDesc(contextId);
            if (!history.isEmpty()) {
                String msg = "Found " + history.size() + " previous attempts.";
                flowLog.append(msg).append("\n");
                agentLogger.info(agentId, msg);
            } else {
                String msg = "No previous attempts found.";
                flowLog.append(msg).append("\n");
                agentLogger.info(agentId, msg);
            }

            // 4. AI Analysis (Simplified)
            agentLogger.info(agentId, "Step 4: AI Analysis...");
            flowLog.append("4. AI Analysis... ");
            String prompt = "Analyze this maven output and suggest a fix:\n"
                    + testOutput.substring(0, Math.min(testOutput.length(), 2000));
            String aiAnalysis = chatClient.prompt().user(prompt).call().content();
            flowLog.append("Analysis Complete.\n");
            agentLogger.info(agentId, "Analysis Complete. Generating suggestion.");

            // 5. Save History
            AgentExecutionHistory execution = AgentExecutionHistory.builder()
                    .contextId(contextId)
                    .goal("Fix test " + testClassName)
                    .steps(flowLog.toString())
                    .result(aiAnalysis)
                    .build();
            historyRepository.save(execution);

            flowLog.append("\n--- AI SUGGESTION ---\n").append(aiAnalysis);
            agentInstanceService.stopAgent(agentId);

        } catch (Exception e) {
            agentLogger.error(agentId, "Critical error in agent flow", e.getMessage());
            agentInstanceService.updateStatus(agentId, AgentStatus.ERROR, e.getMessage());
            throw e;
        }

        return flowLog.toString();
    }
}
