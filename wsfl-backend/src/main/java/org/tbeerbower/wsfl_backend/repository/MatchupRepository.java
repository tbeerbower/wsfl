package org.tbeerbower.wsfl_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tbeerbower.wsfl_backend.model.Matchup;
import org.tbeerbower.wsfl_backend.model.Race;
import org.tbeerbower.wsfl_backend.model.Team;

import java.util.Collection;
import java.util.List;

@Repository
public interface MatchupRepository extends JpaRepository<Matchup, Long> {
    List<Matchup> findByRace(Race race);
    Page<Matchup> findByRace(Race race, Pageable pageable);
    List<Matchup> findByTeam1OrTeam2(Team team1, Team team2);
    Page<Matchup> findByTeam1IdOrTeam2Id(Long team1Id, Long team2Id, Pageable pageable);
    Page<Matchup> findByTeam1IdInOrTeam2IdIn(Collection<Long> team1Ids, Collection<Long> team2Ids,Pageable pageable);
    Page<Matchup> findByRaceIdAndTeam1IdOrRaceIdAndTeam2Id(Long raceId, Long team1Id, Long raceId2, Long team2Id, Pageable pageable);
}