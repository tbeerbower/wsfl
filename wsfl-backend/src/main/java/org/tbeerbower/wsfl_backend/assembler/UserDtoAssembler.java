package org.tbeerbower.wsfl_backend.assembler;

import org.springframework.stereotype.Component;
import org.tbeerbower.wsfl_backend.dto.LeagueSummaryDto;
import org.tbeerbower.wsfl_backend.dto.TeamDetailsDto;
import org.tbeerbower.wsfl_backend.dto.TeamSummaryDto;
import org.tbeerbower.wsfl_backend.dto.UserDetailsDto;
import org.tbeerbower.wsfl_backend.dto.UserSummaryDto;
import org.tbeerbower.wsfl_backend.model.League;
import org.tbeerbower.wsfl_backend.model.Team;
import org.tbeerbower.wsfl_backend.model.User;

import java.util.stream.Collectors;

@Component
public class UserDtoAssembler {

    private final TeamDtoAssembler teamDtoAssembler;

    public UserDtoAssembler(TeamDtoAssembler teamDtoAssembler) {
        this.teamDtoAssembler = teamDtoAssembler;
    }


    public UserSummaryDto toModel(User user) {
        return new UserSummaryDto(
            user.getId(),
            user.getEmail(),
            user.getName()
        );
    }

    public UserDetailsDto toDetailedModel(User user) {
        return new UserDetailsDto(
            user.getId(),
            user.getEmail(),
            user.getName(),
            user.getPicture(),
            user.isActive(),
            user.getRoles(),
            user.getTeams().stream().map(teamDtoAssembler::toModel).collect(Collectors.toList())
        );
    }
}
