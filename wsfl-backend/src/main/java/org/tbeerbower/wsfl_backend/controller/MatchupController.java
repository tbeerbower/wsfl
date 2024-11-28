package org.tbeerbower.wsfl_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.tbeerbower.wsfl_backend.assembler.MatchupDtoAssembler;
import org.tbeerbower.wsfl_backend.dto.MatchupCreateDto;
import org.tbeerbower.wsfl_backend.dto.MatchupDetailsDto;
import org.tbeerbower.wsfl_backend.dto.MatchupSummaryDto;
import org.tbeerbower.wsfl_backend.exception.ResourceNotFoundException;
import org.tbeerbower.wsfl_backend.model.Matchup;
import org.tbeerbower.wsfl_backend.service.MatchupService;
import org.tbeerbower.wsfl_backend.service.RaceService;
import org.tbeerbower.wsfl_backend.service.TeamService;

@RestController
@RequestMapping("/api/matchups")
public class MatchupController  {

    private final MatchupService matchupService;
    private final RaceService raceService;
    private final TeamService teamService;
    private final MatchupDtoAssembler matchupDtoAssembler;

    @Autowired
    public MatchupController(MatchupService matchupService, 
                           RaceService raceService,
                           TeamService teamService,
                           MatchupDtoAssembler matchupDtoAssembler) {
        this.matchupService = matchupService;
        this.raceService = raceService;
        this.teamService = teamService;
        this.matchupDtoAssembler = matchupDtoAssembler;
    }

    @Operation(summary = "Get all matchups", description = "Retrieves a paginated list of all matchups in the system")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved matchups"),
        @ApiResponse(responseCode = "403", description = "Access denied", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(type = "string", example = "Access denied")))
    })
    @GetMapping
    public ResponseEntity<Page<MatchupSummaryDto>> getAllMatchups(
            @Parameter(description = "Page number (0-based)")
            @PageableDefault(size = 20) Pageable pageable,
            @Parameter(description = "Filter by race ID")
            @RequestParam(required = false) Long raceId) {
        
        Page<Matchup> matchups = raceId != null ? 
            matchupService.findByRace(raceId, pageable) :
            matchupService.findAll(pageable);
            
        Page<MatchupSummaryDto> matchupDtos = matchups.map(matchupDtoAssembler::toModel);
        return ResponseEntity.ok(matchupDtos);
    }

    @Operation(summary = "Get matchups by race", description = "Retrieves a paginated list of matchups for a specific race")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved matchups"),
        @ApiResponse(responseCode = "404", description = "Race not found", content = @Content(mediaType = "application/json",
                schema = @Schema(type = "string", example = "Race not found with id: 123"))),
        @ApiResponse(responseCode = "403", description = "Access denied", content = @Content(mediaType = "application/json",
                schema = @Schema(type = "string", example = "Access denied")))
    })
    @GetMapping("/race/{raceId}")
    public ResponseEntity<Page<MatchupSummaryDto>> getMatchupsByRace(
            @Parameter(description = "ID of the race")
            @PathVariable Long raceId,
            @Parameter(description = "Page number (0-based)")
            @PageableDefault(size = 20) Pageable pageable,
            @Parameter(description = "Filter by team ID")
            @RequestParam(required = false) Long teamId) {
        
        Page<Matchup> matchups = teamId != null ?
            matchupService.findByRaceAndTeam(raceId, teamId, pageable) :
            matchupService.findByRace(raceId, pageable);
            
        Page<MatchupSummaryDto> matchupDtos = matchups.map(matchupDtoAssembler::toModel);
        return ResponseEntity.ok(matchupDtos);
    }

    @Operation(summary = "Get a matchup by ID", description = "Retrieves a specific matchup by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Matchup found"),
        @ApiResponse(responseCode = "404", description = "Matchup not found", content = @Content(mediaType = "application/json",
                schema = @Schema(type = "string", example = "Matchup not found with id: 123"))),
        @ApiResponse(responseCode = "403", description = "Access denied", content = @Content(mediaType = "application/json",
                schema = @Schema(type = "string", example = "Access denied")))
    })
    @GetMapping("/{id}")
    public ResponseEntity<MatchupDetailsDto> getMatchup(
            @Parameter(description = "ID of the matchup")
            @PathVariable Long id) {
        
        Matchup matchup = matchupService.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Matchup not found with id: " + id));
            
        return ResponseEntity.ok(matchupDtoAssembler.toDetailedModel(matchup));
    }

    @Operation(summary = "Create a new matchup", description = "Creates a new matchup in the system")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Matchup created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(mediaType = "application/json",
                schema = @Schema(type = "string", example = "Invalid input data"))),
        @ApiResponse(responseCode = "403", description = "Not authorized to create matchups", content = @Content(mediaType = "application/json",
                schema = @Schema(type = "string", example = "Access denied")))
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MatchupDetailsDto> createMatchup(
            @Parameter(description = "Matchup creation data")
            @Valid @RequestBody MatchupCreateDto createDto) {
        
        Matchup matchup = matchupService.create(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(matchupDtoAssembler.toDetailedModel(matchup));

    }

    @Operation(summary = "Update matchup scores", description = "Updates the scores for both teams in a matchup")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Scores updated successfully"),
        @ApiResponse(responseCode = "404", description = "Matchup not found", content = @Content(mediaType = "application/json",
                schema = @Schema(type = "string", example = "Matchup not found with id: 123"))),
        @ApiResponse(responseCode = "403", description = "Not authorized to update matchups", content = @Content(mediaType = "application/json",
                schema = @Schema(type = "string", example = "Access denied")))
    })
    @PatchMapping("/{id}/scores")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MatchupDetailsDto> updateMatchupScores(
            @Parameter(description = "ID of the matchup")
            @PathVariable Long id,
            @Parameter(description = "Score for team 1")
            @RequestParam Integer team1Score,
            @Parameter(description = "Score for team 2")
            @RequestParam Integer team2Score) {

        matchupService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Matchup not found with id: " + id));

        Matchup matchup = matchupService.updateScores(id, team1Score, team2Score);
        return ResponseEntity.ok(matchupDtoAssembler.toDetailedModel(matchup));
    }

    @Operation(summary = "Delete a matchup", description = "Deletes a matchup from the system")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Successfully deleted matchup"),
        @ApiResponse(responseCode = "404", description = "Matchup not found", content = @Content(mediaType = "application/json",
                schema = @Schema(type = "string", example = "Matchup not found with id: 123"))),
        @ApiResponse(responseCode = "403", description = "Not authorized to delete matchups", content = @Content(mediaType = "application/json",
                schema = @Schema(type = "string", example = "Access denied")))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMatchup(
            @Parameter(description = "ID of the matchup")
            @PathVariable Long id) {
        
        if (!matchupService.existsById(id)) {
            throw new ResourceNotFoundException("Matchup not found with id: " + id);
        }
        
        matchupService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}