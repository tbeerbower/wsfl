package org.tbeerbower.wsfl_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.tbeerbower.wsfl_backend.assembler.DraftDtoAssembler;
import org.tbeerbower.wsfl_backend.assembler.DraftPickDtoAssembler;
import org.tbeerbower.wsfl_backend.assembler.RunnerDtoAssembler;
import org.tbeerbower.wsfl_backend.dto.DraftCreateDto;
import org.tbeerbower.wsfl_backend.dto.DraftPatchDto;
import org.tbeerbower.wsfl_backend.dto.DraftPickCreateDto;
import org.tbeerbower.wsfl_backend.dto.DraftPickSummaryDto;
import org.tbeerbower.wsfl_backend.dto.DraftSummaryDto;
import org.tbeerbower.wsfl_backend.dto.DraftUpdateDto;
import org.tbeerbower.wsfl_backend.dto.RunnerSummaryDto;
import org.tbeerbower.wsfl_backend.exception.ResourceNotFoundException;
import org.tbeerbower.wsfl_backend.model.Draft;
import org.tbeerbower.wsfl_backend.model.DraftPick;
import org.tbeerbower.wsfl_backend.model.Runner;
import org.tbeerbower.wsfl_backend.service.DraftService;
import org.tbeerbower.wsfl_backend.service.LeagueService;
import org.tbeerbower.wsfl_backend.service.RunnerService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Tag(name = "Draft", description = "Draft management APIs for league player selection")
@RestController
@RequestMapping("/api/drafts")
@CrossOrigin(origins = "http://localhost:8080")
@PreAuthorize("isAuthenticated()")
public class DraftController  {
    
    private final DraftService draftService;
    private final LeagueService leagueService;
    private final RunnerService runnerService;
    private final DraftDtoAssembler draftDtoAssembler;
    private final DraftPickDtoAssembler draftPickDtoAssembler;
    private final RunnerDtoAssembler runnerDtoAssembler;
    
    @Autowired
    public DraftController(DraftService draftService,
                           LeagueService leagueService, RunnerService runnerService,
                           DraftDtoAssembler draftDtoAssembler,
                           DraftPickDtoAssembler draftPickDtoAssembler, RunnerDtoAssembler runnerDtoAssembler) {
        this.draftService = draftService;
        this.leagueService = leagueService;
        this.runnerService = runnerService;
        this.draftDtoAssembler = draftDtoAssembler;
        this.draftPickDtoAssembler = draftPickDtoAssembler;
        this.runnerDtoAssembler = runnerDtoAssembler;
    }

    @Operation(summary = "Get all drafts", description = "Retrieves a paginated list of all drafts in the system, with optional filtering by league and season")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved drafts"),
        @ApiResponse(responseCode = "403", description = "Access denied", content = @Content(schema = @Schema(type = "string", example = "Access denied")))
    })
    @GetMapping
    public ResponseEntity<Page<DraftSummaryDto>> getAllDrafts(
            @Parameter(description = "League ID to filter drafts")
            @RequestParam(required = false) Long leagueId,
            @Parameter(description = "Season number to filter drafts")
            @RequestParam(required = false) Integer season,
            @Parameter(description = "Pagination parameters")
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        
        Page<Draft> drafts;
        if (leagueId != null && season != null) {
            drafts = draftService.findByLeagueAndSeason(leagueId, season, pageable);
        } else if (leagueId != null) {
            drafts = draftService.findByLeague(leagueId, pageable);
        } else {
            drafts = draftService.findAll(pageable);
        }
        
        Page<DraftSummaryDto> draftDtos = drafts.map(draft -> draftDtoAssembler.toModel(draft));
        return ResponseEntity.ok(draftDtos);
    }

    @Operation(summary = "Get draft by ID", description = "Retrieves detailed information about a specific draft")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Draft found"),
        @ApiResponse(responseCode = "404", description = "Draft not found",  content = @Content(schema = @Schema(type = "string", example = "Draft not found with id: 1")))
    })
    @GetMapping("/{id}")
    public ResponseEntity<DraftSummaryDto> getDraft(
            @Parameter(description = "ID of the draft to retrieve", required = true)
            @PathVariable Long id) {
        
        Draft draft = draftService.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Draft not found with id: " + id));
            
        return ResponseEntity.ok(draftDtoAssembler.toModel(draft));
    }

    @Operation(summary = "Create new draft", description = "Creates a new draft for a league's season")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Draft created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input",  content = @Content(schema = @Schema(type = "string", example = "Invalid input data"))),
        @ApiResponse(responseCode = "404", description = "League not found", content = @Content(schema = @Schema(type = "string", example = "League not found with id: 1"))),
        @ApiResponse(responseCode = "403", description = "Not authorized to create drafts", content = @Content(schema = @Schema(type = "string", example = "Access denied")))
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DraftSummaryDto> createDraft(
            @Parameter(description = "Draft creation data", required = true)
            @Valid @RequestBody DraftCreateDto createDto) {
        
        if (!leagueService.existsById(createDto.getLeagueId())) {
            throw new ResourceNotFoundException("League not found with id: " + createDto.getLeagueId());
        }
        
        Draft draft = draftService.create(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(draftDtoAssembler.toModel(draft));
    }

    @Operation(summary = "Update draft", description = "Update a specific draft")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Draft updated"),
            @ApiResponse(responseCode = "404", description = "Draft not found", content = @Content(schema = @Schema(type = "string", example = "Draft not found with id: 1"))),
            @ApiResponse(responseCode = "403", description = "Not authorized to update drafts", content = @Content(schema = @Schema(type = "string", example = "Access denied")))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DraftSummaryDto> updateDraft(
            @Parameter(description = "ID of the draft to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "Draft update data", required = true)
            @Valid @RequestBody DraftUpdateDto updateDto) {

        Draft draft = draftService.update(id, updateDto);
        return ResponseEntity.ok(draftDtoAssembler.toModel(draft));
    }

    @Operation(summary = "Patch draft", description = "Patch a specific draft")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Draft patched"),
            @ApiResponse(responseCode = "404", description = "Draft not found", content = @Content(schema = @Schema(type = "string", example = "Draft not found with id: 1"))),
            @ApiResponse(responseCode = "403", description = "Not authorized to patch drafts", content = @Content(schema = @Schema(type = "string", example = "Access denied")))
    })
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DraftSummaryDto> patchDraft(
            @Parameter(description = "ID of the draft to patch", required = true)
            @PathVariable Long id,
            @Parameter(description = "Draft patch data", required = true)
            @Valid @RequestBody DraftPatchDto patchDto) {

        Draft draft = draftService.patch(id, patchDto);
        return ResponseEntity.ok(draftDtoAssembler.toModel(draft));
    }

    @Operation(
        summary = "Get picks for a draft",
        description = "Retrieves a paginated list of all picks for a specific draft"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved draft picks",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = DraftPickSummaryDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Draft not found"
        )
    })
    @GetMapping("/{id}/picks")
    public ResponseEntity<Page<DraftPickSummaryDto>> getDraftPicks(
            @Parameter(description = "ID of the draft to get picks for")
            @PathVariable Long id,
            @Parameter(description = "Team ID to filter draft picks")
            @RequestParam(required = false) Long teamId,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        
        Draft draft = draftService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Draft", "id", id));
        
        Page<DraftPick> draftPicks = draftService.findDraftPicksByDraft(draft, teamId, pageable);
        Page<DraftPickSummaryDto> draftPickDtos = draftPicks.map(draftPick -> draftPickDtoAssembler.toModel(draftPick));

        return ResponseEntity.ok(draftPickDtos);
    }

    @Operation(summary = "Create new draft pick", description = "Makes a new pick in a draft")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Draft pick created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input",  content = @Content(schema = @Schema(type = "string", example = "Invalid input data"))),
            @ApiResponse(responseCode = "403", description = "Not authorized to create draft picks", content = @Content(schema = @Schema(type = "string", example = "Access denied")))
    })
    @PostMapping("/{id}/picks")
    public ResponseEntity<DraftSummaryDto> createDraftPick(
            @Parameter(description = "ID of the draft to create pick for")
            @PathVariable Long id,
            @Parameter(description = "Draft creation data", required = true)
            @Valid @RequestBody DraftPickCreateDto createDto) {

        Draft draft = draftService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Draft", "id", id));

        draft = draftService.makePick(draft, createDto.getRunnerId());
        return ResponseEntity.status(HttpStatus.CREATED).body(draftDtoAssembler.toModel(draft));
    }

    @Operation(
            summary = "Get runners for a draft",
            description = "Retrieves a paginated list of all runners for a specific draft"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved draft runners",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DraftPickSummaryDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Draft not found"
            )
    })
    @GetMapping("/{id}/runners")
    public ResponseEntity<Page<RunnerSummaryDto>> getDraftRunners(
            @Parameter(description = "ID of the draft to get picks for")
            @PathVariable Long id,
            @Parameter(description = "Team ID to filter draft runners")
            @RequestParam(required = false) Long teamId,
            @Parameter(description = "Status to filter runners (selected, available)")
            @RequestParam(defaultValue = "selected") String status,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {

        Draft draft = draftService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Draft", "id", id));

        Set<Long> draftedRunnerIds = teamId == null ?
                draft.getPicks().stream()
                .map(DraftPick::getRunner)
                .map(Runner::getId)
                .collect(Collectors.toSet()) :
                draft.getPicks().stream()
                .filter(pick -> pick.getTeam().getId().equals(teamId))
                .map(DraftPick::getRunner)
                .map(Runner::getId)
                .collect(Collectors.toSet());

        draftedRunnerIds.add(-1L); // Add a dummy ID to prevent empty set

        Page<Runner> runners = status.equalsIgnoreCase("available") ?
                runnerService.findAllIdNotIn(draftedRunnerIds, pageable) :
                runnerService.findAllIdIn(draftedRunnerIds, pageable);

        return ResponseEntity.ok(runners.map(runnerDtoAssembler::toSummaryDto));
    }
}