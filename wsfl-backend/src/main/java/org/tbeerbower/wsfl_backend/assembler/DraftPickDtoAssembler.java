package org.tbeerbower.wsfl_backend.assembler;

import org.springframework.stereotype.Component;
import org.tbeerbower.wsfl_backend.dto.DraftPickSummaryDto;
import org.tbeerbower.wsfl_backend.model.DraftPick;

@Component
public class DraftPickDtoAssembler {

    private final TeamDtoAssembler teamDtoAssembler;
    private final RunnerDtoAssembler runnerDtoAssembler;

    public DraftPickDtoAssembler(TeamDtoAssembler teamDtoAssembler, RunnerDtoAssembler runnerDtoAssembler) {
        this.teamDtoAssembler = teamDtoAssembler;
        this.runnerDtoAssembler = runnerDtoAssembler;
    }

    public DraftPickSummaryDto toModel(DraftPick draftPick) {
        return new DraftPickSummaryDto(
            draftPick.getId(),
            draftPick.getPickNumber(),
            teamDtoAssembler.toModel(draftPick.getTeam()),
            runnerDtoAssembler.toSummaryDto(draftPick.getRunner()),
            draftPick.getPickTime()
        );
    }
}
