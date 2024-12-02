package org.tbeerbower.wsfl_backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.tbeerbower.wsfl_backend.dto.DraftCreateDto;
import org.tbeerbower.wsfl_backend.dto.DraftPatchDto;
import org.tbeerbower.wsfl_backend.dto.DraftUpdateDto;
import org.tbeerbower.wsfl_backend.model.Draft;
import org.tbeerbower.wsfl_backend.model.DraftPick;
import org.tbeerbower.wsfl_backend.model.League;
import org.tbeerbower.wsfl_backend.model.Runner;
import org.tbeerbower.wsfl_backend.model.Team;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    public Page<Draft> findByTeams(Set<Team> teams, Pageable pageable);
    Draft create(DraftCreateDto createDto);
    Draft update(Long id, DraftUpdateDto updateDto);
    Draft patch(Long id, DraftPatchDto patchDto);
    Draft makePick(Draft draft, Long runnerId);
    List<Runner> getAvailableRunners(Draft draft);
    Team getCurrentTeam(Draft draft);
    boolean isTeamTurn(Draft draft, Team team);
    Page<DraftPick> findDraftPicksByDraft(Draft draft, Long teamId, Pageable pageable);
}