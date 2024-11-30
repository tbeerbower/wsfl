package org.tbeerbower.wsfl_backend.assembler;

import org.springframework.stereotype.Component;
import org.tbeerbower.wsfl_backend.dto.LeagueSummaryDto;
import org.tbeerbower.wsfl_backend.dto.RaceDetailsDto;
import org.tbeerbower.wsfl_backend.dto.RaceResultSummaryDto;
import org.tbeerbower.wsfl_backend.dto.RaceSummaryDto;
import org.tbeerbower.wsfl_backend.dto.RunnerSummaryDto;
import org.tbeerbower.wsfl_backend.dto.SeasonSummaryDto;
import org.tbeerbower.wsfl_backend.model.Race;
import org.tbeerbower.wsfl_backend.model.RaceResult;

import java.util.stream.Collectors;

@Component
public class RaceDtoAssembler {

    private final RunnerDtoAssembler runnerDtoAssembler;

    public RaceDtoAssembler(RunnerDtoAssembler runnerDtoAssembler) {
        this.runnerDtoAssembler = runnerDtoAssembler;
    }

    public RaceSummaryDto toModel(Race race) {
        return new RaceSummaryDto(
            race.getId(),
            race.getName(),
            race.getDate(),
            race.getIsPlayoff()
        );
    }
    
    public RaceDetailsDto toDetailedModel(Race race) {

        SeasonSummaryDto seasonDto = new SeasonSummaryDto(
                race.getSeason().getId(),
                race.getSeason().getName()
        );

        return new RaceDetailsDto(
            race.getId(),
            race.getName(),
            race.getDate(),
            race.getIsPlayoff(),
                seasonDto,
            race.getResults().stream()
                .map(this::toRaceResultDto)
                .collect(Collectors.toList())
        );
    }

    private RaceResultSummaryDto toRaceResultDto(RaceResult result) {
        RunnerSummaryDto runnerDto = runnerDtoAssembler.toSummaryDto(result.getRunner());

        return new RaceResultSummaryDto(
            result.getId(),
            result.getGenderPlace(),
            result.getOverallPlace(),
            result.getTime(),
            runnerDto
        );
    }
}
