package org.tbeerbower.wsfl_backend.assembler;

import org.springframework.stereotype.Component;
import org.tbeerbower.wsfl_backend.dto.MatchupDetailsDto;
import org.tbeerbower.wsfl_backend.dto.MatchupSummaryDto;
import org.tbeerbower.wsfl_backend.dto.RaceSummaryDto;
import org.tbeerbower.wsfl_backend.dto.TeamSummaryDto;
import org.tbeerbower.wsfl_backend.model.Matchup;
import org.tbeerbower.wsfl_backend.model.Race;
import org.tbeerbower.wsfl_backend.model.Team;

import org.tbeerbower.wsfl_backend.assembler.TeamDtoAssembler;
import org.tbeerbower.wsfl_backend.assembler.RaceDtoAssembler;

@Component
public class MatchupDtoAssembler {

    private final TeamDtoAssembler teamDtoAssembler;
    private final RaceDtoAssembler raceDtoAssembler;

    public MatchupDtoAssembler(TeamDtoAssembler teamDtoAssembler, RaceDtoAssembler raceDtoAssembler) {
        this.teamDtoAssembler = teamDtoAssembler;
        this.raceDtoAssembler = raceDtoAssembler;
    }

    public MatchupSummaryDto toModel(Matchup matchup) {
        return createSummaryDto(matchup);
    }

    public MatchupDetailsDto toDetailedModel(Matchup matchup) {
        return createDetailedDto(matchup);
    }

    private MatchupDetailsDto createDetailedDto(Matchup matchup) {
        TeamSummaryDto team1Dto = teamDtoAssembler.toModel(matchup.getTeam1());
        TeamSummaryDto team2Dto = teamDtoAssembler.toModel(matchup.getTeam2());
        RaceSummaryDto raceDto = raceDtoAssembler.toModel(matchup.getRace());

        return new MatchupDetailsDto(
            matchup.getId(),
            raceDto,
            team1Dto,
            team2Dto,
            matchup.getTeam1Score(),
            matchup.getTeam2Score()
        );
    }

    private MatchupSummaryDto createSummaryDto(Matchup matchup) {
        TeamSummaryDto team1Dto = teamDtoAssembler.toModel(matchup.getTeam1());
        TeamSummaryDto team2Dto = teamDtoAssembler.toModel(matchup.getTeam2());

        return new MatchupSummaryDto(
            matchup.getId(),
            matchup.getRace().getId(),
            team1Dto,
            team2Dto,
            matchup.getTeam1Score(),
            matchup.getTeam2Score()
        );
    }
}
