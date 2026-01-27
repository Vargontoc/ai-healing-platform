package es.vargontoc.ai.healing.platform.incident.service;

import es.vargontoc.ai.healing.platform.incident.web.dto.IncidentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class IncidentConsumer {

    private final IncidentService incidentService;

    @Bean
    public Consumer<IncidentRequest> input() {
        return incident -> {
            log.info("Received incident: {}", incident);
            incidentService.createIncident(incident);
        };
    }
}
