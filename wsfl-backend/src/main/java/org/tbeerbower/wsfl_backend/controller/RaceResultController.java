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
import org.tbeerbower.wsfl_backend.assembler.RaceResultDtoAssembler;
import org.tbeerbower.wsfl_backend.dto.*;
import org.tbeerbower.wsfl_backend.exception.ResourceNotFoundException;
import org.tbeerbower.wsfl_backend.model.Race;
import org.tbeerbower.wsfl_backend.model.RaceResult;
import org.tbeerbower.wsfl_backend.model.Runner;
import org.tbeerbower.wsfl_backend.service.RaceResultService;
import org.tbeerbower.wsfl_backend.service.RaceService;
import org.tbeerbower.wsfl_backend.service.RunnerService;


@Tag(name = "Race Results", description = "APIs for managing race results and runner performance")
@RestController
@RequestMapping("/api/results")
@CrossOrigin(origins = "http://localhost:8080")
public class RaceResultController  {

    private final RaceResultService raceResultService;
    private final RaceService raceService;
    private final RunnerService runnerService;
    private final RaceResultDtoAssembler raceResultDtoAssembler;

    @Autowired
    public RaceResultController(RaceResultService raceResultService,
                                RaceService raceService,
                                RunnerService runnerService, RaceResultDtoAssembler raceResultDtoAssembler) {
        this.raceResultService = raceResultService;
        this.raceService = raceService;
        this.runnerService = runnerService;
        this.raceResultDtoAssembler = raceResultDtoAssembler;
    }

    @Operation(summary = "Get all race results", description = "Retrieves a paginated list of all race results")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved race results"),
        @ApiResponse(responseCode = "403", description = "Access denied", content = @Content(schema = @Schema(type = "string", example = "Access denied")))
    })
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<RaceResultDetailsDto>> getAllRaceResults(
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        Page<RaceResult> results = raceResultService.findAll(pageable);
        Page<RaceResultDetailsDto> dtoPage = results.map(this::convertToRaceResultDetailsDto);
        return ResponseEntity.ok(dtoPage);
    }

    @Operation(summary = "Get race result by ID", description = "Retrieves a specific race result by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved race result"),
        @ApiResponse(responseCode = "404", description = "Race result not found", content = @Content(schema = @Schema(type = "string", example = "Race result not found with id: 1"))),
        @ApiResponse(responseCode = "403", description = "Access denied", content = @Content(schema = @Schema(type = "string", example = "Access denied")))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<RaceResultDetailsDto> getRaceResult(
            @Parameter(description = "ID of the race result") @PathVariable Long id) {
        RaceResult result = raceResultService.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Race result not found with id: " + id));
        return ResponseEntity.ok(convertToRaceResultDetailsDto(result));
    }

    @Operation(summary = "Create race result", description = "Creates a new race result")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Race result created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(type = "string", example = "Invalid input"))),
        @ApiResponse(responseCode = "403", description = "Access denied", content = @Content(schema = @Schema(type = "string", example = "Access denied"))),
        @ApiResponse(responseCode = "404", description = "Race or runner not found", content = @Content(schema = @Schema(type = "string", example = "Race not found with id: 1")))
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RaceResultDetailsDto> createRaceResult(
            @Valid @RequestBody RaceResultCreateDto createDto) {
        Race race = raceService.findById(createDto.getRaceId())
            .orElseThrow(() -> new ResourceNotFoundException("Race not found with id: " + createDto.getRaceId()));
        
        Runner runner = runnerService.findById(createDto.getRunnerId())
            .orElseThrow(() -> new ResourceNotFoundException("Runner not found with id: " + createDto.getRunnerId()));

        RaceResult result = new RaceResult();
        result.setRace(race);
        result.setRunner(runner);
        result.setGenderPlace(createDto.getGenderPlace());
        result.setOverallPlace(createDto.getOverallPlace());
        result.setTime(createDto.getTime());

        RaceResult savedResult = raceResultService.save(result);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToRaceResultDetailsDto(savedResult));

    }

    @Operation(summary = "Update race result", description = "Updates an existing race result")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Race result updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(type = "string", example = "Invalid input"))),
        @ApiResponse(responseCode = "403", description = "Access denied", content = @Content(schema = @Schema(type = "string", example = "Access denied"))),
        @ApiResponse(responseCode = "404", description = "Race result not found", content = @Content(schema = @Schema(type = "string", example = "Race result not found with id: 1")))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RaceResultDetailsDto> updateRaceResult(
            @Parameter(description = "ID of the race result") @PathVariable Long id,
            @Valid @RequestBody RaceResultUpdateDto updateDto) {
        RaceResult result = raceResultService.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Race result not found with id: " + id));

        if (updateDto.getGenderPlace() != null) {
            result.setGenderPlace(updateDto.getGenderPlace());
        }
        if (updateDto.getOverallPlace() != null) {
            result.setOverallPlace(updateDto.getOverallPlace());
        }
        if (updateDto.getTime() != null) {
            result.setTime(updateDto.getTime());
        }

        RaceResult updatedResult = raceResultService.save(result);
        return ResponseEntity.ok(convertToRaceResultDetailsDto(updatedResult));
    }

    @Operation(summary = "Delete race result", description = "Deletes a race result")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Race result deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied", content = @Content(schema = @Schema(type = "string", example = "Access denied"))),
        @ApiResponse(responseCode = "404", description = "Race result not found", content = @Content(schema = @Schema(type = "string", example = "Race result not found with id: 1")))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRaceResult(
            @Parameter(description = "ID of the race result") @PathVariable Long id) {
        if (!raceResultService.existsById(id)) {
            throw new ResourceNotFoundException("Race result not found with id: " + id);
        }
        raceResultService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    private RaceResultDetailsDto convertToRaceResultDetailsDto(RaceResult result) {
        return raceResultDtoAssembler.toDetailedModel(result);
    }
}