package org.tbeerbower.wsfl_backend.assembler;

import org.springframework.stereotype.Component;
import org.tbeerbower.wsfl_backend.dto.LeagueSummaryDto;
import org.tbeerbower.wsfl_backend.dto.RunnerSummaryDto;
import org.tbeerbower.wsfl_backend.dto.TeamDetailsDto;
import org.tbeerbower.wsfl_backend.dto.TeamSummaryDto;
import org.tbeerbower.wsfl_backend.dto.UserSummaryDto;
import org.tbeerbower.wsfl_backend.model.League;
import org.tbeerbower.wsfl_backend.model.Runner;
import org.tbeerbower.wsfl_backend.model.Team;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TeamDtoAssembler {

    public TeamSummaryDto toModel(Team team) {
        return new TeamSummaryDto(
            team.getId(),
            team.getName(),
            team.getWins(),
            team.getLosses(),
            team.getTies(),
            team.getTotalScore()
        );
    }

    public TeamDetailsDto toDetailedModel(Team team) {
        List<RunnerSummaryDto> runnerDtos = team.getRunners().stream()
                .map(this::convertToRunnerSummaryDto)
                .collect(Collectors.toList());

        League league = team.getLeague();
        LeagueSummaryDto leagueDto = new LeagueSummaryDto(
                league.getId(),
                league.getName(),
                league.getSeason()
        );

        return getTeamDetailsDto(team, leagueDto, runnerDtos);
    }

    private static TeamDetailsDto getTeamDetailsDto(Team team, LeagueSummaryDto leagueDto, List<RunnerSummaryDto> runnerDtos) {
        UserSummaryDto ownerDto = new UserSummaryDto(
                team.getOwner().getId(),
                team.getOwner().getEmail(),
                team.getOwner().getName()
        );

        TeamDetailsDto teamDto = new TeamDetailsDto(
                team.getId(),
                team.getName(),
                team.getWins(),
                team.getLosses(),
                team.getTies(),
                team.getTotalScore(),
                leagueDto,
                ownerDto
        );
        teamDto.setRunners(runnerDtos);
        return teamDto;
    }

    private RunnerSummaryDto convertToRunnerSummaryDto(Runner runner) {
        return new RunnerSummaryDto(
                runner.getId(),
                runner.getName(),
                runner.getGender()
        );
    }
}
