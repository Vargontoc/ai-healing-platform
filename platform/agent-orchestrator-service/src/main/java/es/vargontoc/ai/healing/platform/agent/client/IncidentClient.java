package es.vargontoc.ai.healing.platform.agent.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "incident-manager-service")
public interface IncidentClient {

    @GetMapping("/api/v1/incidents/{id}")
    es.vargontoc.ai.healing.platform.agent.client.dto.IncidentResponse getIncident(@PathVariable("id") Long id);

    @org.springframework.web.bind.annotation.PostMapping("/api/v1/incidents")
    es.vargontoc.ai.healing.platform.agent.client.dto.IncidentResponse createIncident(
            @org.springframework.web.bind.annotation.RequestBody es.vargontoc.ai.healing.platform.agent.client.dto.IncidentRequest request);

    @GetMapping("/api/v1/incidents/hash/{hash}")
    es.vargontoc.ai.healing.platform.agent.client.dto.IncidentResponse getIncidentByHash(
            @PathVariable("hash") String hash);
}
