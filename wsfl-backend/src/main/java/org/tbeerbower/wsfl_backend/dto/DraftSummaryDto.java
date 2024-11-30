package org.tbeerbower.wsfl_backend.dto;

import org.springframework.hateoas.server.core.Relation;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Summary information about a draft")
@Relation(collectionRelation = "drafts", itemRelation = "draft")
public class DraftSummaryDto {
    @Schema(description = "Unique identifier of the draft")
    private Long id;

    @Schema(description = "League associated with the draft")
    private LeagueSummaryDto league;

    @Schema(description = "Season name")
    private String name;

    @Schema(description = "Season number")
    private Integer season;

    @Schema(description = "Number of rounds in the draft")
    private Integer numberOfRounds;

    @Schema(description = "Whether the draft order snakes (reverses) each round")
    private Boolean snakeOrder;

    @Schema(description = "Start time of the draft")
    private LocalDateTime startTime;

    @Schema(description = "Whether the draft is started")
    private Boolean isStarted;

    @Schema(description = "Whether the draft is complete")
    private Boolean isComplete;
    @Schema(description = "Current round number")
    private Integer currentRound;

    @Schema(description = "Current pick number within the round")
    private Integer currentPick;

    @Schema(description = "List of team IDs in draft order")
    private List<Long> draftOrder;

    public DraftSummaryDto(Long id, LeagueSummaryDto league, String name, Integer season,
                          Integer numberOfRounds, Boolean snakeOrder, LocalDateTime startTime,
                          Boolean isStarted, Boolean isComplete,
                          Integer currentRound, Integer currentPick,
                          List<Long> draftOrder) {
        this.id = id;
        this.league = league;
        this.name = name;
        this.season = season;
        this.numberOfRounds = numberOfRounds;
        this.snakeOrder = snakeOrder;
        this.startTime = startTime;
        this.isStarted = isStarted;
        this.isComplete = isComplete;
        this.currentRound = currentRound;
        this.currentPick = currentPick;
        this.draftOrder = draftOrder;
    }

    // Getters
    public Long getId() { return id; }
    public LeagueSummaryDto getLeague() { return league; }
    public String getName() { return name; }
    public Integer getSeason() { return season; }
    public Integer getNumberOfRounds() { return numberOfRounds; }
    public Boolean getSnakeOrder() { return snakeOrder; }
    public LocalDateTime getStartTime() { return startTime; }
    public Boolean isStarted() { return isStarted; }
    public Boolean isComplete() { return isComplete; }
    public Integer getCurrentRound() { return currentRound; }
    public Integer getCurrentPick() { return currentPick; }
    public List<Long> getDraftOrder() { return draftOrder; }
}