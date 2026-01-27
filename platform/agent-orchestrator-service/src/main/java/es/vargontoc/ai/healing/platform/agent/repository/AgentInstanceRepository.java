package es.vargontoc.ai.healing.platform.agent.repository;

import es.vargontoc.ai.healing.platform.agent.domain.AgentInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgentInstanceRepository extends JpaRepository<AgentInstance, String> {
}
