package org.tbeerbower.wsfl_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.tbeerbower.wsfl_backend.api.WsflResponse;
import org.tbeerbower.wsfl_backend.assembler.DraftDtoAssembler;
import org.tbeerbower.wsfl_backend.dto.DraftCreateDto;
import org.tbeerbower.wsfl_backend.dto.DraftSummaryDto;
import org.tbeerbower.wsfl_backend.exception.ResourceNotFoundException;
import org.tbeerbower.wsfl_backend.model.Draft;
import org.tbeerbower.wsfl_backend.service.DraftService;
import org.tbeerbower.wsfl_backend.service.LeagueService;

@Tag(name = "Draft", description = "Draft management APIs for league player selection")
@RestController
@RequestMapping("/api/drafts")
public class DraftController extends BaseController {
    
    private final DraftService draftService;
    private final LeagueService leagueService;
    private final DraftDtoAssembler draftDtoAssembler;
    
    @Autowired
    public DraftController(DraftService draftService,
                         LeagueService leagueService,
                         DraftDtoAssembler draftDtoAssembler) {
        this.draftService = draftService;
        this.leagueService = leagueService;
        this.draftDtoAssembler = draftDtoAssembler;
    }

    @Operation(summary = "Get all drafts", description = "Retrieves a paginated list of all drafts in the system, with optional filtering by league and season")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved drafts"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping
    public ResponseEntity<WsflResponse<Page<DraftSummaryDto>>> getAllDrafts(
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
        return ok(draftDtos);
    }

    @Operation(summary = "Get draft by ID", description = "Retrieves detailed information about a specific draft")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Draft found"),
        @ApiResponse(responseCode = "404", description = "Draft not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<WsflResponse<DraftSummaryDto>> getDraft(
            @Parameter(description = "ID of the draft to retrieve", required = true)
            @PathVariable Long id) {
        
        Draft draft = draftService.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Draft not found with id: " + id));
            
        return ok(draftDtoAssembler.toModel(draft));
    }

    @Operation(summary = "Create new draft", description = "Creates a new draft for a league's season")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Draft created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "League not found"),
        @ApiResponse(responseCode = "403", description = "Not authorized to create drafts")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WsflResponse<DraftSummaryDto>> createDraft(
            @Parameter(description = "Draft creation data", required = true)
            @Valid @RequestBody DraftCreateDto createDto) {
        
        if (!leagueService.existsById(createDto.getLeagueId())) {
            throw new ResourceNotFoundException("League not found with id: " + createDto.getLeagueId());
        }
        
        Draft draft = draftService.create(createDto);
        return created(draftDtoAssembler.toModel(draft));
    }

    @Operation(summary = "Start draft", description = "Initiates the draft process for a specific draft")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Draft started successfully"),
        @ApiResponse(responseCode = "404", description = "Draft not found"),
        @ApiResponse(responseCode = "403", description = "Not authorized to start drafts")
    })
    @PostMapping("/{id}/start")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WsflResponse<DraftSummaryDto>> startDraft(
            @Parameter(description = "ID of the draft to start", required = true)
            @PathVariable Long id) {
        
        Draft draft = draftService.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Draft not found with id: " + id));
        
        draft = draftService.startDraft(draft);
        return ok(draftDtoAssembler.toModel(draft));
    }

    @Operation(summary = "End draft", description = "Completes the draft process for a specific draft")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Draft ended successfully"),
        @ApiResponse(responseCode = "404", description = "Draft not found"),
        @ApiResponse(responseCode = "403", description = "Not authorized to end drafts")
    })
    @PostMapping("/{id}/end")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WsflResponse<DraftSummaryDto>> endDraft(
            @Parameter(description = "ID of the draft to end", required = true)
            @PathVariable Long id) {
        
        Draft draft = draftService.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Draft not found with id: " + id));
        
        draft = draftService.endDraft(draft);
        return ok(draftDtoAssembler.toModel(draft));
    }
}