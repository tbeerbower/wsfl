package org.tbeerbower.wsfl_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tbeerbower.wsfl_backend.model.League;
import org.tbeerbower.wsfl_backend.model.User;

import java.util.List;

@Repository
public interface LeagueRepository extends JpaRepository<League, Long> {
    Page<League> findByAdmin(User admin, Pageable pageable);
    List<League> findByAdmin(User admin);
}