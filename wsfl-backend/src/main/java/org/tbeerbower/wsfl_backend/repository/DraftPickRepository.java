package org.tbeerbower.wsfl_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.tbeerbower.wsfl_backend.model.Draft;
import org.tbeerbower.wsfl_backend.model.DraftPick;
import org.tbeerbower.wsfl_backend.model.League;

import java.util.List;

public interface DraftPickRepository extends JpaRepository<DraftPick, Long> {
    Page<DraftPick> findDraftPicksByDraft(@Param("draft") Draft draft, Pageable pageable);
    Page<DraftPick> findDraftPicksByDraftAndTeamId(@Param("draft") Draft draft, @Param("teamId")Long teamId, Pageable pageable);
}