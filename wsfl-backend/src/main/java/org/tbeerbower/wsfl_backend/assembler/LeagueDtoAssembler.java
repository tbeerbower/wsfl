package org.tbeerbower.wsfl_backend.assembler;

import org.springframework.stereotype.Component;
import org.tbeerbower.wsfl_backend.dto.LeagueDetailsDto;
import org.tbeerbower.wsfl_backend.dto.LeagueSummaryDto;
import org.tbeerbower.wsfl_backend.dto.RaceSummaryDto;
import org.tbeerbower.wsfl_backend.dto.SeasonSummaryDto;
import org.tbeerbower.wsfl_backend.dto.TeamSummaryDto;
import org.tbeerbower.wsfl_backend.dto.UserDraftDetailsDto;
import org.tbeerbower.wsfl_backend.dto.UserLeagueDetailsDto;
import org.tbeerbower.wsfl_backend.dto.UserMatchupDetailsDto;
import org.tbeerbower.wsfl_backend.dto.UserSeasonDetailsDto;
import org.tbeerbower.wsfl_backend.dto.UserStandingDto;
import org.tbeerbower.wsfl_backend.dto.UserSummaryDto;
import org.tbeerbower.wsfl_backend.dto.UserTeamDetailsDto;
import org.tbeerbower.wsfl_backend.dto.UserTeamMatchupsDetailsDto;
import org.tbeerbower.wsfl_backend.model.Draft;
import org.tbeerbower.wsfl_backend.model.League;
import org.tbeerbower.wsfl_backend.model.Matchup;
import org.tbeerbower.wsfl_backend.model.Season;
import org.tbeerbower.wsfl_backend.model.Standing;
import org.tbeerbower.wsfl_backend.model.Team;
import org.tbeerbower.wsfl_backend.model.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public class LeagueDtoAssembler  {

    public LeagueSummaryDto toModel(League league) {
        LeagueSummaryDto dto = createLeagueSummaryDto(league);
        return dto;
    }

    public LeagueDetailsDto toDetailsDto(League league) {
        List<TeamSummaryDto> teamDtos = league.getTeams().stream()
                .map(team -> new TeamSummaryDto(
                        team.getId(),
                        team.getName(),
                        team.getWins(),
                        team.getLosses(),
                        team.getTies(),
                        team.getTotalScore()
                ))
                .collect(Collectors.toList());

        Set<Draft> drafts = league.getDrafts();

        Set<Season> seasonSet = drafts.stream()
                .map(draft -> draft.getSeason())
                .collect(Collectors.toUnmodifiableSet());

        List<SeasonSummaryDto> seasonDtos = seasonSet.stream()
                .map(this::createSeasonSummaryDto)
                .toList();

        LeagueDetailsDto dto = new LeagueDetailsDto(
                league.getId(),
                league.getName(),
                new UserSummaryDto(league.getAdmin().getId(), league.getAdmin().getName(), league.getAdmin().getEmail()),
                teamDtos,
                seasonDtos
        );

        return dto;
    }

    public UserLeagueDetailsDto toUserLeagueDetailsDto(League league, User user) {

        Set<Draft> drafts = league.getDrafts();

        List<UserDraftDetailsDto> seasonDtos = drafts.stream().
                filter(draft -> draft.includes(user))
                .map(draft1 -> createUserDraftDetailsDto(draft1, user))
                .toList();

        UserLeagueDetailsDto dto = new UserLeagueDetailsDto(
                league.getId(),
                league.getName(),
                new UserSummaryDto(league.getAdmin().getId(), league.getAdmin().getName(), league.getAdmin().getEmail()),
                seasonDtos
        );

        return dto;
    }

    public LeagueSummaryDto toSummaryDto(League league) {
        LeagueSummaryDto dto = createLeagueSummaryDto(league);
         return dto;
    }

    private LeagueSummaryDto createLeagueSummaryDto(League league) {
        return new LeagueSummaryDto(
            league.getId(),
            league.getName()
        );
    }

    private SeasonSummaryDto createSeasonSummaryDto(Season season) {

        SeasonSummaryDto dto = new SeasonSummaryDto(
                season.getId(),
                season.getName(),
                season.isComplete()
        );
        return dto;
    }

    private UserDraftDetailsDto createUserDraftDetailsDto(Draft draft, User user) {

        UserSeasonDetailsDto seasonDto = new UserSeasonDetailsDto(
                draft.getSeason().getId(),
                draft.getSeason().getName(),
                draft.getSeason().isComplete()
        );

        List<Team> teams = new java.util.ArrayList<>(draft.getMatchups().stream()
                .map(Matchup::getTeam1)
                .filter(team -> team.getOwner().equals(user))
                .toList());
        teams.addAll(draft.getMatchups().stream()
                .map(Matchup::getTeam2)
                .filter(team -> team.getOwner().equals(user))
                .toList());

        List<UserTeamMatchupsDetailsDto> teamMatchups = new ArrayList<>();

        Map<Team, Standing> teamStandingMap = League.getTeamStandingMap(draft);

        for (Team team : new HashSet<>(teams)) {
            Standing teamStanding = teamStandingMap.get(team);
            teamMatchups.add(new UserTeamMatchupsDetailsDto(
                    team.getId(),
                    team.getName(),
                    teamStanding.getWins(),
                    teamStanding.getLosses(),
                    teamStanding.getTies(),
                    teamStanding.getTotalScore(),
                    draft.getMatchups().stream()
                            .filter(matchup -> matchup.includes(user) && (matchup.includes(team)))
                            .map(this::createUserMatchupDetailsDto)
                            .toList()
            ));
        }

        List<UserStandingDto> standings = draft.getLeague().getStandings(draft).stream()
                .map(standing -> new UserStandingDto(
                        getTeamDto(standing.getTeam()),
                        standing.getWins(),
                        standing.getLosses(),
                        standing.getTies(),
                        standing.getTotalScore()
                ))
                .toList();

        UserDraftDetailsDto dto = new UserDraftDetailsDto(
                draft.getId(),
                draft.getName(),
                seasonDto,
                draft.getNumberOfRounds(),
                draft.getStatus(),
                draft.getCurrentRound(),
                draft.getCurrentPick(),
                standings,
                teamMatchups
        );
        return dto;
    }

    private UserMatchupDetailsDto createUserMatchupDetailsDto(Matchup matchup) {

        RaceSummaryDto raceDto = new RaceSummaryDto(
                matchup.getRace().getId(),
                matchup.getRace().getName(),
                matchup.getRace().getDate(),
                matchup.getRace().isCanceled(),
                matchup.getRace().isPlayoff()
        );

        Team team1 = matchup.getTeam1();
        UserTeamDetailsDto team1Dto = getTeamDto(team1);

        Team team2 = matchup.getTeam2();
        UserTeamDetailsDto team2Dto = getTeamDto(team2);

        return new UserMatchupDetailsDto(
                matchup.getId(),
                raceDto,
                team1Dto,
                team2Dto,
                matchup.getTeam1Score(),
                matchup.getTeam2Score()
        );
    }

    private static UserTeamDetailsDto getTeamDto(Team team1) {

        UserSummaryDto teamOwnerDto = new UserSummaryDto(
                team1.getOwner().getId(),
                team1.getOwner().getName(),
                team1.getOwner().getEmail()
        );

        return new UserTeamDetailsDto(
                team1.getId(),
                team1.getName(),
                teamOwnerDto
        );
    }

}
