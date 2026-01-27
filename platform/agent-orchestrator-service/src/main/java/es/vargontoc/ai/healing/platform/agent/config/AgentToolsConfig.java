package es.vargontoc.ai.healing.platform.agent.config;

import es.vargontoc.ai.healing.platform.agent.tools.TestRunnerTool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import java.util.function.Function;

@Configuration
public class AgentToolsConfig {

    @Bean
    @Description("Run all tests for a specific module. Input: moduleName (e.g., 'incident-manager-service'). Output: Maven build log.")
    public Function<String, String> runTestsTool(TestRunnerTool testRunnerTool) {
        return testRunnerTool::runTests;
    }

    @Bean
    @Description("Run a single test. Input: 'moduleName:ClassName#MethodName'. Output: Maven build log.")
    public Function<String, String> runSingleTestTool(TestRunnerTool testRunnerTool) {
        return input -> {
            String[] parts = input.split(":");
            if (parts.length != 2)
                return "Invalid input format. Use 'moduleName:ClassName#MethodName'";
            return testRunnerTool.runSingleTest(parts[0], parts[1]);
        };
    }
}
