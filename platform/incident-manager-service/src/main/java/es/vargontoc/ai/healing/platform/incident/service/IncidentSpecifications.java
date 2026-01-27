package es.vargontoc.ai.healing.platform.incident.service;

import es.vargontoc.ai.healing.platform.incident.domain.Incident;
import org.springframework.data.jpa.domain.Specification;

public class IncidentSpecifications {
    public static Specification<Incident> hasStatus(String status) {
        return (root, query, criteriaBuilder) -> status == null ? null
                : criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Incident> hasService(String serviceName) {
        return (root, query, criteriaBuilder) -> serviceName == null ? null
                : criteriaBuilder.equal(root.get("serviceName"), serviceName);
    }
}
