package es.vargontoc.ai.healing.platform.agent.service;

import es.vargontoc.ai.healing.platform.agent.domain.AgentLog;
import es.vargontoc.ai.healing.platform.agent.repository.AgentLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgentLogger {

    private final AgentLogRepository agentLogRepository;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Persists an agent log entry and broadcasts it via WebSocket.
     * 
     * @param agentId    The ID of the agent generating the log.
     * @param level      Log level (INFO, WARN, ERROR).
     * @param message    The log message.
     * @param stackTrace Optional stack trace for errors.
     * @param metadata   Optional metadata in JSON format.
     */
    @Async
    @Transactional
    public void log(String agentId, String level, String message, String stackTrace, String metadata) {
        AgentLog agentLog = AgentLog.builder()
                .agentId(agentId)
                .level(level)
                .message(message)
                .stackTrace(stackTrace)
                .metadata(metadata)
                .build();

        agentLogRepository.save(agentLog);

        try {
            messagingTemplate.convertAndSend("/topic/agents/" + agentId + "/logs", agentLog);
        } catch (Exception e) {
            log.warn("Failed to broadcast log via WebSocket: {}", e.getMessage());
        }

        log.info("[Agent: {}] {}: {}", agentId, level, message);
    }

    public void info(String agentId, String message) {
        log(agentId, "INFO", message, null, null);
    }

    public void info(String agentId, String message, String metadata) {
        log(agentId, "INFO", message, null, metadata);
    }

    public void warn(String agentId, String message) {
        log(agentId, "WARN", message, null, null);
    }

    public void error(String agentId, String message, String stackTrace) {
        log(agentId, "ERROR", message, stackTrace, null);
    }
}
