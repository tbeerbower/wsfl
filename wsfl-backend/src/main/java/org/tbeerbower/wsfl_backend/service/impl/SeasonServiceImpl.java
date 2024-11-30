package org.tbeerbower.wsfl_backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.tbeerbower.wsfl_backend.model.Season;
import org.tbeerbower.wsfl_backend.repository.SeasonRepository;
import org.tbeerbower.wsfl_backend.service.SeasonService;

import java.util.Optional;

@Service
public class SeasonServiceImpl implements SeasonService {

    private final SeasonRepository seasonRepository;

    @Autowired
    public SeasonServiceImpl(SeasonRepository seasonRepository) {
        this.seasonRepository = seasonRepository;
    }

    @Override
    public Page<Season> findAll(Pageable pageable) {
        return seasonRepository.findAll(pageable);
    }

    @Override
    public Optional<Season> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Season save(Season season) {
        return seasonRepository.save(season);
    }
}