package org.tbeerbower.wsfl_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

@Schema(description = "DTO for creating a new matchup between two teams")
public class MatchupCreateDto {
    @Schema(description = "ID of the race this matchup belongs to", example = "1")
    @NotNull(message = "Race ID is required")
    private Long raceId;

    @Schema(description = "ID of the first team in the matchup", example = "1")
    @NotNull(message = "Team 1 ID is required")
    private Long team1Id;

    @Schema(description = "ID of the second team in the matchup", example = "2")
    @NotNull(message = "Team 2 ID is required")
    private Long team2Id;

    @Schema(description = "Score of the first team", example = "100", minimum = "0")
    @Min(value = 0, message = "Team 1 score must not be negative")
    private Integer team1Score;

    @Schema(description = "Score of the second team", example = "95", minimum = "0")
    @Min(value = 0, message = "Team 2 score must not be negative")
    private Integer team2Score;

    public MatchupCreateDto() {
    }

    public MatchupCreateDto(Long raceId, Long team1Id, Long team2Id, 
                          Integer team1Score, Integer team2Score) {
        this.raceId = raceId;
        this.team1Id = team1Id;
        this.team2Id = team2Id;
        this.team1Score = team1Score;
        this.team2Score = team2Score;
    }

    // Getters and Setters
    public Long getRaceId() { return raceId; }
    public void setRaceId(Long raceId) { this.raceId = raceId; }
    
    public Long getTeam1Id() { return team1Id; }
    public void setTeam1Id(Long team1Id) { this.team1Id = team1Id; }
    
    public Long getTeam2Id() { return team2Id; }
    public void setTeam2Id(Long team2Id) { this.team2Id = team2Id; }
    
    public Integer getTeam1Score() { return team1Score; }
    public void setTeam1Score(Integer team1Score) { this.team1Score = team1Score; }
    
    public Integer getTeam2Score() { return team2Score; }
    public void setTeam2Score(Integer team2Score) { this.team2Score = team2Score; }
}