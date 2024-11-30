package org.tbeerbower.wsfl_backend.assembler;

import org.springframework.stereotype.Component;
import org.tbeerbower.wsfl_backend.dto.LeagueDetailsDto;
import org.tbeerbower.wsfl_backend.dto.LeagueSummaryDto;
import org.tbeerbower.wsfl_backend.dto.SeasonSummaryDto;
import org.tbeerbower.wsfl_backend.dto.TeamSummaryDto;
import org.tbeerbower.wsfl_backend.dto.UserSummaryDto;
import org.tbeerbower.wsfl_backend.model.League;
import org.tbeerbower.wsfl_backend.model.Season;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SeasonDtoAssembler {

    public SeasonSummaryDto toModel(Season season) {
        SeasonSummaryDto dto = new SeasonSummaryDto(
                season.getId(),
                season.getName()
        );
        return dto;
    }

}
