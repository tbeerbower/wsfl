package org.tbeerbower.wsfl_backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tbeerbower.wsfl_backend.dto.MatchupCreateDto;
import org.tbeerbower.wsfl_backend.exception.ResourceNotFoundException;
import org.tbeerbower.wsfl_backend.model.Draft;
import org.tbeerbower.wsfl_backend.model.Matchup;
import org.tbeerbower.wsfl_backend.model.Race;
import org.tbeerbower.wsfl_backend.model.Team;
import org.tbeerbower.wsfl_backend.repository.DraftRepository;
import org.tbeerbower.wsfl_backend.repository.MatchupRepository;
import org.tbeerbower.wsfl_backend.repository.RaceRepository;
import org.tbeerbower.wsfl_backend.repository.TeamRepository;
import org.tbeerbower.wsfl_backend.service.MatchupService;

import java.util.List;
import java.util.Optional;

@Service
public class MatchupServiceImpl implements MatchupService {
    
    private final MatchupRepository matchupRepository;
    private final RaceRepository raceRepository;
    private final TeamRepository teamRepository;

    private final DraftRepository draftRepository;
    
    @Autowired
    public MatchupServiceImpl(MatchupRepository matchupRepository,
                              RaceRepository raceRepository,
                              TeamRepository teamRepository, DraftRepository draftRepository) {
        this.matchupRepository = matchupRepository;
        this.raceRepository = raceRepository;
        this.teamRepository = teamRepository;
        this.draftRepository = draftRepository;
    }
    
    @Override
    public Matchup save(Matchup matchup) {
        Matchup savedMatchup = matchupRepository.save(matchup);
        updateMatchupResults(savedMatchup);
        return savedMatchup;
    }
    
    @Override
    public List<Matchup> findAll() {
        return matchupRepository.findAll();
    }
    
    @Override
    public Page<Matchup> findAll(Pageable pageable) {
        return matchupRepository.findAll(pageable);
    }
    
    @Override
    public Optional<Matchup> findById(Long id) {
        return matchupRepository.findById(id);
    }
    
    @Override
    public boolean existsById(Long id) {
        return matchupRepository.existsById(id);
    }
    
    @Override
    public List<Matchup> findByRace(Race race) {
        return matchupRepository.findByRace(race);
    }
    
    @Override
    public Page<Matchup> findByRace(Long raceId, Pageable pageable) {
        Race race = raceRepository.findById(raceId)
            .orElseThrow(() -> new ResourceNotFoundException("Race not found with id: " + raceId));
        return matchupRepository.findByRace(race, pageable);
    }
    
    @Override
    public List<Matchup> findByTeam(Team team) {
        return matchupRepository.findByTeam1OrTeam2(team, team);
    }
    
    @Override
    public Page<Matchup> findByTeam(Long teamId, Pageable pageable) {
        return matchupRepository.findByTeam1IdOrTeam2Id(teamId, teamId, pageable);
    }
    
    @Override
    public Page<Matchup> findByRaceAndTeam(Long raceId, Long teamId, Pageable pageable) {
        // Verify race exists
        if (!raceRepository.existsById(raceId)) {
            throw new ResourceNotFoundException("Race not found with id: " + raceId);
        }
        
        return matchupRepository.findByRaceIdAndTeam1IdOrRaceIdAndTeam2Id(
            raceId, teamId, raceId, teamId, pageable);
    }
    
    @Override
    public void deleteById(Long id) {
        matchupRepository.deleteById(id);
    }
    
    @Override
    public void updateMatchupResults(Matchup matchup) {
        Team team1 = matchup.getTeam1();
        Team team2 = matchup.getTeam2();
        
        if (matchup.getTeam1Score() != null && matchup.getTeam2Score() != null) {
            if (matchup.getTeam1Score() < matchup.getTeam2Score()) {
                team1.setWins(team1.getWins() + 1);
                team2.setLosses(team2.getLosses() + 1);
            } else if (matchup.getTeam2Score() < matchup.getTeam1Score()) {
                team2.setWins(team2.getWins() + 1);
                team1.setLosses(team1.getLosses() + 1);
            } else {
                team1.setTies(team1.getTies() + 1);
                team2.setTies(team2.getTies() + 1);
            }
            
            team1.setTotalScore(team1.getTotalScore() + matchup.getTeam1Score());
            team2.setTotalScore(team2.getTotalScore() + matchup.getTeam2Score());
        }
    }
    
    @Override
    @Transactional
    public Matchup create(MatchupCreateDto createDto) {
        Race race = raceRepository.findById(createDto.getRaceId())
            .orElseThrow(() -> new ResourceNotFoundException("Race not found with id: " + createDto.getRaceId()));
            
        Team team1 = teamRepository.findById(createDto.getTeam1Id())
            .orElseThrow(() -> new ResourceNotFoundException("Team 1 not found with id: " + createDto.getTeam1Id()));
            
        Team team2 = teamRepository.findById(createDto.getTeam2Id())
            .orElseThrow(() -> new ResourceNotFoundException("Team 2 not found with id: " + createDto.getTeam2Id()));

        Draft draft = draftRepository.findById(createDto.getDraftId())
            .orElseThrow(() -> new ResourceNotFoundException("Draft not found with id: " + createDto.getDraftId()));

        Matchup matchup = new Matchup();
        matchup.setRace(race);
        matchup.setTeam1(team1);
        matchup.setTeam2(team2);
        matchup.setTeam1Score(createDto.getTeam1Score());
        matchup.setTeam2Score(createDto.getTeam2Score());
        matchup.setDraft(draft);

        
        return matchupRepository.save(matchup);
    }
    
    @Override
    @Transactional
    public Matchup update(Matchup matchup) {
        // Verify matchup exists
        if (!matchupRepository.existsById(matchup.getId())) {
            throw new ResourceNotFoundException("Matchup not found with id: " + matchup.getId());
        }
        
        // Save and update results
        Matchup updatedMatchup = matchupRepository.save(matchup);
        updateMatchupResults(updatedMatchup);
        
        return updatedMatchup;
    }
    
    @Override
    @Transactional
    public Matchup updateScores(Long id, Integer team1Score, Integer team2Score) {
        Matchup matchup = findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Matchup not found with id: " + id));
        
        matchup.setTeam1Score(team1Score);
        matchup.setTeam2Score(team2Score);
        
        Matchup updatedMatchup = save(matchup);
        updateMatchupResults(updatedMatchup);
        return updatedMatchup;
    }
}