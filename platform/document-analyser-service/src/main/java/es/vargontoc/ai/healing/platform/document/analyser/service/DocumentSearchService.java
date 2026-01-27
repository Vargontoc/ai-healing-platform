package es.vargontoc.ai.healing.platform.document.analyser.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentSearchService {

    private final VectorStore vectorStore;

    public List<String> search(String query, String serviceId) {
        log.info("Searching for query: '{}' with serviceId: '{}'", query, serviceId);

        SearchRequest request = SearchRequest.query(query).withTopK(5);

        if (serviceId != null && !serviceId.isEmpty()) {
            Filter.Expression filterExpression = new FilterExpressionBuilder()
                    .eq("service_id", serviceId)
                    .build();
            request = request.withFilterExpression(filterExpression);
        }

        List<Document> results = vectorStore.similaritySearch(request);

        return results.stream()
                .map(Document::getContent)
                .toList();
    }
}
