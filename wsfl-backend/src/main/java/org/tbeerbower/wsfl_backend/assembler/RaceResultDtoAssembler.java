package org.tbeerbower.wsfl_backend.assembler;

import org.springframework.stereotype.Component;
import org.tbeerbower.wsfl_backend.dto.RaceResultDetailsDto;
import org.tbeerbower.wsfl_backend.dto.RaceResultSummaryDto;
import org.tbeerbower.wsfl_backend.dto.RaceSummaryDto;
import org.tbeerbower.wsfl_backend.dto.RunnerSummaryDto;
import org.tbeerbower.wsfl_backend.model.RaceResult;

import java.util.stream.Collectors;

@Component
public class RaceResultDtoAssembler {

    public RaceResultSummaryDto toModel(RaceResult raceResult) {
        return new RaceResultSummaryDto(
            raceResult.getId(),
            raceResult.getGenderPlace(),
            new RunnerSummaryDto(
                raceResult.getRunner().getId(),
                raceResult.getRunner().getName(),
                raceResult.getRunner().getGender()
            )
        );
    }
    
    public RaceResultDetailsDto toDetailedModel(RaceResult raceResult) {
        return new RaceResultDetailsDto(
            raceResult.getId(),
            raceResult.getGenderPlace(),
            raceResult.getFinishTime(),
            new RaceSummaryDto(
                raceResult.getRace().getId(),
                raceResult.getRace().getName(),
                raceResult.getRace().getDate(),
                raceResult.getRace().getIsPlayoff()
            ),
            new RunnerSummaryDto(
                raceResult.getRunner().getId(),
                raceResult.getRunner().getName(),
                raceResult.getRunner().getGender()
            )
        );
    }
}
