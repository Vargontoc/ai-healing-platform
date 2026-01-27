package es.vargontoc.ai.healing.platform.incident.repository;

import es.vargontoc.ai.healing.platform.incident.domain.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long>, JpaSpecificationExecutor<Incident> {
    java.util.Optional<Incident> findByIncidentHash(String incidentHash);
}
