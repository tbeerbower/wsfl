package org.tbeerbower.wsfl_backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.tbeerbower.wsfl_backend.model.RaceResult;
import org.tbeerbower.wsfl_backend.model.Race;
import org.tbeerbower.wsfl_backend.model.Runner;
import java.util.List;
import java.util.Optional;

public interface RaceResultService {
    RaceResult save(RaceResult raceResult);
    List<RaceResult> findAll();
    Page<RaceResult> findAll(Pageable pageable);
    Optional<RaceResult> findById(Long id);
    Page<RaceResult> findByRace(Race race, Pageable pageable);
    Optional<RaceResult> findByRaceAndRunner(Race race, Runner runner);
    List<RaceResult> findByRunner(Runner runner);
    Page<RaceResult> findByRunner(Runner runner, Pageable pageable);
    void deleteById(Long id);
    boolean existsById(Long id);
    void updateTeamScores(Race race);
}