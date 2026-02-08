package com.esprit.planning.controller;

import com.esprit.planning.dto.ProgressUpdateRequest;
import com.esprit.planning.entity.ProgressUpdate;
import com.esprit.planning.service.ProgressUpdateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/progress-updates")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Progress Updates", description = "Create and manage progress updates for projects")
public class ProgressUpdateController {

    private final ProgressUpdateService progressUpdateService;

    @GetMapping
    @Operation(summary = "List all progress updates", description = "Returns all progress updates in the system.")
    @ApiResponse(responseCode = "200", description = "Success")
    public ResponseEntity<List<ProgressUpdate>> getAll() {
        return ResponseEntity.ok(progressUpdateService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get progress update by ID", description = "Returns a single progress update by its id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Progress update not found", content = @Content)
    })
    public ResponseEntity<ProgressUpdate> getById(
            @Parameter(description = "Progress update ID", example = "1", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(progressUpdateService.findById(id));
    }

    @GetMapping("/project/{projectId}")
    @Operation(summary = "List by project", description = "Returns all progress updates for the given project.")
    @ApiResponse(responseCode = "200", description = "Success")
    public ResponseEntity<List<ProgressUpdate>> getByProjectId(
            @Parameter(description = "Project ID", example = "1", required = true) @PathVariable Long projectId) {
        return ResponseEntity.ok(progressUpdateService.findByProjectId(projectId));
    }

    @GetMapping("/contract/{contractId}")
    @Operation(summary = "List by contract", description = "Returns all progress updates for the given contract.")
    @ApiResponse(responseCode = "200", description = "Success")
    public ResponseEntity<List<ProgressUpdate>> getByContractId(
            @Parameter(description = "Contract ID", example = "1", required = true) @PathVariable Long contractId) {
        return ResponseEntity.ok(progressUpdateService.findByContractId(contractId));
    }

    @GetMapping("/freelancer/{freelancerId}")
    @Operation(summary = "List by freelancer", description = "Returns all progress updates submitted by the given freelancer.")
    @ApiResponse(responseCode = "200", description = "Success")
    public ResponseEntity<List<ProgressUpdate>> getByFreelancerId(
            @Parameter(description = "Freelancer ID", example = "10", required = true) @PathVariable Long freelancerId) {
        return ResponseEntity.ok(progressUpdateService.findByFreelancerId(freelancerId));
    }

    @PostMapping
    @Operation(summary = "Create progress update", description = "Creates a new progress update. Do not send id, createdAt or updatedAt.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = ProgressUpdate.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content)
    })
    public ResponseEntity<ProgressUpdate> create(@RequestBody ProgressUpdateRequest request) {
        ProgressUpdate entity = ProgressUpdate.builder()
                .projectId(request.getProjectId())
                .contractId(request.getContractId())
                .freelancerId(request.getFreelancerId())
                .title(request.getTitle())
                .description(request.getDescription())
                .progressPercentage(request.getProgressPercentage())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(progressUpdateService.create(entity));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update progress update", description = "Updates an existing progress update by id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(schema = @Schema(implementation = ProgressUpdate.class))),
            @ApiResponse(responseCode = "404", description = "Progress update not found", content = @Content)
    })
    public ResponseEntity<ProgressUpdate> update(
            @Parameter(description = "Progress update ID", example = "1", required = true) @PathVariable Long id,
            @RequestBody ProgressUpdateRequest request) {
        ProgressUpdate entity = ProgressUpdate.builder()
                .projectId(request.getProjectId())
                .contractId(request.getContractId())
                .freelancerId(request.getFreelancerId())
                .title(request.getTitle())
                .description(request.getDescription())
                .progressPercentage(request.getProgressPercentage())
                .build();
        return ResponseEntity.ok(progressUpdateService.update(id, entity));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete progress update", description = "Deletes a progress update and its comments.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "404", description = "Progress update not found", content = @Content)
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "Progress update ID", example = "1", required = true) @PathVariable Long id) {
        progressUpdateService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
