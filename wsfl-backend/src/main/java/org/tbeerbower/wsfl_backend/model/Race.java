package org.tbeerbower.wsfl_backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "races")
public class Race {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private LocalDate date;
    private Boolean isPlayoff;

    @ManyToOne
    @JoinColumn(name = "season_id")
    private Season season;
    
    @OneToMany(mappedBy = "race")
    private Set<RaceResult> results = new HashSet<>();
    
    // Default constructor
    public Race() {
    }
    
    // Constructor for DTO conversion
    public Race(Long id, String name, LocalDate date, Boolean isPlayoff) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.isPlayoff = isPlayoff;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Boolean getIsPlayoff() {
        return isPlayoff;
    }

    public void setIsPlayoff(Boolean isPlayoff) {
        this.isPlayoff = isPlayoff;
    }

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public Set<RaceResult> getResults() {
        return results;
    }

    public void setResults(Set<RaceResult> results) {
        this.results = results;
    }
} 