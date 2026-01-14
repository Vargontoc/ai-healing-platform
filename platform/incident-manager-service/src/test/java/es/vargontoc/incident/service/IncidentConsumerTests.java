package es.vargontoc.incident.service;

import es.vargontoc.incident.domain.IncidentEvent;
import es.vargontoc.incident.repository.IncidentRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(properties = {
        "spring.cloud.stream.default-binder=test",
        "spring.cloud.stream.bindings.incident-in-0.destination=incident-events",
        "spring.cloud.stream.bindings.incident-in-0.group=incident-manager-group"
})
@Import(TestChannelBinderConfiguration.class)
class IncidentConsumerTests {

    @MockBean
    private ConnectionFactory connectionFactory;

    @Autowired
    private InputDestination input;

    @Autowired
    private IncidentRepository repository;

    @Test
    void shouldCreateIncidentUsingEvent() {
        IncidentEvent event = new IncidentEvent("async-service", "AsyncError");

        input.send(MessageBuilder.withPayload(event).build(), "incident-in-0");

        Assertions.assertEquals(1, repository.count());
    }
}
