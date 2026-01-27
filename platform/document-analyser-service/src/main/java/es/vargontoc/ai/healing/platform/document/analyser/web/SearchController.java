package es.vargontoc.ai.healing.platform.document.analyser.web;

import es.vargontoc.ai.healing.platform.document.analyser.service.DocumentSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class SearchController {

    private final DocumentSearchService searchService;

    @GetMapping("/search")
    public ResponseEntity<List<String>> search(
            @RequestParam("query") String query,
            @RequestParam(value = "serviceId", required = false) String serviceId) {
        
        List<String> results = searchService.search(query, serviceId);
        return ResponseEntity.ok(results);
    }
}
