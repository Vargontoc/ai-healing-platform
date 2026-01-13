package es.vargontoc.incident.repository;

import es.vargontoc.incident.domain.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface IncidentRepository extends JpaRepository<Incident, Long> {
    Optional<Incident> findByHash(String hash);
}
