package es.vargontoc.ai.healing.platform.document.analyser.web;

import es.vargontoc.ai.healing.platform.document.analyser.service.DocumentAnalysisService;
import es.vargontoc.ai.healing.platform.document.analyser.web.dto.AnalysisRequest;
import es.vargontoc.ai.healing.platform.document.analyser.web.dto.AnalysisResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analysis")
public class DocumentAnalysisController {

    private final DocumentAnalysisService service;

    public DocumentAnalysisController(DocumentAnalysisService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AnalysisResponse> analyze(@RequestBody AnalysisRequest request) {
        return ResponseEntity.ok(service.analyze(request));
    }
}
