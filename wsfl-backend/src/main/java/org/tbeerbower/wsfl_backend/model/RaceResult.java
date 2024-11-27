package org.tbeerbower.wsfl_backend.model;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "race_results")
public class RaceResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Integer genderPlace;

    @Column(name = "finish_time")
    private LocalTime finishTime;
    
    @ManyToOne
    @JoinColumn(name = "race_id")
    private Race race;
    
    @ManyToOne
    @JoinColumn(name = "runner_id")
    private Runner runner;
    
    // Default constructor
    public RaceResult() {
    }
    
    // Constructor for DTO conversion
    public RaceResult(Long id, Integer genderPlace, LocalTime finishTime, Race race, Runner runner) {
        this.id = id;
        this.genderPlace = genderPlace;
        this.finishTime = finishTime;
        this.race = race;
        this.runner = runner;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getGenderPlace() {
        return genderPlace;
    }

    public void setGenderPlace(Integer genderPlace) {
        this.genderPlace = genderPlace;
    }

    public LocalTime getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(LocalTime finishTime) {
        this.finishTime = finishTime;
    }

    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        this.race = race;
    }

    public Runner getRunner() {
        return runner;
    }

    public void setRunner(Runner runner) {
        this.runner = runner;
    }
}