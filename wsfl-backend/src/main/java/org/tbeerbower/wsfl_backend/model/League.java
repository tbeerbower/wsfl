package org.tbeerbower.wsfl_backend.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

    public List<Standing> getStandings(Draft draft) {

        Map<Team, Standing> teamStandings = new HashMap<>();

        for( Matchup matchup : draft.getMatchups()) {

            if (matchup.isComplete()) {
                Standing team1Standing = teamStandings.computeIfAbsent(matchup.getTeam1(), k -> new Standing(matchup.getTeam1(), 0, 0, 0, 0));
                Standing team2Standing = teamStandings.computeIfAbsent(matchup.getTeam2(), k -> new Standing(matchup.getTeam2(), 0, 0, 0, 0));
                team1Standing.setTotalScore(team1Standing.getTotalScore() + matchup.getTeam1Score());
                team2Standing.setTotalScore(team2Standing.getTotalScore() + matchup.getTeam2Score());

                if (matchup.getTeam1Score().equals(matchup.getTeam2Score())) {
                    team1Standing.setTies(team1Standing.getTies() + 1);
                    team2Standing.setTies(team2Standing.getTies() + 1);
                } else if (matchup.getTeam1Score() < matchup.getTeam2Score()) {
                    team1Standing.setWins(team1Standing.getWins() + 1);
                    team2Standing.setLosses(team2Standing.getLosses() + 1);
                } else {
                    team1Standing.setLosses(team1Standing.getLosses() + 1);
                    team2Standing.setWins(team2Standing.getWins() + 1);
                }
            }
        }
        return teamStandings.values().stream().sorted().toList();
    }
} 