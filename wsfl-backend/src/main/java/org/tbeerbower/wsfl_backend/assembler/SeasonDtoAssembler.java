package org.tbeerbower.wsfl_backend.assembler;

import org.springframework.stereotype.Component;
import org.tbeerbower.wsfl_backend.dto.DraftSummaryDto;
import org.tbeerbower.wsfl_backend.dto.LeagueDetailsDto;
import org.tbeerbower.wsfl_backend.dto.LeagueSummaryDto;
import org.tbeerbower.wsfl_backend.dto.RaceSummaryDto;
import org.tbeerbower.wsfl_backend.dto.SeasonDetailDto;
import org.tbeerbower.wsfl_backend.dto.SeasonSummaryDto;
import org.tbeerbower.wsfl_backend.dto.TeamSummaryDto;
import org.tbeerbower.wsfl_backend.dto.UserSummaryDto;
import org.tbeerbower.wsfl_backend.model.League;
import org.tbeerbower.wsfl_backend.model.Season;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SeasonDtoAssembler {

    private final DraftDtoAssembler draftDtoAssembler;
    private final RaceDtoAssembler raceDtoAssembler;

    public SeasonDtoAssembler(DraftDtoAssembler draftDtoAssembler, RaceDtoAssembler raceDtoAssembler) {
        this.draftDtoAssembler = draftDtoAssembler;
        this.raceDtoAssembler = raceDtoAssembler;
    }

    public SeasonSummaryDto toModel(Season season) {

        SeasonSummaryDto dto = new SeasonSummaryDto(
                season.getId(),
                season.getName()
        );
        return dto;
    }

    public SeasonDetailDto toDetailModel(Season season) {

        List<DraftSummaryDto> drafts = season.getDrafts().stream()
                .map(draftDtoAssembler::toModel)
                .collect(Collectors.toList());
        List<RaceSummaryDto> races = season.getRaces().stream()
                .map(raceDtoAssembler::toModel)
                .collect(Collectors.toList());


        SeasonDetailDto dto = new SeasonDetailDto(
                season.getId(),
                season.getName(),
                drafts,
                races
        );
        return dto;
    }

}
