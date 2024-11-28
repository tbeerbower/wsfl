package org.tbeerbower.wsfl_backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.tbeerbower.wsfl_backend.model.Runner;
import org.tbeerbower.wsfl_backend.repository.RunnerRepository;
import org.tbeerbower.wsfl_backend.service.RunnerService;

import java.util.List;
import java.util.Optional;

@Service
public class RunnerServiceImpl implements RunnerService {
    
    private final RunnerRepository runnerRepository;
    
    @Autowired
    public RunnerServiceImpl(RunnerRepository runnerRepository) {
        this.runnerRepository = runnerRepository;
    }
    
    @Override
    public Runner save(Runner runner) {
        return runnerRepository.save(runner);
    }
    
    @Override
    public List<Runner> findAll() {
        return runnerRepository.findAll();
    }
    
    @Override
    public Page<Runner> findAll(Pageable pageable) {
        return runnerRepository.findAll(pageable);
    }
    
    @Override
    public Optional<Runner> findById(Long id) {
        return runnerRepository.findById(id);
    }
    
    @Override
    public List<Runner> findByGender(String gender) {
        return runnerRepository.findByGender(gender);
    }
    
    @Override
    public Page<Runner> findByGender(String gender, Pageable pageable) {
        return runnerRepository.findByGender(
                gender.toUpperCase().startsWith("M") ? "M" : "F", pageable);
    }
    
    @Override
    public void deleteById(Long id) {
        runnerRepository.deleteById(id);
    }
}