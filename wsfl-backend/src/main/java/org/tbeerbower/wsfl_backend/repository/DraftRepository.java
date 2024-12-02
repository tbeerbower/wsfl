package org.tbeerbower.wsfl_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.tbeerbower.wsfl_backend.model.Draft;
import org.tbeerbower.wsfl_backend.model.DraftPick;
import org.tbeerbower.wsfl_backend.model.League;
import org.tbeerbower.wsfl_backend.model.Team;

import java.util.Collection;
import java.util.List;

public interface DraftRepository extends JpaRepository<Draft, Long> {
    List<Draft> findByLeague(League league);
    
 //   @Query("SELECT d FROM Draft d WHERE d.league.id = :leagueId")
    Page<Draft> findByLeagueId(@Param("leagueId") Long leagueId, Pageable pageable);
    
//    @Query("SELECT d FROM Draft d WHERE d.season = :season")
    Page<Draft> findBySeason(@Param("season") Integer season, Pageable pageable);
    
 //   @Query("SELECT d FROM Draft d WHERE d.league.id = :leagueId AND d.season = :season")
    Page<Draft> findByLeagueIdAndSeason(@Param("leagueId") Long leagueId, @Param("season") Integer season, Pageable pageable);

    @Query("SELECT d FROM Draft d JOIN d.teams t WHERE t IN :teams")
    Page<Draft> findByTeamsIn(@Param("teams") Collection<Team> teams, Pageable pageable);
}