package org.tbeerbower.wsfl_backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.tbeerbower.wsfl_backend.dto.DraftCreateDto;
import org.tbeerbower.wsfl_backend.model.Draft;
import org.tbeerbower.wsfl_backend.model.League;
import org.tbeerbower.wsfl_backend.model.Runner;
import org.tbeerbower.wsfl_backend.model.Team;

import java.util.List;
import java.util.Optional;

public interface DraftService {
    List<Draft> findAll();
    Page<Draft> findAll(Pageable pageable);
    Page<Draft> findBySeason(Integer season, Pageable pageable);
    Optional<Draft> findById(Long id);
    boolean existsById(Long id);
    Draft save(Draft draft);
    void deleteById(Long id);
    List<Draft> findByLeague(League league);
    Page<Draft> findByLeague(Long leagueId, Pageable pageable);
    Page<Draft> findByLeagueAndSeason(Long leagueId, Integer season, Pageable pageable);
    Draft create(DraftCreateDto createDto);
    Draft createDraft(League league, Integer numberOfRounds, Boolean snakeOrder);
    Draft makePick(Draft draft, Long runnerId);
    List<Runner> getAvailableRunners(Draft draft);
    Team getCurrentTeam(Draft draft);
    boolean isTeamTurn(Draft draft, Team team);
    Draft startDraft(Draft draft);
    Draft endDraft(Draft draft);
}