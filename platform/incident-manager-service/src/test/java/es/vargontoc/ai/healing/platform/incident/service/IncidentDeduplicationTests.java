package es.vargontoc.ai.healing.platform.incident.service;

import es.vargontoc.ai.healing.platform.incident.domain.Incident;
import es.vargontoc.ai.healing.platform.incident.repository.IncidentRepository;
import es.vargontoc.ai.healing.platform.incident.web.dto.IncidentRequest;
import es.vargontoc.ai.healing.platform.incident.web.dto.IncidentResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class IncidentDeduplicationTests {

    @Mock
    private IncidentRepository repository;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private IncidentService incidentService;

    @Test
    void shouldCreateNewIncidentWhenHashNotFound() {
        // Arrange
        IncidentRequest request = new IncidentRequest("service-a", "NPE", "Null pointer exception", "OPEN");
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(null);
        when(repository.findByIncidentHash(anyString())).thenReturn(Optional.empty());
        when(repository.save(any(Incident.class))).thenAnswer(invocation -> {
            Incident i = invocation.getArgument(0);
            i.setId(1L);
            return i;
        });

        // Act
        IncidentResponse response = incidentService.createIncident(request);

        // Assert
        assertEquals(1, response.occurrences());
        verify(repository, times(1)).save(any(Incident.class));
        verify(valueOperations, times(1)).set(anyString(), anyString());
    }

    @Test
    void shouldIncrementOccurrencesWhenHashFoundInRedis() {
        // Arrange
        IncidentRequest request = new IncidentRequest("service-a", "NPE", "Duplicate error", "OPEN");
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn("1"); // Found ID 1

        Incident existing = Incident.builder().id(1L).occurrences(1).build();
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Incident.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        IncidentResponse response = incidentService.createIncident(request);

        // Assert
        assertEquals(2, response.occurrences());
        verify(repository, times(1)).save(existing);
    }
}
