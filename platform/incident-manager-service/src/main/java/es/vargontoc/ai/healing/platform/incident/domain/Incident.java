package es.vargontoc.ai.healing.platform.incident.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "incidents", indexes = {
        @Index(name = "idx_service_name", columnList = "service_name"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_last_seen", columnList = "last_seen"),
        @Index(name = "idx_incident_hash", columnList = "hash", unique = true)
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "service_name")
    private String serviceName;

    @Column(name = "error_type")
    private String errorType;

    @Column(length = 4000)
    private String description;

    private String status;

    @Column(name = "first_seen")
    private LocalDateTime detectedAt;

    @Column(name = "last_seen")
    private LocalDateTime lastSeen;

    @Column(name = "hash", unique = true)
    private String incidentHash;

    @Column(name = "occurrence_count")
    private Integer occurrences;

    @Column(length = 4000)
    private String stacktrace;

    @Column(name = "assigned_to")
    private String assignedTo;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (detectedAt == null)
            detectedAt = LocalDateTime.now();
        if (lastSeen == null)
            lastSeen = LocalDateTime.now();
        if (status == null)
            status = "OPEN";
        if (occurrences == null)
            occurrences = 1;
        if (stacktrace == null)
            stacktrace = "No stacktrace available";
    }

    @PreUpdate
    public void preUpdate() {
        // lastSeen is updated by business logic when an occurrence happens
        // updatedAt tracks manual changes
        updatedAt = LocalDateTime.now();
    }
}
