package es.vargontoc.platform.agent.domain;

import java.util.List;

public interface AgentMemory {
    void add(String role, String content);

    List<String> getHistory();

    void clear();
}
