package es.vargontoc.platform.agent.infrastructure;

import es.vargontoc.platform.agent.domain.AgentMemory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple in-memory memory, RequestScoped to simulate per-request agent
 * lifecycle for now.
 * In a real scenario, this would persist to DB with a Session ID.
 */
@Component
@RequestScope
public class InMemoryAgentMemory implements AgentMemory {

    private final List<String> history = new ArrayList<>();

    @Override
    public void add(String role, String content) {
        history.add(role + ": " + content);
    }

    @Override
    public List<String> getHistory() {
        return new ArrayList<>(history);
    }

    @Override
    public void clear() {
        history.clear();
    }
}
