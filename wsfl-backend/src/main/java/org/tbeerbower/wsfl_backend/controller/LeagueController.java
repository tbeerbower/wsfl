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
import org.tbeerbower.wsfl_backend.assembler.LeagueDtoAssembler;
import org.tbeerbower.wsfl_backend.assembler.SeasonDtoAssembler;
import org.tbeerbower.wsfl_backend.dto.*;
import org.tbeerbower.wsfl_backend.exception.ResourceNotFoundException;
import org.tbeerbower.wsfl_backend.model.Draft;
import org.tbeerbower.wsfl_backend.model.League;
import org.tbeerbower.wsfl_backend.model.Season;
import org.tbeerbower.wsfl_backend.model.Team;
import org.tbeerbower.wsfl_backend.model.User;
import org.tbeerbower.wsfl_backend.service.LeagueService;
import org.tbeerbower.wsfl_backend.service.TeamService;
import org.tbeerbower.wsfl_backend.service.UserService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Tag(name = "League", description = "League management APIs for organizing teams and seasons")
@RestController
@RequestMapping("/api/leagues")
@CrossOrigin(origins = "http://localhost:8080")
public class LeagueController  {

    private final LeagueService leagueService;
    private final TeamService teamService;
    private final UserService userService;
    private final LeagueDtoAssembler leagueDtoAssembler;
    private final SeasonDtoAssembler seasonDtoAssembler;

    @Autowired
    public LeagueController(LeagueService leagueService, TeamService teamService, UserService userService, LeagueDtoAssembler leagueDtoAssembler, SeasonDtoAssembler seasonDtoAssembler) {
        this.leagueService = leagueService;
        this.teamService = teamService;
        this.userService = userService;
        this.leagueDtoAssembler = leagueDtoAssembler;
        this.seasonDtoAssembler = seasonDtoAssembler;
    }

    @Operation(
        summary = "Get all leagues",
        description = "Retrieves a paginated list of all leagues in the system"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved leagues"
        )
    })
    @GetMapping
    public ResponseEntity<Page<LeagueSummaryDto>> getAllLeagues(
            @Parameter(description = "Pagination parameters")
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        Page<League> leagues = leagueService.findAll(pageable);
        Page<LeagueSummaryDto> leagueDtos = leagues.map(leagueDtoAssembler::toModel);
        return ResponseEntity.ok(leagueDtos);
    }

    @Operation(
        summary = "Get league by ID",
        description = "Retrieves detailed information about a specific league"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "League found"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "League not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "string", example = "League not found with id: 123"))
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<LeagueDetailsDto> getLeagueById(@PathVariable Long id) {
        League league = leagueService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("League", "id", id));

        LeagueDetailsDto leagueDto = leagueDtoAssembler.toDetailsDto(league);

        return ResponseEntity.ok(leagueDto);
    }

    @Operation(
            summary = "Get league seasons by ID",
            description = "Retrieves summary information about a specific league's seasons"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "League seasons found"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "League not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "string", example = "League not found with id: 123"))
            )
    })
    @GetMapping("/{id}/seasons")
    public ResponseEntity<Page<SeasonDetailDto>> getLeagueSeasonById(
            @PathVariable Long id,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        League league = leagueService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("League", "id", id));

        Set<Draft> drafts = league.getDrafts();

        Set<Season> seasonSet = drafts.stream()
                .map(draft -> draft.getSeason())
                .collect(Collectors.toUnmodifiableSet());

        List<SeasonDetailDto> seasonDtos = seasonSet.stream()
                .map(seasonDtoAssembler::toDetailModel)
                .toList();

        return ResponseEntity.ok(ControllerUtils.getPaginatedEntities(seasonDtos, pageable));
    }


    @Operation(
        summary = "Create new league",
        description = "Creates a new league with the provided information"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "League created successfully"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "string", example = "Invalid input data"))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Not authorized to create leagues",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "string", example = "Access denied"))
        )
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LeagueDetailsDto> createLeague(
            @Parameter(description = "League creation details", required = true)
            @Valid @RequestBody LeagueCreateDto createDto) {
        User admin = userService.findById(createDto.getAdminId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", createDto.getAdminId()));

        League league = new League();
        league.setName(createDto.getName());
        league.setMaxTeams(createDto.getMaxTeams());
        league.setAdmin(admin);

        League savedLeague = leagueService.save(league);
        return ResponseEntity.status(HttpStatus.CREATED).body(leagueDtoAssembler.toDetailsDto(savedLeague));
    }

    @Operation(
        summary = "Update league",
        description = "Updates an existing league's information"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "League updated successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "League not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "string", example = "League not found with id: 123"))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Not authorized to update leagues",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "string", example = "Access denied"))
        )
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LeagueDetailsDto> updateLeague(
            @Parameter(description = "ID of the league to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated league details", required = true)
            @Valid @RequestBody LeagueCreateDto updateDto) {
        League league = leagueService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("League", "id", id));

        league.setName(updateDto.getName());

        League updatedLeague = leagueService.save(league);
        return ResponseEntity.ok(leagueDtoAssembler.toDetailsDto(updatedLeague));
    }

    @Operation(
        summary = "Patch league",
        description = "Partially updates specific fields of an existing league"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "League updated successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "League not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "string", example = "League not found with id: 123"))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Not authorized to update leagues",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "string", example = "Access denied"))
        )
    })
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LeagueDetailsDto> patchLeague(
            @Parameter(description = "ID of the league to patch", required = true)
            @PathVariable Long id,
            @Parameter(description = "Fields to update", required = true)
            @Valid @RequestBody LeaguePatchDto patchDto) {
        League league = leagueService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("League", "id", id));

        if (patchDto.getName() != null) {
            league.setName(patchDto.getName());
        }

        League updatedLeague = leagueService.save(league);
        return ResponseEntity.ok(leagueDtoAssembler.toDetailsDto(updatedLeague));
    }

    @Operation(
        summary = "Delete league",
        description = "Deletes an existing league and all its associated data"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "League deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "League not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "string", example = "League not found with id: 123"))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Not authorized to delete leagues",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "string", example = "Access denied"))
        )
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteLeague(
            @Parameter(description = "ID of the league to delete", required = true)
            @PathVariable Long id) {
        if (!leagueService.existsById(id)) {
            throw new ResourceNotFoundException("League", "id", id);
        }
        leagueService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Get league teams",
        description = "Retrieves all teams participating in a specific league"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved teams"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "League not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "string", example = "League not found with id: 123"))
        )
    })
    @GetMapping("/{id}/teams")
    public ResponseEntity<Page<TeamSummaryDto>> getLeagueTeams(
            @Parameter(description = "ID of the league", required = true)
            @PathVariable Long id,
            @Parameter(description = "Order teams by standings")
            @RequestParam(defaultValue = "true") Boolean standings,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        League league = leagueService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("League", "id", id));

        Page<Team> teamsPage = standings ?
                teamService.getLeagueStandings(league, pageable) : teamService.findByLeague(league, pageable);
        Page<TeamSummaryDto> teams = teamsPage.map(this::convertToTeamSummaryDto);

        return ResponseEntity.ok(teams);
    }

    // Helper methods for DTO conversion
    private TeamSummaryDto convertToTeamSummaryDto(Team team) {
        return new TeamSummaryDto(
            team.getId(),
            team.getName(),
            team.getWins(),
            team.getLosses(),
            team.getTies(),
            team.getTotalScore()
        );
    }
}