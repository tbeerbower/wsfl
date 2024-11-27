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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.tbeerbower.wsfl_backend.api.WsflResponse;
import org.tbeerbower.wsfl_backend.dto.*;
import org.tbeerbower.wsfl_backend.exception.ResourceNotFoundException;
import org.tbeerbower.wsfl_backend.model.Race;
import org.tbeerbower.wsfl_backend.model.RaceResult;
import org.tbeerbower.wsfl_backend.model.Runner;
import org.tbeerbower.wsfl_backend.service.RaceResultService;
import org.tbeerbower.wsfl_backend.service.RaceService;
import org.tbeerbower.wsfl_backend.service.RunnerService;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Race Results", description = "APIs for managing race results and runner performance")
@RestController
@RequestMapping("/api/race-results")
public class RaceResultController extends BaseController {

    private final RaceResultService raceResultService;
    private final RaceService raceService;
    private final RunnerService runnerService;

    @Autowired
    public RaceResultController(RaceResultService raceResultService,
                              RaceService raceService,
                              RunnerService runnerService) {
        this.raceResultService = raceResultService;
        this.raceService = raceService;
        this.runnerService = runnerService;
    }

    @Operation(summary = "Get all race results", description = "Retrieves a paginated list of all race results")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved race results"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<WsflResponse<Page<RaceResultSummaryDto>>> getAllRaceResults(
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        Page<RaceResult> results = raceResultService.findAll(pageable);
        Page<RaceResultSummaryDto> dtoPage = results.map(this::convertToRaceResultSummaryDto);
        return ok(dtoPage);
    }

    @Operation(summary = "Get race result by ID", description = "Retrieves a specific race result by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved race result"),
        @ApiResponse(responseCode = "404", description = "Race result not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<WsflResponse<RaceResultSummaryDto>> getRaceResult(
            @Parameter(description = "ID of the race result") @PathVariable Long id) {
        RaceResult result = raceResultService.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Race result not found with id: " + id));
        return ok(convertToRaceResultSummaryDto(result));
    }

    @Operation(summary = "Create race result", description = "Creates a new race result")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Race result created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Race or runner not found")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WsflResponse<RaceResultSummaryDto>> createRaceResult(
            @Valid @RequestBody RaceResultCreateDto createDto) {
        Race race = raceService.findById(createDto.getRaceId())
            .orElseThrow(() -> new ResourceNotFoundException("Race not found with id: " + createDto.getRaceId()));
        
        Runner runner = runnerService.findById(createDto.getRunnerId())
            .orElseThrow(() -> new ResourceNotFoundException("Runner not found with id: " + createDto.getRunnerId()));

        RaceResult result = new RaceResult();
        result.setRace(race);
        result.setRunner(runner);
        result.setGenderPlace(createDto.getGenderPlace());
        result.setFinishTime(createDto.getFinishTime());

        RaceResult savedResult = raceResultService.save(result);
        return created(convertToRaceResultSummaryDto(savedResult));
    }

    @Operation(summary = "Update race result", description = "Updates an existing race result")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Race result updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Race result not found")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WsflResponse<RaceResultSummaryDto>> updateRaceResult(
            @Parameter(description = "ID of the race result") @PathVariable Long id,
            @Valid @RequestBody RaceResultUpdateDto updateDto) {
        RaceResult result = raceResultService.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Race result not found with id: " + id));

        if (updateDto.getGenderPlace() != null) {
            result.setGenderPlace(updateDto.getGenderPlace());
        }
        if (updateDto.getFinishTime() != null) {
            result.setFinishTime(updateDto.getFinishTime());
        }

        RaceResult updatedResult = raceResultService.save(result);
        return ok(convertToRaceResultSummaryDto(updatedResult));
    }

    @Operation(summary = "Delete race result", description = "Deletes a race result")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Race result deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Race result not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WsflResponse<Void>> deleteRaceResult(
            @Parameter(description = "ID of the race result") @PathVariable Long id) {
        if (!raceResultService.existsById(id)) {
            throw new ResourceNotFoundException("Race result not found with id: " + id);
        }
        raceResultService.deleteById(id);
        return noContent();
    }

    @Operation(summary = "Get results by race", description = "Retrieves all results for a specific race")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved race results"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Race not found")
    })
    @GetMapping("/race/{raceId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<WsflResponse<List<RaceResultSummaryDto>>> getResultsByRace(
            @Parameter(description = "ID of the race") @PathVariable Long raceId) {
        Race race = raceService.findById(raceId)
            .orElseThrow(() -> new ResourceNotFoundException("Race not found with id: " + raceId));
        List<RaceResult> results = raceResultService.findByRace(race);
        List<RaceResultSummaryDto> dtos = results.stream()
            .map(this::convertToRaceResultSummaryDto)
            .collect(Collectors.toList());
        return ok(dtos);
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