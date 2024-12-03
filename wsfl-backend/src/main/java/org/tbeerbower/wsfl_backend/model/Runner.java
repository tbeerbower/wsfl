package org.tbeerbower.wsfl_backend.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "runners")
public class Runner extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String gender;

    @OneToMany(mappedBy = "runner")
    private Set<RaceResult> results = new HashSet<>();

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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Set<RaceResult> getResults() {
        return results;
    }

    public void setResults(Set<RaceResult> results) {
        this.results = results;
    }

    public Double getAverageOverallPlace() {
        return results.stream()
                .mapToInt(RaceResult::getOverallPlace)
                .average()
                .orElse(0.0);
    }

    public Double getAverageGenderPlace() {
        return results.stream()
                .mapToInt(RaceResult::getGenderPlace)
                .average()
                .orElse(0.0);
    }

    public Integer getNumberOfRaces() {
        return results.size();
    }
}