package org.tbeerbower.wsfl_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tbeerbower.wsfl_backend.model.League;

import java.util.List;

@Repository
public interface LeagueRepository extends JpaRepository<League, Long> {
    List<League> findBySeason(Integer season);
} 