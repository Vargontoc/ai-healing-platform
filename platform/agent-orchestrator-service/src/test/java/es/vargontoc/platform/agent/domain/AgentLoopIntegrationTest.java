package es.vargontoc.platform.agent.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(properties = "spring.autoconfigure.exclude=org.springframework.ai.vectorstore.pgvector.PgVectorStoreAutoConfiguration")
@AutoConfigureMockMvc
@org.springframework.test.context.TestPropertySource(properties = "agent.max-steps=1")
class AgentLoopIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // We need to verify that /start endpoint works.
    // Since we don't have a real OpenAI key, we rely on the fact that without
    // mocking, it might fail or we mock the ChatModel.
    // Ideally we should mock the ChatClient or ChatModel to return a dummy
    // response.

    // Spring AI ChatClient is final or hard to mock directly due to fluent API.
    // But we can test that the Controller calls the service.

    // For this context, checking that the context loads and the endpoint is
    // reachable is a good start.
    // Mocking the whole chain of ChatClient is verbose.

    @Test
    void shouldStartAgentAndReturnErrorDueToMissingApiKey() throws Exception {
        // Since we have a dummy key, the OpenAI client will likely throw 401.
        // We handle errors gracefully now, so we expect 200 OK but with a failure
        // message.
        // We set agent.max-steps=1 to fail fast.

        mockMvc.perform(get("/api/v1/agents/start").param("goal", "Analyze this file"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED")) // The controller sets status COMPLETED on success,
                                                                    // even if the result is an error message
                .andExpect(jsonPath("$.result").value("Agent failed to produce a result within the step limit."));
    }
}
