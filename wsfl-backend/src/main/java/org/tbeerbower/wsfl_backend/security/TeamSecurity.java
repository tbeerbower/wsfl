package org.tbeerbower.wsfl_backend.security;

import org.springframework.stereotype.Component;
import org.tbeerbower.wsfl_backend.service.TeamService;

@Component("teamSecurity")
public class TeamSecurity {
    
    private final TeamService teamService;
    
    public TeamSecurity(TeamService teamService) {
        this.teamService = teamService;
    }
    
    public boolean isTeamOwner(Long teamId, String userEmail) {
        return teamService.findById(teamId)
                .map(team -> team.getOwner().getEmail().equals(userEmail))
                .orElse(false);
    }
} 