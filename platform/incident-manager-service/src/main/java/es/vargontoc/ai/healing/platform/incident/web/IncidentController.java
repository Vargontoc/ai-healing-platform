package es.vargontoc.ai.healing.platform.incident.web;

import es.vargontoc.ai.healing.platform.incident.service.IncidentService;
import es.vargontoc.ai.healing.platform.incident.web.dto.IncidentRequest;
import es.vargontoc.ai.healing.platform.incident.web.dto.IncidentResponse;
import es.vargontoc.ai.healing.platform.incident.web.dto.IncidentStatsResponse;
import es.vargontoc.ai.healing.platform.incident.web.dto.IncidentStatusUpdate;
import es.vargontoc.ai.healing.platform.incident.web.dto.IncidentAssignment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/incidents")
@RequiredArgsConstructor
@Tag(name = "Incidents", description = "API for managing and tracking system incidents")
public class IncidentController {

    private final IncidentService service;

    @Operation(summary = "Create a new incident", description = "Registers a new incident. If a similar incident (same service, error, description) exists, it increments the occurrence count.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Incident registered successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = IncidentResponse.class)) })
    })
    @PostMapping
    public ResponseEntity<IncidentResponse> create(@RequestBody IncidentRequest request) {
        return ResponseEntity.ok(service.createIncident(request));
    }

    @Operation(summary = "List incidents", description = "Retrieve a paginated list of incidents, optionally filtering by status and service name.")
    @GetMapping
    public ResponseEntity<Page<IncidentResponse>> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String serviceName,
            Pageable pageable) {
        return ResponseEntity.ok(service.getIncidents(status, serviceName, pageable));
    }

    @Operation(summary = "Get incident by ID", description = "Retrieves details of a specific incident.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the incident", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = IncidentResponse.class)) }),
            @ApiResponse(responseCode = "404", description = "Incident not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<IncidentResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.getIncident(id));
    }

    @Operation(summary = "Get Incident Statistics", description = "Provides a summary of incidents grouped by service and status")
    @GetMapping("/stats")
    public ResponseEntity<IncidentStatsResponse> stats() {
        return ResponseEntity.ok(service.getStatistics());
    }

    @Operation(summary = "Get Incident by Hash", description = "Retrieves an incident by its unique hash.")
    @GetMapping("/hash/{hash}")
    public ResponseEntity<IncidentResponse> getByHash(@PathVariable String hash) {
        return service.getIncidentByHash(hash)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update Incident Status", description = "Updates the status of an incident. Valid transitions: OPEN -> CLOSED/RESOLVED, CLOSED -> REOPENED.")
    @PutMapping("/{id}/status")
    public ResponseEntity<IncidentResponse> updateStatus(@PathVariable Long id,
            @RequestBody IncidentStatusUpdate update) {
        // Hardcoded user 'system' for now
        return ResponseEntity.ok(service.updateStatus(id, update.status(), update.comment(), "system"));
    }

    @Operation(summary = "Assign Incident", description = "Assigns an incident to a user.")
    @PutMapping("/{id}/assign")
    public ResponseEntity<IncidentResponse> assign(@PathVariable Long id, @RequestBody IncidentAssignment assignment) {
        // Hardcoded user 'system' for now
        return ResponseEntity.ok(service.assignIncident(id, assignment.assignedTo(), "system"));
    }
}
