package es.vargontoc.ai.healing.platform.agent.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "agent_instances")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentInstance {

    @Id
    private String id; // e.g., "agent-001" - manually assigned or UUID

    private String name;

    @Enumerated(EnumType.STRING)
    private AgentStatus status;

    @Enumerated(EnumType.STRING)
    private AgentType type;

    private LocalDateTime createdAt;
    private LocalDateTime lastExecution;

    @Embedded
    private AgentConfiguration configuration;

    @Embedded
    private AgentMetrics metrics;

    @PrePersist
    public void prePersist() {
        if (createdAt == null)
            createdAt = LocalDateTime.now();
        if (status == null)
            status = AgentStatus.STOPPED;
        if (configuration == null)
            configuration = new AgentConfiguration();
        if (metrics == null)
            metrics = new AgentMetrics();
    }
}
