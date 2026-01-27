package es.vargontoc.ai.healing.platform.document.analyser.web;

import es.vargontoc.ai.healing.platform.document.analyser.service.DocumentIngestionService;
import es.vargontoc.ai.healing.platform.document.analyser.service.DocumentService;
import es.vargontoc.ai.healing.platform.document.analyser.web.dto.DocumentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentIngestionService ingestionService;
    private final DocumentService documentService;

    @PostMapping
    public ResponseEntity<String> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("serviceId") String serviceId,
            @RequestParam("version") String version) {

        try {
            ingestionService.ingestDocument(file, serviceId, version);
            return ResponseEntity.ok("Document ingested successfully");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error processing document: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getDocuments(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String serviceId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            Pageable pageable) {
        try {
            return ResponseEntity.ok(documentService.getDocuments(type, serviceId, status, search, pageable));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentDto> getDocument(@PathVariable String id) {
        return ResponseEntity.ok(documentService.getDocument(id));
    }
}
