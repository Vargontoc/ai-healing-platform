package es.vargontoc.ai.healing.platform.agent.service;

import es.vargontoc.ai.healing.platform.agent.domain.AgentInstance;
import es.vargontoc.ai.healing.platform.agent.domain.AgentLog;
import es.vargontoc.ai.healing.platform.agent.domain.AgentStatus;
import es.vargontoc.ai.healing.platform.agent.domain.AgentType;
import es.vargontoc.ai.healing.platform.agent.repository.AgentInstanceRepository;
import es.vargontoc.ai.healing.platform.agent.repository.AgentLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgentInstanceService {

    private final AgentInstanceRepository agentInstanceRepository;
    private final AgentLogRepository agentLogRepository;
    private final AgentLogger agentLogger;

    public List<AgentInstance> getAllAgents() {
        return agentInstanceRepository.findAll();
    }

    public Optional<AgentInstance> getAgent(String id) {
        return agentInstanceRepository.findById(id);
    }

    @Transactional
    public AgentInstance registerAgent(String id, String name, AgentType type) {
        return agentInstanceRepository.findById(id)
                .orElseGet(() -> {
                    AgentInstance newAgent = AgentInstance.builder()
                            .id(id)
                            .name(name)
                            .type(type)
                            .status(AgentStatus.STOPPED)
                            .build();
                    return agentInstanceRepository.save(newAgent);
                });
    }

    @Transactional
    public AgentInstance startAgent(String id) {
        AgentInstance agent = agentInstanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agent not found: " + id));

        if (agent.getStatus() == AgentStatus.RUNNING) {
            throw new RuntimeException("Agent is already running");
        }

        agent.setStatus(AgentStatus.RUNNING);
        agentLogger.info(id, "Agent started manually");
        return agentInstanceRepository.save(agent);
    }

    @Transactional
    public AgentInstance stopAgent(String id) {
        AgentInstance agent = agentInstanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agent not found: " + id));

        if (agent.getStatus() == AgentStatus.STOPPED) {
            throw new RuntimeException("Agent is already stopped");
        }

        agent.setStatus(AgentStatus.STOPPED);
        agentLogger.info(id, "Agent stopped manually");
        return agentInstanceRepository.save(agent);
    }

    @Transactional
    public void updateStatus(String id, AgentStatus status, String message) {
        agentInstanceRepository.findById(id).ifPresent(agent -> {
            agent.setStatus(status);
            agentInstanceRepository.save(agent);
            if (message != null) {
                agentLogger.info(id, "Status changed to " + status + ": " + message);
            }
        });
    }

    public Page<AgentLog> getAgentLogs(String id, Pageable pageable) {
        return agentLogRepository.findByAgentId(id, pageable);
    }
}
