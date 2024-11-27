package org.tbeerbower.wsfl_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Schema(description = "Summary representation of a team")
@Relation(collectionRelation = "teams", itemRelation = "team")
public class TeamSummaryDto {
    
    @Schema(description = "Team's unique identifier", example = "1")
    private Long id;
    
    @Schema(description = "Team's name", example = "Thunderbolts")
    private String name;
    
    @Schema(description = "Number of wins", example = "10")
    private Integer wins;
    
    @Schema(description = "Number of losses", example = "5")
    private Integer losses;
    
    @Schema(description = "Number of ties", example = "2")
    private Integer ties;
    
    @Schema(description = "Total score", example = "157")
    private Integer totalScore;

    public TeamSummaryDto(Long id, String name, Integer wins, Integer losses, 
                         Integer ties, Integer totalScore) {
        this.id = id;
        this.name = name;
        this.wins = wins != null ? wins : 0;
        this.losses = losses != null ? losses : 0;
        this.ties = ties != null ? ties : 0;
        this.totalScore = totalScore != null ? totalScore : 0;
    }

    // Getters only for immutability
    public Long getId() { return id; }
    public String getName() { return name; }
    public Integer getWins() { return wins; }
    public Integer getLosses() { return losses; }
    public Integer getTies() { return ties; }
    public Integer getTotalScore() { return totalScore; }
}