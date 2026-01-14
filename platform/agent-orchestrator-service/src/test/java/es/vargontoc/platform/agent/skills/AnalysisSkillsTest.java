package es.vargontoc.platform.agent.skills;

import es.vargontoc.platform.agent.mcp.tools.IncidentAnalysisTool;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "spring.autoconfigure.exclude=org.springframework.ai.vectorstore.pgvector.PgVectorStoreAutoConfiguration")
class AnalysisSkillsTest {

        @Autowired
        private IncidentAnalysisTool incidentAnalysisTool;

        @org.springframework.boot.test.mock.mockito.MockBean
        private org.springframework.ai.vectorstore.VectorStore vectorStore;

        @Test
        void shouldAnalyzeOOMError() {
                Function<IncidentAnalysisTool.AnalysisRequest, String> analysisFunction = incidentAnalysisTool
                                .analyzeIncident();
                String result = analysisFunction.apply(new IncidentAnalysisTool.AnalysisRequest(
                                "Application crashed with java.lang.OutOfMemoryError: Java heap space"));

                assertTrue(result.contains("Memory Exhaustion"), "Should detect OOM");
                assertTrue(result.contains("CRITICAL"), "Should be CRITICAL");
        }

        @Test
        void shouldAnalyzeTimeoutError() {
                Function<IncidentAnalysisTool.AnalysisRequest, String> analysisFunction = incidentAnalysisTool
                                .analyzeIncident();
                String result = analysisFunction
                                .apply(new IncidentAnalysisTool.AnalysisRequest(
                                                "Connection timed out linking to database"));

                assertTrue(result.contains("Network/Dependency Failure"), "Should detect Timeout");
        }

        // We cannot easily test AgentLooper picking this tool without a real LLM or
        // complex mocking of ChatClient.
        // The previous AgentLoopIntegrationTest verified that the controller calls the
        // looper.
        // Here we verify the "Brain's hands" (the tool) works.
}
