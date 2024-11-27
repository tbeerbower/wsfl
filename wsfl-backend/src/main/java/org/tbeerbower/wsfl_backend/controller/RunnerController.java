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
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.tbeerbower.wsfl_backend.dto.RunnerCreateDto;
import org.tbeerbower.wsfl_backend.dto.RunnerDetailsDto;
import org.tbeerbower.wsfl_backend.dto.RunnerPatchDto;
import org.tbeerbower.wsfl_backend.dto.RunnerSummaryDto;
import org.tbeerbower.wsfl_backend.dto.TeamSummaryDto;
import org.tbeerbower.wsfl_backend.api.WsflResponse;
import org.tbeerbower.wsfl_backend.exception.ResourceNotFoundException;
import org.tbeerbower.wsfl_backend.model.Runner;
import org.tbeerbower.wsfl_backend.model.Team;
import org.tbeerbower.wsfl_backend.service.RunnerService;
import org.tbeerbower.wsfl_backend.controller.TeamController;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Runner", description = "Runner management APIs")
@RestController
@RequestMapping("/api/runners")
public class RunnerController extends BaseController {
    
    private final RunnerService runnerService;
    
    public RunnerController(RunnerService runnerService) {
        this.runnerService = runnerService;
    }
    
    @Operation(
        summary = "Get all runners",
        description = "Retrieves a paginated list of all runners in the system"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved runners",
            content = @Content(mediaType = "application/json", 
                             schema = @Schema(implementation = Page.class))
        )
    })
    @GetMapping
    public ResponseEntity<WsflResponse<Page<RunnerSummaryDto>>> getAllRunners(
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        Page<Runner> runners = runnerService.findAll(pageable);
        Page<RunnerSummaryDto> runnerDtos = runners.map(runner -> {
            RunnerSummaryDto dto = convertToRunnerSummaryDto(runner);
            List<Link> links = List.of(
                linkTo(methodOn(RunnerController.class).getRunnerById(runner.getId())).withSelfRel(),
                linkTo(methodOn(RunnerController.class).getRunnersByGender(runner.getGender(), null)).withRel("runners-by-gender")
            );
            return dto;
        });
        return ok(runnerDtos);
    }
    
    @Operation(
        summary = "Get a runner by ID",
        description = "Retrieves a specific runner by their ID with all details"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Runner found",
            content = @Content(mediaType = "application/json", 
                             schema = @Schema(implementation = RunnerDetailsDto.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Runner not found",
            content = @Content
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<RunnerDetailsDto> getRunnerById(@PathVariable Long id) {
        Runner runner = runnerService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Runner", "id", id));
        
        RunnerDetailsDto runnerDto = convertToRunnerDetailsDto(runner);
        runnerDto.add(
            linkTo(methodOn(RunnerController.class).getRunnerById(id)).withSelfRel(),
            linkTo(methodOn(RunnerController.class).getRunnersByGender(runner.getGender(), null)).withRel("runners-by-gender")
        );
        
        return ResponseEntity.ok(runnerDto);
    }
    
    @Operation(
        summary = "Get runners by gender",
        description = "Retrieves a paginated list of runners of a specific gender"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved runners",
            content = @Content(mediaType = "application/json", 
                             schema = @Schema(implementation = Page.class))
        )
    })
    @GetMapping("/gender/{gender}")
    public ResponseEntity<WsflResponse<Page<RunnerSummaryDto>>> getRunnersByGender(
            @PathVariable String gender,
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        Page<Runner> runners = runnerService.findByGender(gender, pageable);
        Page<RunnerSummaryDto> runnerDtos = runners.map(runner -> {
            RunnerSummaryDto dto = convertToRunnerSummaryDto(runner);
            return dto;
        });
        
        List<Link> links = List.of(
            linkTo(methodOn(RunnerController.class).getRunnersByGender(gender, null)).withRel("self")
        );
        
        return ok(runnerDtos, links);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Create a new runner",
        description = "Creates a new runner with the provided information"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Runner created successfully",
            content = @Content(mediaType = "application/json", 
                             schema = @Schema(implementation = RunnerDetailsDto.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content
        )
    })
    @PostMapping
    public ResponseEntity<RunnerDetailsDto> createRunner(
            @Valid @RequestBody RunnerCreateDto createDto) {
        Runner runner = convertToRunner(createDto);
        Runner savedRunner = runnerService.save(runner);
        RunnerDetailsDto runnerDto = convertToRunnerDetailsDto(savedRunner);
        
        runnerDto.add(
            linkTo(methodOn(RunnerController.class).getRunnerById(savedRunner.getId())).withSelfRel(),
            linkTo(methodOn(RunnerController.class).getRunnersByGender(savedRunner.getGender(), null)).withRel("runners-by-gender")
        );
        
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
            description = "Runner updated successfully",
            content = @Content(mediaType = "application/json", 
                             schema = @Schema(implementation = RunnerDetailsDto.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Runner not found",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content
        )
    })
    @PutMapping("/{id}")
    public ResponseEntity<RunnerDetailsDto> updateRunner(
            @PathVariable Long id,
            @Valid @RequestBody RunnerCreateDto updateDto) {
        Runner existingRunner = runnerService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Runner", "id", id));
        
        Runner runner = convertToRunner(updateDto);
        runner.setId(id);
        Runner updatedRunner = runnerService.save(runner);
        RunnerDetailsDto runnerDto = convertToRunnerDetailsDto(updatedRunner);
        
        runnerDto.add(
            linkTo(methodOn(RunnerController.class).getRunnerById(id)).withSelfRel(),
            linkTo(methodOn(RunnerController.class).getRunnersByGender(updatedRunner.getGender(), null)).withRel("runners-by-gender")
        );
        
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
            description = "Runner updated successfully",
            content = @Content(mediaType = "application/json", 
                             schema = @Schema(implementation = RunnerDetailsDto.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Runner not found",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content
        )
    })
    @PatchMapping("/{id}")
    public ResponseEntity<RunnerDetailsDto> patchRunner(
            @PathVariable Long id,
            @Valid @RequestBody RunnerPatchDto patchDto) {
        Runner existingRunner = runnerService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Runner", "id", id));
        
        updateRunnerFromPatch(existingRunner, patchDto);
        Runner updatedRunner = runnerService.save(existingRunner);
        RunnerDetailsDto runnerDto = convertToRunnerDetailsDto(updatedRunner);
        
        runnerDto.add(
            linkTo(methodOn(RunnerController.class).getRunnerById(id)).withSelfRel(),
            linkTo(methodOn(RunnerController.class).getRunnersByGender(updatedRunner.getGender(), null)).withRel("runners-by-gender")
        );
        
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
            content = @Content
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
    private RunnerDetailsDto convertToRunnerDetailsDto(Runner runner) {
        List<TeamSummaryDto> teamDtos = runner.getTeams().stream()
            .map(team -> {
                TeamSummaryDto dto = new TeamSummaryDto(
                    team.getId(),
                    team.getName(),
                    team.getWins(),
                    team.getLosses(),
                    team.getTies(),
                    team.getTotalScore()
                );
                dto.add(
                    linkTo(methodOn(TeamController.class).getTeamById(team.getId())).withSelfRel()
                );
                return dto;
            })
            .collect(Collectors.toList());
            
        return new RunnerDetailsDto(
            runner.getId(),
            runner.getName(),
            runner.getGender(),
            teamDtos
        );
    }
    
    private RunnerSummaryDto convertToRunnerSummaryDto(Runner runner) {
        return new RunnerSummaryDto(
            runner.getId(),
            runner.getName(),
            runner.getGender()
        );
    }
    
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