package es.vargontoc.ai.healing.platform.document.analyser.service;

import es.vargontoc.ai.healing.platform.document.analyser.domain.DocumentEntity;
import es.vargontoc.ai.healing.platform.document.analyser.domain.DocumentStatus;
import es.vargontoc.ai.healing.platform.document.analyser.repository.DocumentRepository;
import es.vargontoc.ai.healing.platform.document.analyser.web.dto.DocumentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;

    public Page<DocumentDto> getDocuments(
            String type,
            String serviceId,
            String status,
            String search,
            Pageable pageable) {

        DocumentStatus docStatus = status != null ? DocumentStatus.valueOf(status.toUpperCase()) : null;

        return documentRepository.findAllWithFilters(type, serviceId, docStatus, search, pageable)
                .map(this::mapToDto);
    }

    public DocumentDto getDocument(String id) {
        return documentRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("Document not found"));
    }

    private DocumentDto mapToDto(DocumentEntity entity) {
        return DocumentDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .type(entity.getType())
                .serviceId(entity.getServiceId())
                .version(entity.getVersion())
                .status(entity.getStatus().name())
                .size(entity.getSize())
                .uploadedAt(entity.getUploadedAt())
                .analyzedAt(entity.getAnalyzedAt())
                .build();
    }
}
