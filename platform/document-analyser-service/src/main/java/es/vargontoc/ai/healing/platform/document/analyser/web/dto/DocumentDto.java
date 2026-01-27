package es.vargontoc.ai.healing.platform.document.analyser.web.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class DocumentDto {
    private String id;
    private String name;
    private String type;
    private String serviceId;
    private String version;
    private String status;
    private Long size;
    private LocalDateTime uploadedAt;
    private LocalDateTime analyzedAt;
}
