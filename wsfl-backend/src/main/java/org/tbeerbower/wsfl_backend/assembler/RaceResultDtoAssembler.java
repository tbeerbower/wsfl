package org.tbeerbower.wsfl_backend.assembler;

import org.springframework.stereotype.Component;
import org.tbeerbower.wsfl_backend.dto.RaceResultDetailsDto;
import org.tbeerbower.wsfl_backend.dto.RaceResultSummaryDto;
import org.tbeerbower.wsfl_backend.dto.RaceSummaryDto;
import org.tbeerbower.wsfl_backend.model.RaceResult;

@Component
public class RaceResultDtoAssembler {

    private final RunnerDtoAssembler runnerDtoAssembler;

    public RaceResultDtoAssembler(RunnerDtoAssembler runnerDtoAssembler) {
        this.runnerDtoAssembler = runnerDtoAssembler;
    }

    public RaceResultSummaryDto toModel(RaceResult raceResult) {
        return new RaceResultSummaryDto(
            raceResult.getId(),
            raceResult.getGenderPlace(),
            raceResult.getOverallPlace(),
            raceResult.getTime(),
            runnerDtoAssembler.toSummaryDto(raceResult.getRunner())
        );
    }
    
    public RaceResultDetailsDto toDetailedModel(RaceResult raceResult) {
        return new RaceResultDetailsDto(
            raceResult.getId(),
            raceResult.getGenderPlace(),
            raceResult.getOverallPlace(),
            raceResult.getTime(),
            new RaceSummaryDto(
                raceResult.getRace().getId(),
                raceResult.getRace().getName(),
                raceResult.getRace().getDate(),
                raceResult.getRace().isCanceled()
            ),
            runnerDtoAssembler.toSummaryDto(raceResult.getRunner())
        );
    }
}
