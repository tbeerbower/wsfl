package org.tbeerbower.wsfl_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tbeerbower.wsfl_backend.model.Team;
import org.tbeerbower.wsfl_backend.model.League;
import org.tbeerbower.wsfl_backend.model.User;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    Page<Team> findByLeague(League league, Pageable pageable);
    Page<Team> findByOwner(User owner, Pageable pageable);
    Page<Team> findByLeagueOrderByWinsDescTotalScoreAsc(League league, Pageable pageable);
} 