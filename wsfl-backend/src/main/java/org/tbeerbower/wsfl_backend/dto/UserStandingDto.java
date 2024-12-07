package org.tbeerbower.wsfl_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Detailed representation of a standing")
public class UserStandingDto {
    @Schema(description = "Team in the standing")
    private UserTeamDetailsDto team;

    @Schema(description = "Number of wins")
    private int wins;

    @Schema(description = "Number of losses")
    private int losses;

    @Schema(description = "Number of ties")
    private int ties;

    @Schema(description = "Total score")
    private int totalScore;

    public UserStandingDto(UserTeamDetailsDto team, int wins, int losses, int ties, int totalScore) {
        this.team = team;
        this.wins = wins;
        this.losses = losses;
        this.ties = ties;
        this.totalScore = totalScore;
    }

    // Getters only for immutability
    public UserTeamDetailsDto getTeam() { return team; }
    public int getWins() { return wins; }
    public int getLosses() { return losses; }
    public int getTies() { return ties; }
    public int getTotalScore() { return totalScore; }
}