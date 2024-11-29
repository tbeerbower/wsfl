package org.tbeerbower.wsfl_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.hateoas.server.core.Relation;

@Schema(description = "Summary information about a draft pick")
@Relation(collectionRelation = "picks", itemRelation = "pick")
public class DraftPickSummaryDto {
    @Schema(description = "Unique identifier of the draft pick")
    private Long id;

    @Schema(description = "Pick number in the draft")
    private int pickNumber;

    @Schema(description = "Team that made the pick")
    private TeamSummaryDto team;

    @Schema(description = "Runner that was picked")
    private RunnerSummaryDto runner;

    public DraftPickSummaryDto(Long id, int pickNumber, TeamSummaryDto team, RunnerSummaryDto runner) {
        this.id = id;
        this.pickNumber = pickNumber;
        this.team = team;
        this.runner = runner;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getPickNumber() {
        return pickNumber;
    }

    public void setPickNumber(int pickNumber) {
        this.pickNumber = pickNumber;
    }

    public TeamSummaryDto getTeam() {
        return team;
    }

    public void setTeam(TeamSummaryDto team) {
        this.team = team;
    }

    public RunnerSummaryDto getRunner() {
        return runner;
    }

    public void setRunner(RunnerSummaryDto runner) {
        this.runner = runner;
    }
}
