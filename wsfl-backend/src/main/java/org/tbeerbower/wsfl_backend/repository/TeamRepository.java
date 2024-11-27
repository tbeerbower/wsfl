package org.tbeerbower.wsfl_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tbeerbower.wsfl_backend.model.Team;
import org.tbeerbower.wsfl_backend.model.League;
import org.tbeerbower.wsfl_backend.model.User;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByLeague(League league);
    List<Team> findByOwner(User owner);
    List<Team> findByLeagueOrderByWinsDescTotalScoreAsc(League league);
} 