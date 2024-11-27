package org.tbeerbower.wsfl_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tbeerbower.wsfl_backend.model.RaceResult;
import org.tbeerbower.wsfl_backend.model.Race;
import org.tbeerbower.wsfl_backend.model.Runner;

import java.util.List;
import java.util.Optional;

@Repository
public interface RaceResultRepository extends JpaRepository<RaceResult, Long> {
    List<RaceResult> findByRace(Race race);
    Page<RaceResult> findByRace(Race race, Pageable pageable);
    Optional<RaceResult> findByRaceAndRunner(Race race, Runner runner);
    List<RaceResult> findByRunner(Runner runner);
    Page<RaceResult> findByRunner(Runner runner, Pageable pageable);
}