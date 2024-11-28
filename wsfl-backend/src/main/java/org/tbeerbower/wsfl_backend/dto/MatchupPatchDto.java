package org.tbeerbower.wsfl_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO for patching a matchup between two teams")
public class MatchupPatchDto {

    @Schema(description = "Score of the first team", example = "100", minimum = "0")
    @Min(value = 0, message = "Team 1 score must not be negative")
    private Integer team1Score;

    @Schema(description = "Score of the second team", example = "95", minimum = "0")
    @Min(value = 0, message = "Team 2 score must not be negative")
    private Integer team2Score;

    public MatchupPatchDto() {
    }

    public MatchupPatchDto(Integer team1Score, Integer team2Score) {
        this.team1Score = team1Score;
        this.team2Score = team2Score;
    }

    // Getters and Setters
    public Integer getTeam1Score() { return team1Score; }
    public void setTeam1Score(Integer team1Score) { this.team1Score = team1Score; }
    
    public Integer getTeam2Score() { return team2Score; }
    public void setTeam2Score(Integer team2Score) { this.team2Score = team2Score; }
}