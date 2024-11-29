package org.tbeerbower.wsfl_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.hateoas.RepresentationModel;
import org.tbeerbower.wsfl_backend.model.User;

import java.util.List;

@Schema(description = "Detailed representation of a league")
public class LeagueDetailsDto {
    @Schema(description = "Unique identifier of the league", example = "1")
    private Long id;

    @Schema(description = "Name of the league", example = "Fantasy Premier League 2024")
    private String name;

    @Schema(description = "The league administrator", example = "1")
    private UserSummaryDto admin;

    @Schema(description = "List of teams participating in the league")
    private List<TeamSummaryDto> teams;

    public LeagueDetailsDto(Long id, String name, UserSummaryDto admin, List<TeamSummaryDto> teams) {
        this.id = id;
        this.name = name;
        this.admin = admin;
        this.teams = teams;
    }

    // Getters only for immutability
    public Long getId() { return id; }
    public String getName() { return name; }
    public UserSummaryDto getAdmin() { return admin; }
    public List<TeamSummaryDto> getTeams() { return teams; }
}