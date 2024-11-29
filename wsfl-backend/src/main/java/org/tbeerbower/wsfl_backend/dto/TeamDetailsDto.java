package org.tbeerbower.wsfl_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Detailed representation of a team")
public class TeamDetailsDto {
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

    @Schema(description = "Summary of the league this team belongs to")
    private LeagueSummaryDto league;

    @Schema(description = "Summary of the team owner/manager")
    private UserSummaryDto owner;

    public TeamDetailsDto(Long id, String name, Integer wins, Integer losses, 
                         Integer ties, Integer totalScore, LeagueSummaryDto league,
                         UserSummaryDto owner) {
        this.id = id;
        this.name = name;
        this.wins = wins;
        this.losses = losses;
        this.ties = ties;
        this.totalScore = totalScore;
        this.league = league;
        this.owner = owner;
    }

    // Getters only for immutability
    public Long getId() { return id; }
    public String getName() { return name; }
    public Integer getWins() { return wins; }
    public Integer getLosses() { return losses; }
    public Integer getTies() { return ties; }
    public Integer getTotalScore() { return totalScore; }
    public LeagueSummaryDto getLeague() { return league; }
    public UserSummaryDto getOwner() { return owner; }
}