package es.vargontoc.ai.healing.platform.agent.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentMetrics {
    @Builder.Default
    private Long totalExecutions = 0L;

    @Builder.Default
    private Double successRate = 0.0;

    @Builder.Default
    private Double averageExecutionTime = 0.0;

    private String lastError;
}
