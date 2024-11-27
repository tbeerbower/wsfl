package org.tbeerbower.wsfl_backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.tbeerbower.wsfl_backend.model.Runner;
import java.util.List;
import java.util.Optional;

public interface RunnerService {
    Runner save(Runner runner);
    List<Runner> findAll();
    Page<Runner> findAll(Pageable pageable);
    Optional<Runner> findById(Long id);
    List<Runner> findByGender(String gender);
    Page<Runner> findByGender(String gender, Pageable pageable);
    void deleteById(Long id);
}