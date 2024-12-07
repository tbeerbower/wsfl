package org.tbeerbower.wsfl_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Detailed representation of a team")
public class UserTeamMatchupsDetailsDto {
    @Schema(description = "Unique identifier of the team", example = "1")
    private Long id;

    @Schema(description = "Name of the team", example = "Speed Demons")
    private String name;

    @Schema(description = "Number of matches won", example = "5", minimum = "0")
    private Integer wins;

    @Schema(description = "Number of matches lost", example = "3", minimum = "0")
    private Integer losses;

    @Schema(description = "Number of matches tied", example = "1", minimum = "0")
    private Integer ties;

    @Schema(description = "Total score accumulated by the team", example = "450", minimum = "0")
    private Integer totalScore;

    @Schema(description = "List of matchups for the draft")
    private List<UserMatchupDetailsDto> matchups;

    public UserTeamMatchupsDetailsDto(Long id, String name, Integer wins, Integer losses, Integer ties, Integer totalScore,
                                      List<UserMatchupDetailsDto> matchups) {
        this.id = id;
        this.name = name;
        this.wins = wins;
        this.losses = losses;
        this.ties = ties;
        this.totalScore = totalScore;
        this.matchups = matchups;
    }

    // Getters only for immutability
    public Long getId() { return id; }
    public String getName() { return name; }
    public List<UserMatchupDetailsDto> getMatchups() { return matchups; }
    public Integer getWins() { return wins; }
    public Integer getLosses() { return losses; }
    public Integer getTies() { return ties; }
    public Integer getTotalScore() { return totalScore; }

}