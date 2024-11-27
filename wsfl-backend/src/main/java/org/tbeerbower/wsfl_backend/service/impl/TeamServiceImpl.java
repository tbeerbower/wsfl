package org.tbeerbower.wsfl_backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.tbeerbower.wsfl_backend.model.Team;
import org.tbeerbower.wsfl_backend.model.League;
import org.tbeerbower.wsfl_backend.model.User;
import org.tbeerbower.wsfl_backend.model.Runner;
import org.tbeerbower.wsfl_backend.repository.TeamRepository;
import org.tbeerbower.wsfl_backend.service.TeamService;

import java.util.List;
import java.util.Optional;

@Service
public class TeamServiceImpl implements TeamService {
    
    private final TeamRepository teamRepository;
    
    @Autowired
    public TeamServiceImpl(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }
    
    @Override
    public Team save(Team team) {
        return teamRepository.save(team);
    }
    
    @Override
    public List<Team> findAll() {
        return teamRepository.findAll();
    }

    @Override
    public Page<Team> findAll(Pageable pageable) {
        return teamRepository.findAll(pageable);
    }
    
    @Override
    public Optional<Team> findById(Long id) {
        return teamRepository.findById(id);
    }
    
    @Override
    public boolean existsById(Long id) {
        return teamRepository.existsById(id);
    }
    
    @Override
    public List<Team> findByLeague(League league) {
        return teamRepository.findByLeague(league);
    }
    
    @Override
    public List<Team> findByOwner(User owner) {
        return teamRepository.findByOwner(owner);
    }
    
    @Override
    public List<Team> getLeagueStandings(League league) {
        return teamRepository.findByLeagueOrderByWinsDescTotalScoreAsc(league);
    }
    
    @Override
    public boolean addRunner(Team team, Runner runner) {
        if (team.getRunners().size() >= 5) { // Assuming max 5 runners per team
            return false;
        }
        team.getRunners().add(runner);
        teamRepository.save(team);
        return true;
    }
    
    @Override
    public void deleteById(Long id) {
        teamRepository.deleteById(id);
    }
} 