package es.vargontoc.ai.healing.platform.agent.repository;

import es.vargontoc.ai.healing.platform.agent.domain.AgentLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AgentLogRepository extends JpaRepository<AgentLog, Long> {
    Page<AgentLog> findByAgentId(String agentId, Pageable pageable);

    Page<AgentLog> findByAgentIdAndTimestampAfter(String agentId, LocalDateTime timestamp, Pageable pageable);
}
