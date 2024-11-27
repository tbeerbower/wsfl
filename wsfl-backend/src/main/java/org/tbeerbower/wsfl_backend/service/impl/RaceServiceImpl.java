package org.tbeerbower.wsfl_backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.tbeerbower.wsfl_backend.model.Race;
import org.tbeerbower.wsfl_backend.model.League;
import org.tbeerbower.wsfl_backend.repository.RaceRepository;
import org.tbeerbower.wsfl_backend.service.RaceService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class RaceServiceImpl implements RaceService {
    
    private final RaceRepository raceRepository;
    
    @Autowired
    public RaceServiceImpl(RaceRepository raceRepository) {
        this.raceRepository = raceRepository;
    }
    
    @Override
    public Race save(Race race) {
        return raceRepository.save(race);
    }
    
    @Override
    public Page<Race> findAll(Pageable pageable) {
        return raceRepository.findAll(pageable);
    }
    
    @Override
    public Optional<Race> findById(Long id) {
        return raceRepository.findById(id);
    }
    
    @Override
    public Page<Race> findByLeague(League league, Pageable pageable) {
        return raceRepository.findByLeagueOrderByDateAsc(league, pageable);
    }
    
    @Override
    public List<Race> findUpcomingRaces(League league, LocalDate date) {
        return raceRepository.findByLeagueAndDateGreaterThanEqual(league, date);
    }
    
    @Override
    public List<Race> findPlayoffRaces(League league) {
        return raceRepository.findByLeagueAndIsPlayoffTrue(league);
    }
    
    @Override
    public void deleteById(Long id) {
        raceRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return raceRepository.existsById(id);
    }
}