package org.tbeerbower.wsfl_backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.tbeerbower.wsfl_backend.model.Race;
import org.tbeerbower.wsfl_backend.model.League;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RaceService {
    Race save(Race race);
    Page<Race> findAll(Pageable pageable);
    Optional<Race> findById(Long id);
    Page<Race> findByLeague(League league, Pageable pageable);
    List<Race> findUpcomingRaces(League league, LocalDate date);
    List<Race> findPlayoffRaces(League league);
    void deleteById(Long id);
    boolean existsById(Long id);
}