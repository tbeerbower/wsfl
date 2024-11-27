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
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.tbeerbower.wsfl_backend.api.WsflResponse;
import org.tbeerbower.wsfl_backend.dto.*;
import org.tbeerbower.wsfl_backend.exception.ResourceNotFoundException;
import org.tbeerbower.wsfl_backend.model.League;
import org.tbeerbower.wsfl_backend.model.Runner;
import org.tbeerbower.wsfl_backend.model.Team;
import org.tbeerbower.wsfl_backend.service.LeagueService;
import org.tbeerbower.wsfl_backend.service.TeamService;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Team", description = "Team management APIs for managing teams and their rosters")
@RestController
@RequestMapping("/api/teams")
public class TeamController extends BaseController {

    private final TeamService teamService;
    private final LeagueService leagueService;

    @Autowired
    public TeamController(TeamService teamService, LeagueService leagueService) {
        this.teamService = teamService;
        this.leagueService = leagueService;
    }

    @Operation(
        summary = "Get all teams",
        description = "Retrieves a paginated list of all teams in the system"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved teams",
            content = @Content(schema = @Schema(implementation = TeamSummaryDto.class))
        )
    })
    @GetMapping
    public ResponseEntity<WsflResponse<Page<TeamSummaryDto>>> getAllTeams(
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        Page<Team> teams = teamService.findAll(pageable);
        Page<TeamSummaryDto> teamDtos = teams.map(team -> convertToTeamSummaryDto(team));
        return ResponseEntity.ok(new WsflResponse<>(teamDtos));
    }

    @Operation(
        summary = "Get team by ID",
        description = "Retrieves detailed information about a specific team"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Team found",
            content = @Content(schema = @Schema(implementation = TeamDetailsDto.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Team not found",
            content = @Content(schema = @Schema(type = "string", example = "Team not found with id: 123"))
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<WsflResponse<TeamDetailsDto>> getTeamById(
            @Parameter(description = "ID of the team to retrieve", required = true)
            @PathVariable Long id) {
        Team team = teamService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", id));

        TeamDetailsDto teamDto = convertToTeamDetailsDto(team);
        List<Link> links = List.of(
            linkTo(methodOn(TeamController.class).getTeamRunners(id)).withRel("runners"),
            linkTo(methodOn(TeamController.class).getTeamById(id)).withSelfRel()
        );

        return ok(teamDto, links);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Create new team",
        description = "Creates a new team in a specific league"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Team created successfully",
            content = @Content(schema = @Schema(implementation = TeamDetailsDto.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content(schema = @Schema(type = "string"))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "League not found",
            content = @Content(schema = @Schema(type = "string"))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Not authorized to create teams",
            content = @Content(schema = @Schema(type = "string"))
        )
    })
    @PostMapping
    public ResponseEntity<WsflResponse<TeamDetailsDto>> createTeam(
            @Parameter(description = "Team creation details", required = true)
            @Valid @RequestBody TeamCreateDto createDto) {
        League league = leagueService.findById(createDto.getLeagueId())
                .orElseThrow(() -> new ResourceNotFoundException("League", "id", createDto.getLeagueId()));

        Team team = new Team();
        team.setName(createDto.getName());
        team.setLeague(league);

        Team savedTeam = teamService.save(team);
        return ResponseEntity.ok(new WsflResponse<>(convertToTeamDetailsDto(savedTeam)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Update team",
        description = "Updates an existing team's information"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Team updated successfully",
            content = @Content(schema = @Schema(implementation = TeamDetailsDto.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Team not found",
            content = @Content(schema = @Schema(type = "string"))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Not authorized to update teams",
            content = @Content(schema = @Schema(type = "string"))
        )
    })
    @PutMapping("/{id}")
    public ResponseEntity<WsflResponse<TeamDetailsDto>> updateTeam(
            @Parameter(description = "ID of the team to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated team details", required = true)
            @Valid @RequestBody TeamUpdateDto updateDto) {
        Team team = teamService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", id));

        team.setName(updateDto.getName());
        if (updateDto.getLeagueId() != null) {
            League newLeague = leagueService.findById(updateDto.getLeagueId())
                    .orElseThrow(() -> new ResourceNotFoundException("League", "id", updateDto.getLeagueId()));
            team.setLeague(newLeague);
        }

        Team updatedTeam = teamService.save(team);
        return ResponseEntity.ok(new WsflResponse<>(convertToTeamDetailsDto(updatedTeam)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Delete team",
        description = "Deletes an existing team and its roster"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Team deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Team not found",
            content = @Content(schema = @Schema(type = "string"))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Not authorized to delete teams",
            content = @Content(schema = @Schema(type = "string"))
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<WsflResponse<Void>> deleteTeam(
            @Parameter(description = "ID of the team to delete", required = true)
            @PathVariable Long id) {
        if (!teamService.existsById(id)) {
            throw new ResourceNotFoundException("Team", "id", id);
        }
        teamService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Get team runners",
        description = "Retrieves all runners on a specific team's roster"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved runners",
            content = @Content(schema = @Schema(implementation = RunnerSummaryDto.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Team not found",
            content = @Content(schema = @Schema(type = "string"))
        )
    })
    @GetMapping("/{id}/runners")
    public ResponseEntity<WsflResponse<List<RunnerSummaryDto>>> getTeamRunners(
            @Parameter(description = "ID of the team", required = true)
            @PathVariable Long id) {
        Team team = teamService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", id));

        List<RunnerSummaryDto> runners = team.getRunners().stream()
                .map(this::convertToRunnerSummaryDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new WsflResponse<>(runners));
    }

    @Operation(
        summary = "Get teams by league",
        description = "Retrieves all teams belonging to a specific league"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved teams",
            content = @Content(schema = @Schema(implementation = List.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "League not found",
            content = @Content(schema = @Schema(type = "string", example = "League not found with id: 123"))
        )
    })
    @GetMapping("/league/{leagueId}")
    public ResponseEntity<WsflResponse<List<TeamSummaryDto>>> getTeamsByLeague(
            @Parameter(description = "ID of the league", required = true)
            @PathVariable Long leagueId) {
        League league = leagueService.findById(leagueId)
                .orElseThrow(() -> new ResourceNotFoundException("League", "id", leagueId));
                
        List<TeamSummaryDto> teamDtos = league.getTeams().stream()
                .map(this::convertToTeamSummaryDto)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(new WsflResponse<>(teamDtos));
    }

    @Operation(
        summary = "Get league standings",
        description = "Retrieves teams in a league ordered by wins and total score"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved standings",
            content = @Content(schema = @Schema(implementation = TeamDetailsDto.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "League not found",
            content = @Content(schema = @Schema(type = "string"))
        )
    })
    @GetMapping("/standings/{leagueId}")
    public ResponseEntity<WsflResponse<List<TeamDetailsDto>>> getLeagueStandings(
            @Parameter(description = "ID of the league", required = true)
            @PathVariable Long leagueId) {
        League league = leagueService.findById(leagueId)
                .orElseThrow(() -> new ResourceNotFoundException("League", "id", leagueId));

        List<TeamDetailsDto> standings = teamService.getLeagueStandings(league).stream()
                .map(this::convertToTeamDetailsDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new WsflResponse<>(standings));
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

    private TeamDetailsDto convertToTeamDetailsDto(Team team) {
        List<RunnerSummaryDto> runnerDtos = team.getRunners().stream()
                .map(this::convertToRunnerSummaryDto)
                .collect(Collectors.toList());

        League league = team.getLeague();
        LeagueSummaryDto leagueDto = new LeagueSummaryDto(
            league.getId(),
            league.getName(),
            league.getSeason()
        );

        UserSummaryDto ownerDto = new UserSummaryDto(
            team.getOwner().getId(),
            team.getOwner().getEmail(),
            team.getOwner().getName()
        );

        TeamDetailsDto teamDto = new TeamDetailsDto(
            team.getId(),
            team.getName(),
            team.getWins(),
            team.getLosses(),
            team.getTies(),
            team.getTotalScore(),
            leagueDto,
            ownerDto
        );
        teamDto.setRunners(runnerDtos);
        return teamDto;
    }

    private RunnerSummaryDto convertToRunnerSummaryDto(Runner runner) {
        return new RunnerSummaryDto(
            runner.getId(),
            runner.getName(),
            runner.getGender()
        );
    }
}