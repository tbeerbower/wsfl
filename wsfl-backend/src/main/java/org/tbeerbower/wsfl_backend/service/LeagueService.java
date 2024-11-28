package org.tbeerbower.wsfl_backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.tbeerbower.wsfl_backend.model.League;
import org.tbeerbower.wsfl_backend.model.Team;
import org.tbeerbower.wsfl_backend.model.User;

import java.util.List;
import java.util.Optional;

public interface LeagueService {
    League save(League league);
    List<League> findAll();
    Page<League> findAll(Pageable pageable);
    Page<League> findByAdmin(User admin, Pageable pageable);
    Optional<League> findById(Long id);
    List<League> findBySeason(Integer season);
    boolean addTeam(League league, Team team);
    void deleteById(Long id);
    boolean existsById(Long id);
}