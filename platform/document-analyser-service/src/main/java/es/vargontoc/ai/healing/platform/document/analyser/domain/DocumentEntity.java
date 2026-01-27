package es.vargontoc.ai.healing.platform.document.analyser.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    private String type;

    @Column(name = "service_id")
    private String serviceId;

    private String version;

    @Enumerated(EnumType.STRING)
    private DocumentStatus status;

    private Long size;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    @Column(name = "analyzed_at")
    private LocalDateTime analyzedAt;

    @PrePersist
    public void prePersist() {
        if (uploadedAt == null) {
            uploadedAt = LocalDateTime.now();
        }
    }
}
