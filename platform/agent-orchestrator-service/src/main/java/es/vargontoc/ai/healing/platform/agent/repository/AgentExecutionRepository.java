package es.vargontoc.ai.healing.platform.agent.repository;

import es.vargontoc.ai.healing.platform.agent.domain.AgentExecutionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AgentExecutionRepository extends JpaRepository<AgentExecutionHistory, Long> {
    List<AgentExecutionHistory> findByContextIdOrderByTimestampDesc(String contextId);
}
