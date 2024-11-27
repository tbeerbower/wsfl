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
import org.tbeerbower.wsfl_backend.dto.*;
import org.tbeerbower.wsfl_backend.exception.ResourceNotFoundException;
import org.tbeerbower.wsfl_backend.model.League;
import org.tbeerbower.wsfl_backend.model.Race;
import org.tbeerbower.wsfl_backend.model.RaceResult;
import org.tbeerbower.wsfl_backend.service.LeagueService;
import org.tbeerbower.wsfl_backend.service.RaceResultService;
import org.tbeerbower.wsfl_backend.service.RaceService;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Race", description = "Race management APIs for scheduling and managing races")
@RestController
@RequestMapping("/api/races")
public class RaceController  {

    private final RaceService raceService;
    private final RaceResultService raceResultService;
    private final LeagueService leagueService;

    @Autowired
    public RaceController(RaceService raceService, RaceResultService raceResultService, LeagueService leagueService) {
        this.raceService = raceService;
        this.raceResultService = raceResultService;
        this.leagueService = leagueService;
    }

    @Operation(
        summary = "Get all races",
        description = "Retrieves a paginated list of all races in the system"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved races",
            content = @Content(schema = @Schema(implementation = RaceSummaryDto.class))
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
            description = "Successfully retrieved race",
            content = @Content(schema = @Schema(implementation = RaceDetailsDto.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Race not found",
            content = @Content(schema = @Schema(implementation = ErrorDto.class))
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
            description = "Race created successfully",
            content = @Content(schema = @Schema(implementation = RaceDetailsDto.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content(schema = @Schema(implementation = ErrorDto.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "League not found",
            content = @Content(schema = @Schema(implementation = ErrorDto.class))
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
            description = "Race updated successfully",
            content = @Content(schema = @Schema(implementation = RaceDetailsDto.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Race or League not found",
            content = @Content(schema = @Schema(implementation = ErrorDto.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content(schema = @Schema(implementation = ErrorDto.class))
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
            content = @Content(schema = @Schema(implementation = ErrorDto.class))
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
            description = "Successfully retrieved results",
            content = @Content(schema = @Schema(implementation = RaceResultSummaryDto.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Race not found",
            content = @Content(schema = @Schema(implementation = ErrorDto.class))
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

    // Helper methods for DTO conversion
    private RaceSummaryDto convertToRaceSummaryDto(Race race) {
        return new RaceSummaryDto(
            race.getId(),
            race.getName(),
            race.getDate(),
            race.getIsPlayoff()
        );
    }

    private RaceDetailsDto convertToRaceDetailsDto(Race race) {
        List<RaceResultSummaryDto> resultDtos = race.getResults().stream()
                .map(this::convertToRaceResultSummaryDto)
                .collect(Collectors.toList());

        LeagueSummaryDto leagueDto = new LeagueSummaryDto(
            race.getLeague().getId(),
            race.getLeague().getName(),
            race.getLeague().getSeason()
        );

        return new RaceDetailsDto(
            race.getId(),
            race.getName(),
            race.getDate(),
            race.getIsPlayoff(),
            leagueDto,
            resultDtos
        );
    }

    private RaceResultSummaryDto convertToRaceResultSummaryDto(RaceResult result) {
        return new RaceResultSummaryDto(
            result.getId(),
            result.getGenderPlace(),
            new RunnerSummaryDto(
                result.getRunner().getId(),
                result.getRunner().getName(),
                result.getRunner().getGender()
            )
        );
    }
}