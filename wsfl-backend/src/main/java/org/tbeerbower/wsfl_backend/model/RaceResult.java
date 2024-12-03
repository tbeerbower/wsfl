package org.tbeerbower.wsfl_backend.model;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "race_results")
public class RaceResult extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "gender_place")
    private Integer genderPlace;

    @Column(name = "overall_place")
    private Integer overallPlace;

    @Column(name = "time")
    private String time;

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
    public RaceResult(Long id, Integer genderPlace, Integer overallPlace, String time, Race race, Runner runner) {
        this.id = id;
        this.genderPlace = genderPlace;
        this.overallPlace = overallPlace;
        this.time = time;
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

    public Integer getOverallPlace() {
        return overallPlace;
    }

    public void setOverallPlace(Integer overallPlace) {
        this.overallPlace = overallPlace;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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