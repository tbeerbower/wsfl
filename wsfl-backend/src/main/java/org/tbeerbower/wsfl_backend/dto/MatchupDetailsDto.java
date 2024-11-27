package org.tbeerbower.wsfl_backend.dto;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Detailed information about a matchup")
@Relation(collectionRelation = "matchups", itemRelation = "matchup")
public class MatchupDetailsDto {
    @Schema(description = "Unique identifier of the matchup")
    private Long id;

    @Schema(description = "Race in which the matchup takes place")
    private RaceSummaryDto race;

    @Schema(description = "First team in the matchup")
    private TeamSummaryDto team1;

    @Schema(description = "Second team in the matchup")
    private TeamSummaryDto team2;

    @Schema(description = "Score of the first team")
    private Integer team1Score;

    @Schema(description = "Score of the second team")
    private Integer team2Score;

    public MatchupDetailsDto(Long id, RaceSummaryDto race, TeamSummaryDto team1, 
                           TeamSummaryDto team2, Integer team1Score, Integer team2Score) {
        this.id = id;
        this.race = race;
        this.team1 = team1;
        this.team2 = team2;
        this.team1Score = team1Score;
        this.team2Score = team2Score;
    }

    // Getters only for immutability
    public Long getId() { return id; }
    public RaceSummaryDto getRace() { return race; }
    public TeamSummaryDto getTeam1() { return team1; }
    public TeamSummaryDto getTeam2() { return team2; }
    public Integer getTeam1Score() { return team1Score; }
    public Integer getTeam2Score() { return team2Score; }
}