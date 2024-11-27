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
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.tbeerbower.wsfl_backend.dto.*;
import org.tbeerbower.wsfl_backend.exception.ResourceNotFoundException;
import org.tbeerbower.wsfl_backend.model.League;
import org.tbeerbower.wsfl_backend.model.Team;
import org.tbeerbower.wsfl_backend.service.LeagueService;
import org.tbeerbower.wsfl_backend.service.TeamService;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Tag(name = "League", description = "League management APIs for organizing teams and seasons")
@RestController
@RequestMapping("/api/leagues")
public class LeagueController  {

    private final LeagueService leagueService;
    private final TeamService teamService;
    private final PagedResourcesAssembler<LeagueSummaryDto> pagedResourcesAssembler;

    @Autowired
    public LeagueController(LeagueService leagueService, TeamService teamService,
                            PagedResourcesAssembler<LeagueSummaryDto> pagedResourcesAssembler) {
        this.leagueService = leagueService;
        this.teamService = teamService;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(
        summary = "Get all leagues",
        description = "Retrieves a paginated list of all leagues in the system"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved leagues",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PagedModel.class))
        )
    })
    @GetMapping
    public ResponseEntity<Page<LeagueSummaryDto>> getAllLeagues(
            @Parameter(description = "Pagination parameters")
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        Page<League> leagues = leagueService.findAll(pageable);
        Page<LeagueSummaryDto> leagueDtos = leagues.map(this::convertToLeagueSummaryDto);
        return ResponseEntity.ok(leagueDtos);
    }

    @Operation(
        summary = "Get league by ID",
        description = "Retrieves detailed information about a specific league"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "League found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = LeagueDetailsDto.class))
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

        LeagueDetailsDto leagueDto = convertToLeagueDetailsDto(league);
//        List<Link> links = List.of(
//            linkTo(methodOn(LeagueController.class).getLeagueTeams(id)).withRel("teams"),
//            linkTo(methodOn(LeagueController.class).getLeagueById(id)).withSelfRel()
//        );

        return ResponseEntity.ok(leagueDto);
    }

    @Operation(
        summary = "Create new league",
        description = "Creates a new league with the provided information"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "League created successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = LeagueDetailsDto.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "string"))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Not authorized to create leagues",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "string"))
        )
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LeagueDetailsDto> createLeague(
            @Parameter(description = "League creation details", required = true)
            @Valid @RequestBody LeagueCreateDto createDto) {
        League league = new League();
        league.setName(createDto.getName());
        league.setSeason(createDto.getSeason());

        League savedLeague = leagueService.save(league);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToLeagueDetailsDto(savedLeague));
    }

    @Operation(
        summary = "Update league",
        description = "Updates an existing league's information"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "League updated successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = LeagueDetailsDto.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "League not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "string"))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Not authorized to update leagues",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "string"))
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
        league.setSeason(updateDto.getSeason());

        League updatedLeague = leagueService.save(league);
        return ResponseEntity.ok(convertToLeagueDetailsDto(updatedLeague));
    }

    @Operation(
        summary = "Patch league",
        description = "Partially updates specific fields of an existing league"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "League updated successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = LeagueDetailsDto.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "League not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "string"))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Not authorized to update leagues",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "string"))
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
        if (patchDto.getSeason() != null) {
            league.setSeason(patchDto.getSeason());
        }

        League updatedLeague = leagueService.save(league);
        return ResponseEntity.ok(convertToLeagueDetailsDto(updatedLeague));
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
                    schema = @Schema(type = "string"))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Not authorized to delete leagues",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "string"))
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
            description = "Successfully retrieved teams",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TeamSummaryDto.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "League not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "string"))
        )
    })
    @GetMapping("/{id}/teams")
    public ResponseEntity<Page<TeamSummaryDto>> getLeagueTeams(
            @Parameter(description = "ID of the league", required = true)
            @PathVariable Long id,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        League league = leagueService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("League", "id", id));

        Page<TeamSummaryDto> teams = teamService.findByLeague(league, pageable).map(this::convertToTeamSummaryDto);

        return ResponseEntity.ok(teams);
    }

    // Helper methods for DTO conversion
    private LeagueSummaryDto convertToLeagueSummaryDto(League league) {
        return new LeagueSummaryDto(
            league.getId(),
            league.getName(),
            league.getSeason()
        );
    }

    private LeagueDetailsDto convertToLeagueDetailsDto(League league) {
        List<TeamSummaryDto> teamDtos = league.getTeams().stream()
            .map(team -> new TeamSummaryDto(
                team.getId(),
                team.getName(),
                team.getWins(),
                team.getLosses(),
                team.getTies(),
                team.getTotalScore()
            ))
            .collect(Collectors.toList());

        LeagueDetailsDto dto = new LeagueDetailsDto(
            league.getId(),
            league.getName(),
            league.getSeason(),
            league.getAdmin() != null ? league.getAdmin().getId() : null,
            teamDtos
        );

//        dto.add(linkTo(methodOn(LeagueController.class).getLeagueById(league.getId())).withSelfRel());
//        dto.add(linkTo(methodOn(LeagueController.class).getAllLeagues(Pageable.unpaged())).withRel("leagues"));
        
        return dto;
    }

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