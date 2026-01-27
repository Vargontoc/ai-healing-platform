package es.vargontoc.ai.healing.platform.agent.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "agent_logs", indexes = {
        @Index(name = "idx_agent_log_agent_id", columnList = "agentId"),
        @Index(name = "idx_agent_log_timestamp", columnList = "timestamp")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String agentId;

    private LocalDateTime timestamp;

    @Column(length = 20)
    private String level; // INFO, WARN, ERROR

    @Column(length = 4000)
    private String message;

    @Column(length = 4000)
    private String stackTrace;

    @Column(name = "metadata_json", length = 4000)
    private String metadata; // Storing metadata as JSON string for simplicity

    @PrePersist
    public void prePersist() {
        if (timestamp == null)
            timestamp = LocalDateTime.now();
    }
}
