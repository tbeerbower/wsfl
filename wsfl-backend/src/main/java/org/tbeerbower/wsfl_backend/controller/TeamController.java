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
import org.tbeerbower.wsfl_backend.assembler.TeamDtoAssembler;
import org.tbeerbower.wsfl_backend.dto.*;
import org.tbeerbower.wsfl_backend.exception.ResourceNotFoundException;
import org.tbeerbower.wsfl_backend.model.League;
import org.tbeerbower.wsfl_backend.model.Runner;
import org.tbeerbower.wsfl_backend.model.Team;
import org.tbeerbower.wsfl_backend.model.User;
import org.tbeerbower.wsfl_backend.service.LeagueService;
import org.tbeerbower.wsfl_backend.service.TeamService;
import org.tbeerbower.wsfl_backend.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Tag(name = "Team", description = "Team management APIs for managing teams and their rosters")
@RestController
@RequestMapping("/api/teams")
@CrossOrigin(origins = "http://localhost:8080")
public class TeamController  {

    private final TeamService teamService;
    private final LeagueService leagueService;
    private final UserService userService;

    private final TeamDtoAssembler teamDtoAssembler;


    @Autowired
    public TeamController(TeamService teamService, LeagueService leagueService, UserService userService, TeamDtoAssembler teamDtoAssembler) {
        this.teamService = teamService;
        this.leagueService = leagueService;
        this.userService = userService;
        this.teamDtoAssembler = teamDtoAssembler;
    }

    @Operation(
        summary = "Get all teams",
        description = "Retrieves a paginated list of all teams in the system"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved teams"
        )
    })
    @GetMapping
    public ResponseEntity<Page<TeamDetailsDto>> getAllTeams(
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        Page<Team> teams = teamService.findAll(pageable);
        Page<TeamDetailsDto> teamDtos = teams.map(team -> teamDtoAssembler.toDetailedModel(team));
        return ResponseEntity.ok(teamDtos);
    }

    @Operation(
        summary = "Get team by ID",
        description = "Retrieves detailed information about a specific team"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Team found"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Team not found",
            content = @Content(schema = @Schema(type = "string", example = "Team not found with id: 123"))
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<TeamDetailsDto> getTeamById(
            @Parameter(description = "ID of the team to retrieve", required = true)
            @PathVariable Long id) {
        Team team = teamService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team", "id", id));

        TeamDetailsDto teamDto = teamDtoAssembler.toDetailedModel(team);

        return ResponseEntity.ok(teamDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Create new team",
        description = "Creates a new team in a specific league"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Team created successfully"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content(schema = @Schema(type = "string", example = "Invalid input data"))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "League not found",
            content = @Content(schema = @Schema(type = "string", example = "League not found with id: 1"))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Not authorized to create teams",
            content = @Content(schema = @Schema(type = "string", example = "Access denied"))
        )
    })
    @PostMapping
    public ResponseEntity<TeamDetailsDto> createTeam(
            @Parameter(description = "Team creation details", required = true)
            @Valid @RequestBody TeamCreateDto createDto) {
        League league = leagueService.findById(createDto.getLeagueId())
                .orElseThrow(() -> new ResourceNotFoundException("League", "id", createDto.getLeagueId()));

        User owner = userService.findById(createDto.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", createDto.getOwnerId()));

        Team team = new Team();
        team.setName(createDto.getName());
        team.setLeague(league);
        team.setOwner(owner);

        Team savedTeam = teamService.save(team);
        return ResponseEntity.status(HttpStatus.CREATED).body(teamDtoAssembler.toDetailedModel(savedTeam));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Update team",
        description = "Updates an existing team's information"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Team updated successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Team not found",
            content = @Content(schema = @Schema(type = "string", example = "Team not found with id: 123"))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Not authorized to update teams",
            content = @Content(schema = @Schema(type = "string", example = "Access denied"))
        )
    })
    @PutMapping("/{id}")
    public ResponseEntity<TeamDetailsDto> updateTeam(
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
        return ResponseEntity.ok(teamDtoAssembler.toDetailedModel(updatedTeam));
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
            content = @Content(schema = @Schema(type = "string", example = "Team not found with id: 123"))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Not authorized to delete teams",
            content = @Content(schema = @Schema(type = "string", example = "Access denied"))
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(
            @Parameter(description = "ID of the team to delete", required = true)
            @PathVariable Long id) {
        if (!teamService.existsById(id)) {
            throw new ResourceNotFoundException("Team", "id", id);
        }
        teamService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}