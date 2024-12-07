package org.tbeerbower.wsfl_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.hateoas.server.core.Relation;

@Schema(description = "Detailed information about a matchup")
@Relation(collectionRelation = "matchups", itemRelation = "matchup")
public class UserMatchupDetailsDto {
    @Schema(description = "Unique identifier of the matchup")
    private Long id;

    @Schema(description = "Race in which the matchup takes place")
    private RaceSummaryDto race;

    @Schema(description = "First team in the matchup")
    private UserTeamDetailsDto team1;

    @Schema(description = "Second team in the matchup")
    private UserTeamDetailsDto team2;

    @Schema(description = "Score of the first team")
    private Integer team1Score;

    @Schema(description = "Score of the second team")
    private Integer team2Score;

    public UserMatchupDetailsDto(Long id, RaceSummaryDto race, UserTeamDetailsDto team1,
                                 UserTeamDetailsDto team2, Integer team1Score, Integer team2Score) {
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
    public UserTeamDetailsDto getTeam1() { return team1; }
    public UserTeamDetailsDto getTeam2() { return team2; }
    public Integer getTeam1Score() { return team1Score; }
    public Integer getTeam2Score() { return team2Score; }
}