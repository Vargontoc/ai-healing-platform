package es.vargontoc.ai.healing.platform.document.analyser.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import es.vargontoc.ai.healing.platform.document.analyser.domain.DocumentEntity;
import es.vargontoc.ai.healing.platform.document.analyser.domain.DocumentStatus;
import es.vargontoc.ai.healing.platform.document.analyser.repository.DocumentRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentIngestionService {

    private final VectorStore vectorStore;
    private final TokenTextSplitter tokenTextSplitter;
    private final DocumentRepository documentRepository;

    public void ingestDocument(MultipartFile file, String serviceId, String version) throws IOException {
        log.info("Ingesting document: {} for service: {} version: {}", file.getOriginalFilename(), serviceId, version);

        // Save pending document
        DocumentEntity entity = DocumentEntity.builder()
                .name(file.getOriginalFilename())
                .type(determineType(file.getOriginalFilename()))
                .serviceId(serviceId)
                .version(version)
                .status(DocumentStatus.PENDING)
                .size(file.getSize())
                .build();

        entity = documentRepository.save(entity);

        try {
            Resource resource = new ByteArrayResource(file.getBytes());
            TikaDocumentReader documentReader = new TikaDocumentReader(resource);

            List<Document> documents = documentReader.read();

            // Add metadata to each document
            String docId = entity.getId();
            documents.forEach(doc -> {
                Map<String, Object> metadata = doc.getMetadata();
                metadata.put("service_id", serviceId);
                metadata.put("version", version);
                metadata.put("original_filename", file.getOriginalFilename());
                metadata.put("document_db_id", docId);
            });

            // Split documents
            List<Document> splitDocuments = tokenTextSplitter.apply(documents);

            // Save to VectorStore
            vectorStore.add(splitDocuments);

            // Update status
            entity.setStatus(DocumentStatus.ANALYZED);
            entity.setAnalyzedAt(LocalDateTime.now());
            documentRepository.save(entity);

            log.info("Successfully ingested {} chunks for document {}", splitDocuments.size(),
                    file.getOriginalFilename());
        } catch (Exception e) {
            log.error("Error ingesting document", e);
            entity.setStatus(DocumentStatus.ERROR);
            documentRepository.save(entity);
            throw e;
        }
    }

    private String determineType(String filename) {
        if (filename == null)
            return "UNKNOWN";
        int dotIndex = filename.lastIndexOf('.');
        return dotIndex > 0 ? filename.substring(dotIndex + 1).toUpperCase() : "UNKNOWN";
    }
}
