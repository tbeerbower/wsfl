package org.tbeerbower.wsfl_backend.assembler;

import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import org.tbeerbower.wsfl_backend.controller.LeagueController;
import org.tbeerbower.wsfl_backend.dto.LeagueDetailsDto;
import org.tbeerbower.wsfl_backend.dto.LeagueSummaryDto;
import org.tbeerbower.wsfl_backend.dto.TeamSummaryDto;
import org.tbeerbower.wsfl_backend.dto.UserSummaryDto;
import org.tbeerbower.wsfl_backend.model.League;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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

        LeagueDetailsDto dto = new LeagueDetailsDto(
                league.getId(),
                league.getName(),
                new UserSummaryDto(league.getAdmin().getId(), league.getAdmin().getName(), league.getAdmin().getEmail()),
                teamDtos
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
}
