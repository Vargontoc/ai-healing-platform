package es.vargontoc.ai.healing.platform.agent.tools;

import es.vargontoc.ai.healing.platform.agent.client.IncidentClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import java.util.function.Function;

@Configuration
public class IncidentAnalysisTool {

    @Bean
    @Description("Analyze incident details given an incident ID")
    public Function<Long, String> analyzeIncidentTool(IncidentClient incidentClient) {
        return id -> {
            try {
                return incidentClient.getIncident(id).toString();
            } catch (Exception e) {
                return "Error fetching incident: " + e.getMessage();
            }
        };
    }
}
