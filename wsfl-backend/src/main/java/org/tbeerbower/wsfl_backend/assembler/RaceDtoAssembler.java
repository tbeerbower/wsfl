package org.tbeerbower.wsfl_backend.assembler;

import org.springframework.stereotype.Component;
import org.tbeerbower.wsfl_backend.dto.LeagueSummaryDto;
import org.tbeerbower.wsfl_backend.dto.RaceDetailsDto;
import org.tbeerbower.wsfl_backend.dto.RaceResultSummaryDto;
import org.tbeerbower.wsfl_backend.dto.RaceSummaryDto;
import org.tbeerbower.wsfl_backend.dto.RunnerSummaryDto;
import org.tbeerbower.wsfl_backend.model.Race;
import org.tbeerbower.wsfl_backend.model.RaceResult;

import java.util.stream.Collectors;

@Component
public class RaceDtoAssembler {

    public RaceSummaryDto toModel(Race race) {
        return new RaceSummaryDto(
            race.getId(),
            race.getName(),
            race.getDate(),
            race.getIsPlayoff()
        );
    }
    
    public RaceDetailsDto toDetailedModel(Race race) {
        LeagueSummaryDto leagueDto = new LeagueSummaryDto(
            race.getLeague().getId(),
            race.getLeague().getName(),
            race.getLeague().getSeason()
        );

        return new RaceDetailsDto(
            race.getId(),
            race.getName(),
            race.getDate(),
            race.getIsPlayoff(),
            leagueDto,
            race.getResults().stream()
                .map(this::toRaceResultDto)
                .collect(Collectors.toList())
        );
    }

    private RaceResultSummaryDto toRaceResultDto(RaceResult result) {
        RunnerSummaryDto runnerDto = new RunnerSummaryDto(
            result.getRunner().getId(),
            result.getRunner().getName(),
            result.getRunner().getGender()
        );

        return new RaceResultSummaryDto(
            result.getId(),
            result.getGenderPlace(),
            runnerDto
        );
    }
}