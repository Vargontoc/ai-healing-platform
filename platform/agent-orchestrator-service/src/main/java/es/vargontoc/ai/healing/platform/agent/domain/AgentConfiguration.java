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
public class AgentConfiguration {
    @Builder.Default
    private Boolean autoRestart = false;

    @Builder.Default
    private Integer maxConcurrentTasks = 1;

    @Builder.Default
    private Long timeout = 30000L;
}
