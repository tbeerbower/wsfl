package org.tbeerbower.wsfl_backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tbeerbower.wsfl_backend.dto.DraftCreateDto;
import org.tbeerbower.wsfl_backend.dto.DraftPatchDto;
import org.tbeerbower.wsfl_backend.dto.DraftUpdateDto;
import org.tbeerbower.wsfl_backend.exception.ResourceNotFoundException;
import org.tbeerbower.wsfl_backend.model.Draft;
import org.tbeerbower.wsfl_backend.model.DraftPick;
import org.tbeerbower.wsfl_backend.model.League;
import org.tbeerbower.wsfl_backend.model.Runner;
import org.tbeerbower.wsfl_backend.model.Season;
import org.tbeerbower.wsfl_backend.model.Team;
import org.tbeerbower.wsfl_backend.repository.DraftPickRepository;
import org.tbeerbower.wsfl_backend.repository.DraftRepository;
import org.tbeerbower.wsfl_backend.repository.LeagueRepository;
import org.tbeerbower.wsfl_backend.repository.RunnerRepository;
import org.tbeerbower.wsfl_backend.repository.SeasonRepository;
import org.tbeerbower.wsfl_backend.service.DraftService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DraftServiceImpl implements DraftService {

    private final DraftRepository draftRepository;
    private final DraftPickRepository draftPickRepository;
    private final RunnerRepository runnerRepository;
    private final LeagueRepository leagueRepository;
    private final SeasonRepository seasonRepository;
    
    @Autowired
    public DraftServiceImpl(DraftRepository draftRepository, DraftPickRepository draftPickRepository,
                            RunnerRepository runnerRepository,
                            LeagueRepository leagueRepository,
                            SeasonRepository seasonRepository) {
        this.draftRepository = draftRepository;
        this.draftPickRepository = draftPickRepository;
        this.runnerRepository = runnerRepository;
        this.leagueRepository = leagueRepository;
        this.seasonRepository = seasonRepository;
    }
    
    @Override
    public List<Draft> findAll() {
        return draftRepository.findAll();
    }
    
    @Override
    public Page<Draft> findAll(Pageable pageable) {
        return draftRepository.findAll(pageable);
    }
    
    @Override
    public Page<Draft> findBySeason(Integer season, Pageable pageable) {
        return draftRepository.findBySeason(season, pageable);
    }
    
    @Override
    public Optional<Draft> findById(Long id) {
        return draftRepository.findById(id);
    }
    
    @Override
    public boolean existsById(Long id) {
        return draftRepository.existsById(id);
    }
    
    @Override
    public Draft save(Draft draft) {
        return draftRepository.save(draft);
    }
    
    @Override
    public void deleteById(Long id) {
        draftRepository.deleteById(id);
    }
    
    @Override
    public List<Draft> findByLeague(League league) {
        return draftRepository.findByLeague(league);
    }

    
    @Override
    public Page<Draft> findByLeague(Long leagueId, Pageable pageable) {
        return draftRepository.findByLeagueId(leagueId, pageable);
    }
    
    @Override
    public Page<Draft> findByLeagueAndSeason(Long leagueId, Integer season, Pageable pageable) {
        return draftRepository.findByLeagueIdAndSeason(leagueId, season, pageable);
    }

    @Override
    public Page<Draft> findByTeams(Set<Team> teams, Pageable pageable) {
        return draftRepository.findByTeamsIn(teams, pageable);
    }

    @Override
    @Transactional
    public Draft create(DraftCreateDto createDto) {
        League league = leagueRepository.findById(createDto.getLeagueId())
            .orElseThrow(() -> new ResourceNotFoundException("League not found with id: " + createDto.getLeagueId()));

        Season season = seasonRepository.findById(createDto.getSeasonId())
            .orElseThrow(() -> new ResourceNotFoundException("Season not found with id: " + createDto.getSeasonId()));

        Draft draft = new Draft();
        draft.setLeague(league);
        draft.setName(createDto.getName());
        draft.setSeason(season);
        draft.setNumberOfRounds(createDto.getNumberOfRounds());
        draft.setSnakeOrder(createDto.getSnakeOrder());
        draft.setStartTime(createDto.getStartTime());
        
        return draftRepository.save(draft);
    }

    @Override
    @Transactional
    public Draft update(Long id, DraftUpdateDto updateDto) {

        Draft draft = findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Draft not found with id: " + id));

        if (updateDto.getNumberOfRounds() != null) {
            draft.setNumberOfRounds(updateDto.getNumberOfRounds());
        }
        if (updateDto.getSnakeOrder() != null) {
            draft.setSnakeOrder(updateDto.getSnakeOrder());
        }
        if (updateDto.getStartTime() != null) {
            draft.setStartTime(updateDto.getStartTime());
        }
        return draftRepository.save(draft);
    }

    @Override
    @Transactional
    public Draft patch(Long id, DraftPatchDto patchDto) {

        Draft draft = findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Draft not found with id: " + id));

        if (patchDto.getNumberOfRounds() != null) {
            draft.setNumberOfRounds(patchDto.getNumberOfRounds());
        }
        if (patchDto.getSnakeOrder() != null) {
            draft.setSnakeOrder(patchDto.getSnakeOrder());
        }
        if (patchDto.getStartTime() != null) {
            draft.setStartTime(patchDto.getStartTime());
        }
        if (patchDto.isStarted() != null && patchDto.isStarted() && !draft.isStarted()) {
            // Generate random draft order
            List<Long> teamIds = draft.getLeague().getTeams().stream()
                    .map(Team::getId)
                    .collect(Collectors.toList());
            Collections.shuffle(teamIds);
            draft.setDraftOrder(teamIds);

            draft.setStartTime(LocalDateTime.now());
        }
        if (patchDto.isComplete() != null  && !patchDto.isComplete() && draft.isComplete()) {
// complete draft
        }
        return draftRepository.save(draft);
    }

    @Override
    @Transactional
    public Draft makePick(Draft draft, Long runnerId) {
        if (draft.isComplete()) {
            throw new IllegalStateException("Draft is already complete");
        }
        
        Team currentTeam = getCurrentTeam(draft);
        if (currentTeam == null) {
            throw new IllegalStateException("No current team found for the draft");
        }
        
        Runner runner = runnerRepository.findById(runnerId)
            .orElseThrow(() -> new ResourceNotFoundException("Runner not found with id: " + runnerId));
            
        // Check if runner is already drafted
        boolean isRunnerDrafted = draft.getPicks().stream()
            .anyMatch(pick -> pick.getRunner().getId().equals(runnerId));
        if (isRunnerDrafted) {
            throw new IllegalStateException("Runner has already been drafted");
        }
        
        // Create new draft pick
        DraftPick pick = new DraftPick();
        pick.setDraft(draft);
        pick.setTeam(currentTeam);
        pick.setRunner(runner);
        pick.setRound(draft.getCurrentRound());
        pick.setPickNumber(calculatePickNumber(draft));
        pick.setPickTime(LocalDateTime.now());
        
        draft.getPicks().add(pick);

        // Update draft state
        updateDraftState(draft);
        
        return draftRepository.save(draft);
    }
    
    @Override
    public List<Runner> getAvailableRunners(Draft draft) {
        Set<Runner> draftedRunners = draft.getPicks().stream()
                .map(DraftPick::getRunner)
                .collect(Collectors.toSet());
        
        return runnerRepository.findAll().stream()
                .filter(runner -> !draftedRunners.contains(runner))
                .collect(Collectors.toList());
    }
    
    @Override
    public Team getCurrentTeam(Draft draft) {
        if (draft.isComplete()) {
            return null;
        }
        
        int pickInRound = draft.getCurrentPick();
        List<Long> draftOrder = draft.getDraftOrder();
        
        if (draft.getSnakeOrder() && draft.getCurrentRound() % 2 == 0) {
            // Reverse order for even rounds in snake draft
            pickInRound = draftOrder.size() - pickInRound + 1;
        }
        
        Long teamId = draftOrder.get(pickInRound - 1);
        return draft.getLeague().getTeams().stream()
                .filter(team -> team.getId().equals(teamId))
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public boolean isTeamTurn(Draft draft, Team team) {
        Team currentTeam = getCurrentTeam(draft);
        return currentTeam != null && currentTeam.getId().equals(team.getId());
    }

    @Override
    public Page<DraftPick> findDraftPicksByDraft(Draft draft, Long teamId, Pageable pageable) {
        return teamId == null ? draftPickRepository.findDraftPicksByDraft(draft, pageable) :
                draftPickRepository.findDraftPicksByDraftAndTeamId(draft, teamId, pageable);
    }
    
    private int calculatePickNumber(Draft draft) {
        return (draft.getCurrentRound() - 1) * draft.getLeague().getTeams().size() + draft.getCurrentPick();
    }
    
    private void updateDraftState(Draft draft) {
        int teamsCount = draft.getLeague().getTeams().size();
        
        // Increment pick number
//        draft.setCurrentPick(draft.getCurrentPick() + 1);
//
//        // If we've reached the end of the round
//        if (draft.getCurrentPick() > teamsCount) {
//            draft.setCurrentRound(draft.getCurrentRound() + 1);
//            draft.setCurrentPick(1);
//
//            // If we've completed all rounds
//            if (draft.getCurrentRound() > draft.getNumberOfRounds()) {
//                draft.setIsComplete(true);
//            }
//        }
    }
} 