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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.tbeerbower.wsfl_backend.assembler.LeagueDtoAssembler;
import org.tbeerbower.wsfl_backend.assembler.SeasonDtoAssembler;
import org.tbeerbower.wsfl_backend.dto.LeagueCreateDto;
import org.tbeerbower.wsfl_backend.dto.LeagueDetailsDto;
import org.tbeerbower.wsfl_backend.dto.LeaguePatchDto;
import org.tbeerbower.wsfl_backend.dto.LeagueSummaryDto;
import org.tbeerbower.wsfl_backend.dto.SeasonCreateDto;
import org.tbeerbower.wsfl_backend.dto.SeasonSummaryDto;
import org.tbeerbower.wsfl_backend.dto.TeamSummaryDto;
import org.tbeerbower.wsfl_backend.exception.ResourceNotFoundException;
import org.tbeerbower.wsfl_backend.model.League;
import org.tbeerbower.wsfl_backend.model.Season;
import org.tbeerbower.wsfl_backend.model.Team;
import org.tbeerbower.wsfl_backend.model.User;
import org.tbeerbower.wsfl_backend.service.LeagueService;
import org.tbeerbower.wsfl_backend.service.SeasonService;
import org.tbeerbower.wsfl_backend.service.TeamService;
import org.tbeerbower.wsfl_backend.service.UserService;

@Tag(name = "Season", description = "Season management APIs for organizing teams and seasons")
@RestController
@RequestMapping("/api/seasons")
@CrossOrigin(origins = "http://localhost:8080")
public class SeasonController {

    private final LeagueService leagueService;
    private final TeamService teamService;
    private final UserService userService;
    private final SeasonService seasonService;
    private final LeagueDtoAssembler leagueDtoAssembler;
    private final SeasonDtoAssembler seasonDtoAssembler;

    @Autowired
    public SeasonController(LeagueService leagueService, TeamService teamService, UserService userService, SeasonService seasonService, LeagueDtoAssembler leagueDtoAssembler, SeasonDtoAssembler seasonDtoAssembler) {
        this.leagueService = leagueService;
        this.teamService = teamService;
        this.userService = userService;
        this.seasonService = seasonService;
        this.leagueDtoAssembler = leagueDtoAssembler;
        this.seasonDtoAssembler = seasonDtoAssembler;
    }

    @Operation(
        summary = "Get all seasons",
        description = "Retrieves a paginated list of all seasons in the system"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved seasons"
        )
    })
    @GetMapping
    public ResponseEntity<Page<SeasonSummaryDto>> getAllSeasons(
            @Parameter(description = "Pagination parameters")
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        Page<Season> seasons = seasonService.findAll(pageable);
        Page<SeasonSummaryDto> leagueDtos = seasons.map(seasonDtoAssembler::toModel);
        return ResponseEntity.ok(leagueDtos);
    }

    @Operation(
        summary = "Get season by ID",
        description = "Retrieves detailed information about a specific season"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Season found"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Season not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "string", example = "Season not found with id: 123"))
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<SeasonSummaryDto> getSeasonById(@PathVariable Long id) {
        Season season = seasonService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Season", "id", id));

        SeasonSummaryDto seasonDto = seasonDtoAssembler.toModel(season);

        return ResponseEntity.ok(seasonDto);
    }

    @Operation(
        summary = "Create new season",
        description = "Creates a new season with the provided information"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Season created successfully"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "string", example = "Invalid input data"))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Not authorized to create season",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "string", example = "Access denied"))
        )
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SeasonSummaryDto> createSeason(
            @Parameter(description = "League creation details", required = true)
            @Valid @RequestBody SeasonCreateDto createDto) {

        Season season = new Season();
        season.setName(createDto.getName());

        Season savedSeason = seasonService.save(season);
        return ResponseEntity.status(HttpStatus.CREATED).body(seasonDtoAssembler.toModel(savedSeason));
    }
}