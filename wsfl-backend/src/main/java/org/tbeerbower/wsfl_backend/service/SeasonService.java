package org.tbeerbower.wsfl_backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.tbeerbower.wsfl_backend.model.League;
import org.tbeerbower.wsfl_backend.model.Season;
import org.tbeerbower.wsfl_backend.model.Team;
import org.tbeerbower.wsfl_backend.model.User;

import java.util.List;
import java.util.Optional;

public interface SeasonService {

    Page<Season> findAll(Pageable pageable);
    Optional<Season> findById(Long id);
    Season save(Season season);
}