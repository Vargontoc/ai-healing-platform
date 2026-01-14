package es.vargontoc.platform.agent.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class AgentLooper {

    private static final Logger logger = LoggerFactory.getLogger(AgentLooper.class);

    private final ChatClient chatClient;
    private final AgentMemory agentMemory;

    @Autowired
    public AgentLooper(ChatClient chatClient, AgentMemory agentMemory) {
        this.chatClient = chatClient;
        this.agentMemory = agentMemory;
    }

    @org.springframework.beans.factory.annotation.Value("${agent.max-steps:10}")
    private int maxSteps;

    @org.springframework.beans.factory.annotation.Value("classpath:/prompts/system-prompt.st")
    private Resource systemPromptResource;

    /**
     * Executes the agent loop for a data goal.
     * Use Spring AI's automatic function calling capabilities with robustness
     * upgrades.
     */
    public String run(String goal) {
        logger.info("Starting agent loop for goal: {}", goal);
        agentMemory.clear();

        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemPromptResource);
        String systemMessage = systemPromptTemplate.createMessage().getContent();

        agentMemory.add("System", systemMessage);

        int currentStep = 0;
        String lastResponse = "";

        while (currentStep < maxSteps) {
            try {
                logger.info("Agent Step {}/{}", currentStep + 1, maxSteps);

                // Call the LLM with tools
                String response = chatClient.prompt()
                        .user(goal) // In a real loop, we would append previous history/context to the prompt or
                                    // rely on ChatClient memory if configured
                        .functions("listFiles", "readFile", "queryKnowledgeBase", "analyzeIncident")
                        .call()
                        .content();

                lastResponse = response;
                agentMemory.add("Agent", response);

                // Simple heuristic to stop if the agent seems done or provides a final answer
                // In a more advanced version, we'd check for a specific "FINAL ANSWER" token or
                // structure.
                if (response != null && !response.isBlank()) {
                    logger.info("Agent produced a response.");
                    break;
                }

            } catch (Exception e) {
                logger.error("Error during agent step execution: {}", e.getMessage(), e);
                agentMemory.add("System", "Error encountered: " + e.getMessage()
                        + ". Please try a different approach or analyze why this failed.");
                // We continue the loop so the agent can try to recover
            } finally {
                currentStep++;
            }
        }

        if (currentStep >= maxSteps) {
            logger.warn("Agent reached maximum steps without a definitive conclusion (or just finished the loop).");
            if (lastResponse.isBlank()) {
                return "Agent failed to produce a result within the step limit.";
            }
        }

        return lastResponse;
    }
}
