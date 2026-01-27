package es.vargontoc.ai.healing.platform.incident.service;

import es.vargontoc.ai.healing.platform.incident.domain.Incident;
import es.vargontoc.ai.healing.platform.incident.repository.IncidentRepository;
import es.vargontoc.ai.healing.platform.incident.web.dto.IncidentRequest;
import es.vargontoc.ai.healing.platform.incident.web.dto.IncidentResponse;
import es.vargontoc.ai.healing.platform.incident.web.dto.IncidentStatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class IncidentService {

    private final IncidentRepository repository;
    private final org.springframework.data.redis.core.StringRedisTemplate redisTemplate;

    @Transactional
    public IncidentResponse createIncident(IncidentRequest request) {
        String hash = calculateHash(request);
        String cacheKey = "incident:hash:" + hash;

        // Try to find existing by Hash (Redis -> DB)
        String cachedId = redisTemplate.opsForValue().get(cacheKey);
        if (cachedId != null) {
            return repository.findById(Long.valueOf(cachedId))
                    .map(existing -> {
                        existing.setOccurrences(existing.getOccurrences() + 1);
                        return toResponse(repository.save(existing));
                    })
                    .orElseGet(() -> createNewIncident(request, hash, cacheKey));
        }

        // Try DB directly (Fallback)
        return repository.findByIncidentHash(hash)
                .map(existing -> {
                    existing.setOccurrences(existing.getOccurrences() + 1);
                    // Update cache
                    redisTemplate.opsForValue().set(cacheKey, existing.getId().toString());
                    return toResponse(repository.save(existing));
                })
                .orElseGet(() -> createNewIncident(request, hash, cacheKey));
    }

    private IncidentResponse createNewIncident(IncidentRequest request, String hash, String cacheKey) {
        Incident incident = Incident.builder()
                .serviceName(request.serviceName())
                .errorType(request.errorType())
                .description(request.description())
                .status(request.status() != null ? request.status() : "OPEN")
                .incidentHash(hash)
                .occurrences(1)
                .build();

        Incident saved = repository.save(incident);
        redisTemplate.opsForValue().set(cacheKey, saved.getId().toString());
        return toResponse(saved);
    }

    private String calculateHash(IncidentRequest request) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            String raw = request.serviceName() + ":" + request.errorType() + ":" + request.description(); // Simple hash
                                                                                                          // input
            byte[] encodedhash = digest.digest(raw.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return hex(encodedhash);
        } catch (Exception e) {
            throw new RuntimeException("Error calculating hash", e);
        }
    }

    private String hex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte aByte : bytes) {
            result.append(String.format("%02x", aByte));
        }
        return result.toString();
    }

    public Page<IncidentResponse> getIncidents(String status, String serviceName, Pageable pageable) {
        return repository.findAll(
                IncidentSpecifications.hasStatus(status).and(IncidentSpecifications.hasService(serviceName)),
                pageable).map(this::toResponse);
    }

    public IncidentResponse getIncident(Long id) {
        return repository.findById(id).map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Incident not found"));
    }

    public java.util.Optional<IncidentResponse> getIncidentByHash(String hash) {
        return repository.findByIncidentHash(hash).map(this::toResponse);
    }

    @Transactional
    public IncidentStatsResponse getStatistics() {
        var all = repository.findAll();
        Map<String, Long> byStatus = all.stream()
                .collect(Collectors.groupingBy(Incident::getStatus, Collectors.counting()));
        Map<String, Long> byService = all.stream()
                .collect(Collectors.groupingBy(Incident::getServiceName, Collectors.counting()));

        return new IncidentStatsResponse(all.size(), byStatus, byService);
    }

    private IncidentResponse toResponse(Incident i) {
        return new IncidentResponse(
                i.getId(),
                i.getServiceName(),
                i.getErrorType(),
                i.getDescription(),
                i.getStatus(),
                i.getDetectedAt(),
                i.getLastSeen(),
                i.getOccurrences());
    }
}
