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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.tbeerbower.wsfl_backend.assembler.MatchupDtoAssembler;
import org.tbeerbower.wsfl_backend.assembler.RaceDtoAssembler;
import org.tbeerbower.wsfl_backend.assembler.RaceResultDtoAssembler;
import org.tbeerbower.wsfl_backend.dto.*;
import org.tbeerbower.wsfl_backend.exception.ResourceNotFoundException;
import org.tbeerbower.wsfl_backend.model.League;
import org.tbeerbower.wsfl_backend.model.Matchup;
import org.tbeerbower.wsfl_backend.model.Race;
import org.tbeerbower.wsfl_backend.model.RaceResult;
import org.tbeerbower.wsfl_backend.service.LeagueService;
import org.tbeerbower.wsfl_backend.service.MatchupService;
import org.tbeerbower.wsfl_backend.service.RaceResultService;
import org.tbeerbower.wsfl_backend.service.RaceService;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Race", description = "Race management APIs for scheduling and managing races")
@RestController
@RequestMapping("/api/races")
@CrossOrigin(origins = "http://localhost:8080")
public class RaceController  {

    private final RaceService raceService;
    private final RaceResultService raceResultService;
    private final LeagueService leagueService;
    private final MatchupService matchupService;
    private final RaceDtoAssembler raceDtoAssembler;
    private final MatchupDtoAssembler matchupDtoAssembler;
    private final RaceResultDtoAssembler raceResultDtoAssembler;


    @Autowired
    public RaceController(RaceService raceService, RaceResultService raceResultService, LeagueService leagueService, MatchupService matchupService, RaceDtoAssembler raceDtoAssembler, MatchupDtoAssembler matchupDtoAssembler, RaceResultDtoAssembler raceResultDtoAssembler) {
        this.raceService = raceService;
        this.raceResultService = raceResultService;
        this.leagueService = leagueService;
        this.matchupService = matchupService;
        this.raceDtoAssembler = raceDtoAssembler;
        this.matchupDtoAssembler = matchupDtoAssembler;
        this.raceResultDtoAssembler = raceResultDtoAssembler;
    }

    @Operation(
        summary = "Get all races",
        description = "Retrieves a paginated list of all races in the system"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved races"
        )
    })
    @GetMapping
    public ResponseEntity<Page<RaceSummaryDto>> getAllRaces(
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        Page<Race> races = raceService.findAll(pageable);
        Page<RaceSummaryDto> raceDtos = races.map(this::convertToRaceSummaryDto);
        return ResponseEntity.ok(raceDtos);
    }

    @Operation(
        summary = "Get race by ID",
        description = "Retrieves detailed information about a specific race"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved race"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Race not found",
            content = @Content(schema = @Schema(type = "string", example = "Race not found with id: 1"))
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<RaceDetailsDto> getRaceById(@PathVariable Long id) {
        Race race = raceService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Race not found with id: " + id));
        return ResponseEntity.ok(convertToRaceDetailsDto(race));
    }

    @Operation(
        summary = "Create new race",
        description = "Creates a new race in the system"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Race created successfully"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content(schema = @Schema(type = "string", example = "Invalid input data"))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "League not found",
            content = @Content(schema = @Schema(type = "string", example = "League not found with id: 1"))
        )
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<RaceDetailsDto> createRace(@Valid @RequestBody RaceCreateDto raceCreateDto) {
        League league = leagueService.findById(raceCreateDto.getLeagueId())
                .orElseThrow(() -> new ResourceNotFoundException("League not found with id: " + raceCreateDto.getLeagueId()));
        
        Race race = new Race();
        race.setName(raceCreateDto.getName());
        race.setDate(raceCreateDto.getDate());
        race.setIsPlayoff(raceCreateDto.getIsPlayoff());
        race.setLeague(league);
        
        Race savedRace = raceService.save(race);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToRaceDetailsDto(savedRace));
    }

    @Operation(
        summary = "Update race",
        description = "Updates an existing race's information"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Race updated successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Race or League not found",
            content = @Content(schema = @Schema(type = "string", example = "Race not found with id: 1"))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content(schema = @Schema(type = "string", example = "Invalid input data"))
        )
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<RaceDetailsDto> updateRace(
            @PathVariable Long id,
            @Valid @RequestBody RaceUpdateDto raceUpdateDto) {
        Race race = raceService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Race not found with id: " + id));
        
        race.setName(raceUpdateDto.getName());
        race.setDate(raceUpdateDto.getDate());
        race.setIsPlayoff(raceUpdateDto.getIsPlayoff());
        
        if (raceUpdateDto.getLeagueId() != null) {
            League league = leagueService.findById(raceUpdateDto.getLeagueId())
                    .orElseThrow(() -> new ResourceNotFoundException("League not found with id: " + raceUpdateDto.getLeagueId()));
            race.setLeague(league);
        }
        
        Race updatedRace = raceService.save(race);
        return ResponseEntity.ok(convertToRaceDetailsDto(updatedRace));
    }

    @Operation(
        summary = "Delete race",
        description = "Deletes a race from the system"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Race deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Race not found",
            content = @Content(schema = @Schema(type = "string", example = "Race not found with id: 1"))
        )
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRace(@PathVariable Long id) {
        if (!raceService.existsById(id)) {
            throw new ResourceNotFoundException("Race not found with id: " + id);
        }
        raceService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Get race results",
        description = "Retrieves all results for a specific race"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved results"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Race not found",
            content = @Content(schema = @Schema(type = "string", example = "Race not found with id: 1"))
        )
    })
    @GetMapping("/{id}/results")
    public ResponseEntity<Page<RaceResultSummaryDto>> getRaceResults(
            @PathVariable Long id,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        Race race = raceService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Race not found with id: " + id));

        Page<RaceResultSummaryDto> results = raceResultService.findByRace(race, pageable)
                .map(this::convertToRaceResultSummaryDto);

        return ResponseEntity.ok(results);
    }

    @Operation(summary = "Get matchups by race", description = "Retrieves a paginated list of matchups for a specific race")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved matchups"),
            @ApiResponse(responseCode = "404", description = "Race not found", content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "string", example = "Race not found with id: 123"))),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "string", example = "Access denied")))
    })
    @GetMapping("/{id}/matchups")
    public ResponseEntity<Page<MatchupSummaryDto>> getMatchupsByRace(
            @Parameter(description = "ID of the race")
            @PathVariable Long id,
            @Parameter(description = "Page number (0-based)")
            @PageableDefault(size = 20) Pageable pageable,
            @Parameter(description = "Filter by team ID")
            @RequestParam(required = false) Long teamId) {

        Page<Matchup> matchups = teamId != null ?
                matchupService.findByRaceAndTeam(id, teamId, pageable) :
                matchupService.findByRace(id, pageable);

        Page<MatchupSummaryDto> matchupDtos = matchups.map(matchupDtoAssembler::toModel);
        return ResponseEntity.ok(matchupDtos);
    }

    // Helper methods for DTO conversion
    private RaceSummaryDto convertToRaceSummaryDto(Race race) {
        return raceDtoAssembler.toModel(race);
    }

    private RaceDetailsDto convertToRaceDetailsDto(Race race) {
        return raceDtoAssembler.toDetailedModel(race);
    }

    private RaceResultSummaryDto convertToRaceResultSummaryDto(RaceResult result) {
        return raceResultDtoAssembler.toModel(result);
    }
}