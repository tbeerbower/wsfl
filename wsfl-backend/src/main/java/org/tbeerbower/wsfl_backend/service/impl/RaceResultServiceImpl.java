package org.tbeerbower.wsfl_backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.tbeerbower.wsfl_backend.model.Matchup;
import org.tbeerbower.wsfl_backend.model.RaceResult;
import org.tbeerbower.wsfl_backend.model.Race;
import org.tbeerbower.wsfl_backend.model.Runner;
import org.tbeerbower.wsfl_backend.model.Team;
import org.tbeerbower.wsfl_backend.repository.RaceResultRepository;
import org.tbeerbower.wsfl_backend.repository.MatchupRepository;
import org.tbeerbower.wsfl_backend.service.RaceResultService;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

@Service
public class RaceResultServiceImpl implements RaceResultService {
    
    private final RaceResultRepository raceResultRepository;
    private final MatchupRepository matchupRepository;
    
    @Autowired
    public RaceResultServiceImpl(RaceResultRepository raceResultRepository, 
                               MatchupRepository matchupRepository) {
        this.raceResultRepository = raceResultRepository;
        this.matchupRepository = matchupRepository;
    }
    
    @Override
    public RaceResult save(RaceResult raceResult) {
        RaceResult savedResult = raceResultRepository.save(raceResult);
        updateTeamScores(raceResult.getRace());
        return savedResult;
    }
    
    @Override
    public List<RaceResult> findAll() {
        return raceResultRepository.findAll();
    }
    
    @Override
    public Page<RaceResult> findAll(Pageable pageable) {
        return raceResultRepository.findAll(pageable);
    }
    
    @Override
    public Optional<RaceResult> findById(Long id) {
        return raceResultRepository.findById(id);
    }
    
    @Override
    public boolean existsById(Long id) {
        return raceResultRepository.existsById(id);
    }
    

    @Override
    public Page<RaceResult> findByRace(Race race, Pageable pageable) {
        return raceResultRepository.findByRace(race, pageable);
    }
    
    @Override
    public Optional<RaceResult> findByRaceAndRunner(Race race, Runner runner) {
        return raceResultRepository.findByRaceAndRunner(race, runner);
    }
    
    @Override
    public List<RaceResult> findByRunner(Runner runner) {
        return raceResultRepository.findByRunner(runner);
    }
    
    @Override
    public Page<RaceResult> findByRunner(Runner runner, Pageable pageable) {
        return raceResultRepository.findByRunner(runner, pageable);
    }
    
    @Override
    public void deleteById(Long id) {
        Optional<RaceResult> raceResult = raceResultRepository.findById(id);
        raceResult.ifPresent(result -> {
            raceResultRepository.deleteById(id);
            updateTeamScores(result.getRace());
        });
    }
    
    @Override
    public void updateTeamScores(Race race) {
//        List<RaceResult> raceResults = raceResultRepository.findByRace(race);
//        Map<Team, Integer> teamScores = new HashMap<>();
//
//        // Calculate scores for each team
//        for (RaceResult result : raceResults) {
//            Set<Team> teams = result.getRunner().getTeams();
//            if (teams != null && !teams.isEmpty()) {
//                // For now, we'll use the first team in the set
//                Team team = teams.iterator().next();
//                teamScores.merge(team, result.getGenderPlace(), Integer::sum);
//            }
//        }
//
//        // Get all matchups for this race
//        List<Matchup> matchups = matchupRepository.findByRace(race);
//
//        // Update matchups with team scores
//        for (Matchup matchup : matchups) {
//            Team team1 = matchup.getTeam1();
//            Team team2 = matchup.getTeam2();
//
//            Integer team1Score = teamScores.getOrDefault(team1, 0);
//            Integer team2Score = teamScores.getOrDefault(team2, 0);
//
//            matchup.setTeam1Score(team1Score);
//            matchup.setTeam2Score(team2Score);
//            matchupRepository.save(matchup);
//        }
    }
}