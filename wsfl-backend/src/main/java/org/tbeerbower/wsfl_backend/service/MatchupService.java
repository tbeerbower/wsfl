package org.tbeerbower.wsfl_backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.tbeerbower.wsfl_backend.dto.MatchupCreateDto;
import org.tbeerbower.wsfl_backend.model.Matchup;
import org.tbeerbower.wsfl_backend.model.Race;
import org.tbeerbower.wsfl_backend.model.Team;
import java.util.List;
import java.util.Optional;

public interface MatchupService {
    Matchup save(Matchup matchup);
    List<Matchup> findAll();
    Page<Matchup> findAll(Pageable pageable);
    Optional<Matchup> findById(Long id);
    boolean existsById(Long id);
    List<Matchup> findByRace(Race race);
    Page<Matchup> findByRace(Long raceId, Pageable pageable);
    List<Matchup> findByTeam(Team team);
    Page<Matchup> findByTeam(Long teamId, Pageable pageable);
    Page<Matchup> findByRaceAndTeam(Long raceId, Long teamId, Pageable pageable);
    Matchup create(MatchupCreateDto createDto);
    Matchup update(Matchup matchup);
    void deleteById(Long id);
    void updateMatchupResults(Matchup matchup);
    Matchup updateScores(Long id, Integer team1Score, Integer team2Score);
}