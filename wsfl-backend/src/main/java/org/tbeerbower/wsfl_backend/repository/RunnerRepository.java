package org.tbeerbower.wsfl_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tbeerbower.wsfl_backend.model.Runner;

import java.util.Collection;
import java.util.List;

@Repository
public interface RunnerRepository extends JpaRepository<Runner, Long> {
    List<Runner> findByGender(String gender);
    Page<Runner> findByGender(String gender, Pageable pageable);
    Page<Runner> findByIdNotIn(Collection<Long> ids, Pageable pageable);
    Page<Runner> findByIdIn(Collection<Long> ids, Pageable pageable);
}