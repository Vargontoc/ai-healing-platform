package es.vargontoc.ai.healing.platform.agent.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "agent_execution_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentExecutionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "context_id")
    private String contextId; // e.g., serviceName + errorType hash

    private String goal;

    @Column(columnDefinition = "TEXT")
    private String steps; // JSON or text summary of steps taken

    @Column(columnDefinition = "TEXT")
    private String result;

    private LocalDateTime timestamp;

    @PrePersist
    public void prePersist() {
        timestamp = LocalDateTime.now();
    }
}
