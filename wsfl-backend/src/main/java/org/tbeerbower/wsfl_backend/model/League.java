package org.tbeerbower.wsfl_backend.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "leagues")
public class League extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private Integer maxTeams;
    
    @ManyToOne
    @JoinColumn(name = "admin_id")
    private User admin;
    
    @OneToMany(mappedBy = "league")
    private Set<Team> teams = new HashSet<>();

    @OneToMany(mappedBy = "league")
    private Set<Draft> drafts = new HashSet<>();


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


    public Integer getMaxTeams() {
        return maxTeams;
    }

    public void setMaxTeams(Integer maxTeams) {
        this.maxTeams = maxTeams;
    }

    public User getAdmin() {
        return admin;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }

    public Set<Team> getTeams() {
        return teams;
    }

    public void setTeams(Set<Team> teams) {
        this.teams = teams;
    }

    public Set<Draft> getDrafts() {
        return drafts;
    }

    public void setDrafts(Set<Draft> drafts) {
        this.drafts = drafts;
    }
} 