package es.vargontoc.incident.service;

import es.vargontoc.incident.domain.IncidentEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class IncidentConsumer {

    private final IncidentService service;

    public IncidentConsumer(IncidentService service) {
        this.service = service;
    }

    @Bean
    public Consumer<IncidentEvent> incident() {
        return event -> {
            service.reportIncident(event.getServiceName(), event.getStacktrace());
        };
    }
}
