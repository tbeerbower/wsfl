package org.tbeerbower.wsfl_backend.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "teams")
public class Team extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne
    @JoinColumn(name = "league_id")
    private League league;

    @ManyToMany
    @JoinTable(
            name = "draft_order",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "draft_id")
    )
    private Set<Draft> drafts = new HashSet<>();


    @OneToMany(mappedBy = "team1")
    private List<Matchup> homeMatchups = new ArrayList<>();

    @OneToMany(mappedBy = "team2")
    private List<Matchup> awayMatchups = new ArrayList<>();


    // Default constructor
    public Team() {
    }

    // Constructor for DTO conversion
    public Team(Long id, String name) {
        this.id = id;
        this.name = name;
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

    public Integer getWins() {
        int wins = 0;
        for (Matchup matchup : getAllMatchups()) {
            if (matchup.isComplete()) {
                Integer team1Score = matchup.getTeam1Score();
                Integer team2Score = matchup.getTeam2Score();
                if (team1Score != team2Score) {
                    if ((team1Score > team2Score && matchup.getTeam1().equals(this)) ||
                            matchup.getTeam2().equals(this)) {
                        wins++;
                    }
                }
            }
        }
        return wins;
    }

    public Integer getLosses() {
        int losses = 0;
        for (Matchup matchup : getAllMatchups()) {
            if (matchup.isComplete()) {
                Integer team1Score = matchup.getTeam1Score();
                Integer team2Score = matchup.getTeam2Score();
                if (team1Score != team2Score) {
                    if ((team1Score < team2Score && matchup.getTeam1().equals(this)) ||
                            matchup.getTeam2().equals(this)) {
                        losses++;
                    }
                }
            }
        }
        return losses;
    }

    public Integer getTies() {
        int ties = 0;
        for (Matchup matchup : getAllMatchups()) {
            if (matchup.isComplete()) {
                if (matchup.getTeam1Score() == matchup.getTeam2Score()) {
                    ties++;
                }
            }
        }
        return ties;
    }

    public Integer getTotalScore() {
        int totalScore = 0;
        for (Matchup matchup : getAllMatchups()) {
            if (matchup.isComplete()) {
                totalScore += (matchup.getTeam1().equals(this)) ?
                        matchup.getTeam1Score() :
                        matchup.getTeam2Score();
            }
        }
        return totalScore;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public League getLeague() {
        return league;
    }

    public void setLeague(League league) {
        this.league = league;
    }

    public Set<Draft> getDrafts() {
        return drafts;
    }

    public void setDrafts(Set<Draft> drafts) {
        this.drafts = drafts;
    }

    public List<Matchup> getHomeMatchups() {
        return homeMatchups;
    }

    public void setHomeMatchups(List<Matchup> homeMatchups) {
        this.homeMatchups = homeMatchups;
    }


    public List<Matchup> getAwayMatchups() {
        return awayMatchups;
    }

    public void setAwayMatchups(List<Matchup> awayMatchups) {
        this.awayMatchups = awayMatchups;
    }

    @Transient
    public List<Matchup> getAllMatchups() {
        List<Matchup> allMatchups = new ArrayList<>();
        allMatchups.addAll(homeMatchups);
        allMatchups.addAll(awayMatchups);
        return allMatchups;
    }
}
