package org.tbeerbower.wsfl_backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.tbeerbower.wsfl_backend.model.Team;
import org.tbeerbower.wsfl_backend.model.League;
import org.tbeerbower.wsfl_backend.model.User;
import org.tbeerbower.wsfl_backend.model.Runner;
import java.util.List;
import java.util.Optional;

public interface TeamService {
    Team save(Team team);
    List<Team> findAll();
    Page<Team> findAll(Pageable pageable);
    Optional<Team> findById(Long id);
    boolean existsById(Long id);
    List<Team> findByLeague(League league);
    List<Team> findByOwner(User owner);
    List<Team> getLeagueStandings(League league);
    boolean addRunner(Team team, Runner runner);
    void deleteById(Long id);
}