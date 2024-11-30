package org.tbeerbower.wsfl_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.tbeerbower.wsfl_backend.assembler.RunnerDtoAssembler;
import org.tbeerbower.wsfl_backend.dto.RunnerCreateDto;
import org.tbeerbower.wsfl_backend.dto.RunnerSummaryDto;
import org.tbeerbower.wsfl_backend.dto.RunnerPatchDto;
import org.tbeerbower.wsfl_backend.dto.RunnerSummaryDto;
import org.tbeerbower.wsfl_backend.dto.TeamSummaryDto;
import org.tbeerbower.wsfl_backend.exception.ResourceNotFoundException;
import org.tbeerbower.wsfl_backend.model.Runner;
import org.tbeerbower.wsfl_backend.service.RunnerService;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Runner", description = "Runner management APIs")
@RestController
@RequestMapping("/api/runners")
@CrossOrigin(origins = "http://localhost:8080")
public class RunnerController  {
    
    private final RunnerService runnerService;
    private final RunnerDtoAssembler runnerDtoAssembler;
    
    public RunnerController(RunnerService runnerService, RunnerDtoAssembler runnerDtoAssembler) {
        this.runnerService = runnerService;
        this.runnerDtoAssembler = runnerDtoAssembler;
    }
    
    @Operation(
        summary = "Get all runners",
        description = "Retrieves a paginated list of all runners in the system"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved runners"
        )
    })
    @GetMapping
    public ResponseEntity<Page<RunnerSummaryDto>> getAllRunners(
            @Parameter(description = "Gender(M/F) to filter runners")
            @RequestParam(required = false) String gender,
            @Parameter(description = "Pagination parameters") Pageable pageable) {

        Page<Runner> runners = gender == null ?
                runnerService.findAll(pageable) : runnerService.findByGender(gender, pageable);

        Page<RunnerSummaryDto> runnerDtos = runners.map(runner -> {
            RunnerSummaryDto dto = runnerDtoAssembler.toSummaryDto(runner);
            return dto;
        });
        return ResponseEntity.ok(runnerDtos);
    }
    
    @Operation(
        summary = "Get a runner by ID",
        description = "Retrieves a specific runner by their ID with all details"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Runner found"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Runner not found",
            content = @Content
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<RunnerSummaryDto> getRunnerById(@PathVariable Long id) {
        Runner runner = runnerService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Runner", "id", id));
        
        RunnerSummaryDto runnerDto = runnerDtoAssembler.toSummaryDto(runner);
        return ResponseEntity.ok(runnerDto);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Create a new runner",
        description = "Creates a new runner with the provided information"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Runner created successfully"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content(schema = @Schema(type = "string", example = "Invalid input data"))
        )
    })
    @PostMapping
    public ResponseEntity<RunnerSummaryDto> createRunner(
            @Valid @RequestBody RunnerCreateDto createDto) {
        Runner runner = convertToRunner(createDto);
        Runner savedRunner = runnerService.save(runner);
        RunnerSummaryDto runnerDto = runnerDtoAssembler.toSummaryDto(savedRunner);
        return ResponseEntity.status(201).body(runnerDto);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Update a runner",
        description = "Updates an existing runner's information"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Runner updated successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Runner not found",
            content = @Content(schema = @Schema(type = "string", example = "Runner not found with id: 1"))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content(schema = @Schema(type = "string", example = "Invalid input data"))
        )
    })
    @PutMapping("/{id}")
    public ResponseEntity<RunnerSummaryDto> updateRunner(
            @PathVariable Long id,
            @Valid @RequestBody RunnerCreateDto updateDto) {
        Runner existingRunner = runnerService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Runner", "id", id));
        
        Runner runner = convertToRunner(updateDto);
        runner.setId(id);
        Runner updatedRunner = runnerService.save(runner);
        RunnerSummaryDto runnerDto = runnerDtoAssembler.toSummaryDto(updatedRunner);
        
        return ResponseEntity.ok(runnerDto);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Partially update a runner",
        description = "Updates specific fields of an existing runner"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Runner updated successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Runner not found",
            content = @Content(schema = @Schema(type = "string", example = "Runner not found with id: 1"))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content(schema = @Schema(type = "string", example = "Invalid input data"))
        )
    })
    @PatchMapping("/{id}")
    public ResponseEntity<RunnerSummaryDto> patchRunner(
            @PathVariable Long id,
            @Valid @RequestBody RunnerPatchDto patchDto) {
        Runner existingRunner = runnerService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Runner", "id", id));
        
        updateRunnerFromPatch(existingRunner, patchDto);
        Runner updatedRunner = runnerService.save(existingRunner);
        RunnerSummaryDto runnerDto = runnerDtoAssembler.toSummaryDto(updatedRunner);
        
        return ResponseEntity.ok(runnerDto);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Delete a runner",
        description = "Deletes an existing runner"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Runner deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Runner not found",
            content = @Content(schema = @Schema(type = "string", example = "Runner not found with id: 1"))
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRunner(@PathVariable Long id) {
        if (!runnerService.findById(id).isPresent()) {
            throw new ResourceNotFoundException("Runner", "id", id);
        }
        
        runnerService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    // Helper methods for DTO conversion

    private Runner convertToRunner(RunnerCreateDto dto) {
        Runner runner = new Runner();
        runner.setName(dto.getName());
        runner.setGender(dto.getGender());
        return runner;
    }
    
    private void updateRunnerFromPatch(Runner runner, RunnerPatchDto patchDto) {
        if (patchDto.getName() != null) {
            runner.setName(patchDto.getName());
        }
        if (patchDto.getGender() != null) {
            runner.setGender(patchDto.getGender());
        }
    }
}