package org.tbeerbower.wsfl_backend.assembler;

import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import org.tbeerbower.wsfl_backend.controller.LeagueController;
import org.tbeerbower.wsfl_backend.dto.LeagueSummaryDto;
import org.tbeerbower.wsfl_backend.model.League;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class LeagueDtoAssembler  {

    public LeagueSummaryDto toModel(League league) {
        LeagueSummaryDto dto = createLeagueSummaryDto(league);
        
//        dto.add(linkTo(methodOn(LeagueController.class).getLeagueById(league.getId())).withSelfRel());
//        dto.add(linkTo(methodOn(LeagueController.class).getAllLeagues(null)).withRel("leagues"));
//        dto.add(linkTo(methodOn(LeagueController.class).getLeagueTeams(league.getId())).withRel("teams"));
        
        return dto;
    }

    public LeagueSummaryDto toSummaryDto(League league) {
        LeagueSummaryDto dto = createLeagueSummaryDto(league);
 //       dto.add(linkTo(methodOn(LeagueController.class).getLeagueById(league.getId())).withSelfRel());
        return dto;
    }

    private LeagueSummaryDto createLeagueSummaryDto(League league) {
        return new LeagueSummaryDto(
            league.getId(),
            league.getName(),
            league.getSeason()
        );
    }
}
