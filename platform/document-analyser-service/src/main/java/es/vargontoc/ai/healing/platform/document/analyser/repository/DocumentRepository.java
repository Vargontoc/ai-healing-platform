package es.vargontoc.ai.healing.platform.document.analyser.repository;

import es.vargontoc.ai.healing.platform.document.analyser.domain.DocumentEntity;
import es.vargontoc.ai.healing.platform.document.analyser.domain.DocumentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DocumentRepository extends JpaRepository<DocumentEntity, String> {

    @Query("SELECT d FROM DocumentEntity d WHERE " +
            "(:type IS NULL OR d.type = :type) AND " +
            "(:serviceId IS NULL OR d.serviceId = :serviceId) AND " +
            "(:status IS NULL OR d.status = :status) AND " +
            "(:search IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<DocumentEntity> findAllWithFilters(
            @Param("type") String type,
            @Param("serviceId") String serviceId,
            @Param("status") DocumentStatus status,
            @Param("search") String search,
            Pageable pageable);
}
