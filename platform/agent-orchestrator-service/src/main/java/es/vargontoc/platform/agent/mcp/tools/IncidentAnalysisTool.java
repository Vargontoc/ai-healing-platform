package es.vargontoc.platform.agent.mcp.tools;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;

@Configuration
public class IncidentAnalysisTool {

    @Bean
    @Description("Analyze incident data (JSON) to find root causes using heuristics")
    public Function<AnalysisRequest, String> analyzeIncident() {
        return request -> {
            String data = request.incidentData.toLowerCase();
            if (data.contains("oom") || data.contains("outofmemory")) {
                return "{\"root_cause\": \"Memory Exhaustion\", \"severity\": \"CRITICAL\", \"recommendation\": \"Check heap dump and scale up memory.\"}";
            } else if (data.contains("timeout") || data.contains("timed out") || data.contains("connection refused")) {
                return "{\"root_cause\": \"Network/Dependency Failure\", \"severity\": \"HIGH\", \"recommendation\": \"Check downstream service health and network connectivity.\"}";
            } else if (data.contains("nullpointer")) {
                return "{\"root_cause\": \"Code Defect (NPE)\", \"severity\": \"MEDIUM\", \"recommendation\": \"Check stack trace and fix null check.\"}";
            }
            return "{\"root_cause\": \"Unknown\", \"severity\": \"UNKNOWN\", \"recommendation\": \"Manual investigation required.\"}";
        };
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonClassDescription("Request to analyze incident data")
    public record AnalysisRequest(
            @JsonProperty(required = true, value = "incidentData") @JsonPropertyDescription("The raw incident data or logs as a string") String incidentData) {
    }
}
