package org.tbeerbower.wsfl_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.hateoas.RepresentationModel;
import java.util.List;

@Schema(description = "Detailed representation of a league")
public class LeagueDetailsDto {
    @Schema(description = "Unique identifier of the league", example = "1")
    private Long id;

    @Schema(description = "Name of the league", example = "Fantasy Premier League 2024")
    private String name;

    @Schema(description = "Season year of the league", example = "2024")
    private Integer season;

    @Schema(description = "ID of the league administrator", example = "1")
    private Long adminId;

    @Schema(description = "List of teams participating in the league")
    private List<TeamSummaryDto> teams;

    public LeagueDetailsDto(Long id, String name, Integer season, Long adminId, List<TeamSummaryDto> teams) {
        this.id = id;
        this.name = name;
        this.season = season;
        this.adminId = adminId;
        this.teams = teams;
    }

    // Getters only for immutability
    public Long getId() { return id; }
    public String getName() { return name; }
    public Integer getSeason() { return season; }
    public Long getAdminId() { return adminId; }
    public List<TeamSummaryDto> getTeams() { return teams; }
}