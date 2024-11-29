package org.tbeerbower.wsfl_backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.tbeerbower.wsfl_backend.model.League;
import org.tbeerbower.wsfl_backend.model.Team;
import org.tbeerbower.wsfl_backend.model.User;
import org.tbeerbower.wsfl_backend.repository.LeagueRepository;
import org.tbeerbower.wsfl_backend.service.LeagueService;

import java.util.List;
import java.util.Optional;

@Service
public class LeagueServiceImpl implements LeagueService {
    
    private final LeagueRepository leagueRepository;
    
    @Autowired
    public LeagueServiceImpl(LeagueRepository leagueRepository) {
        this.leagueRepository = leagueRepository;
    }
    
    @Override
    public League save(League league) {
        return leagueRepository.save(league);
    }
    
    @Override
    public List<League> findAll() {
        return leagueRepository.findAll();
    }

    @Override
    public Page<League> findByAdmin(User admin, Pageable pageable) {
        return leagueRepository.findByAdmin(admin, pageable);
    }

    @Override
    public Page<League> findAll(Pageable pageable) {
        return leagueRepository.findAll(pageable);
    }
    
    @Override
    public Optional<League> findById(Long id) {
        return leagueRepository.findById(id);
    }

    @Override
    public boolean addTeam(League league, Team team) {
        if (league.getTeams().contains(team)) {
            return false;
        }
        league.getTeams().add(team);
        leagueRepository.save(league);
        return true;
    }
    
    @Override
    public void deleteById(Long id) {
        leagueRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return leagueRepository.existsById(id);
    }
}