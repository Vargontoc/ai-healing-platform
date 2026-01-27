package es.vargontoc.ai.healing.platform.document.analyser.web;

import es.vargontoc.ai.healing.platform.document.analyser.service.DocumentAnalysisService;
import es.vargontoc.ai.healing.platform.document.analyser.web.dto.AnalysisRequest;
import es.vargontoc.ai.healing.platform.document.analyser.web.dto.AnalysisResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(DocumentAnalysisController.class)
@SuppressWarnings("null")
class DocumentAnalysisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DocumentAnalysisService service;

    @Test
    void shouldAnalyzeDocument() throws Exception {
        Mockito.when(service.analyze(any(AnalysisRequest.class)))
                .thenReturn(new AnalysisResponse("Analysis Result", Map.of("type", "SUMMARY")));

        mockMvc.perform(post("/api/v1/analysis")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "content": "This is a test document",
                            "type": "SUMMARY"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("Analysis Result"))
                .andExpect(jsonPath("$.metadata.type").value("SUMMARY"));
    }
}
