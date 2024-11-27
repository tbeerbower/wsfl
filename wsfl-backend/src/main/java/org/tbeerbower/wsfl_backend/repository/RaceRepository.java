package org.tbeerbower.wsfl_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tbeerbower.wsfl_backend.model.Race;
import org.tbeerbower.wsfl_backend.model.League;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RaceRepository extends JpaRepository<Race, Long> {
    Page<Race> findByLeagueOrderByDateAsc(League league, Pageable pageable);
    List<Race> findByLeagueAndDateGreaterThanEqual(League league, LocalDate date);
    List<Race> findByLeagueAndIsPlayoffTrue(League league);
}