package org.tbeerbower.wsfl_backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "drafts")
public class Draft extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "league_id")
    private League league;

    private String name;
    @ManyToOne
    @JoinColumn(name = "season_id")
    private Season season;
    private Integer numberOfRounds;
    private Boolean snakeOrder;
    private LocalDateTime startTime;

    
    @OneToMany(mappedBy = "draft", cascade = CascadeType.ALL)
    @OrderBy("pickNumber")
    private List<DraftPick> picks = new ArrayList<>();

    @OneToMany(mappedBy = "draft", cascade = CascadeType.ALL)
    private List<Matchup> matchups = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "draft_order", 
                    joinColumns = @JoinColumn(name = "draft_id"))
    @Column(name = "team_id")
    @OrderColumn(name = "position")
    private List<Long> draftOrder = new ArrayList<>();

    @ManyToMany(mappedBy = "drafts")
    private Set<Team> teams = new HashSet<>();

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public League getLeague() { return league; }
    public void setLeague(League league) { this.league = league; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Season getSeason() { return season; }
    public void setSeason(Season season) { this.season = season; }

    
    public Integer getNumberOfRounds() { return numberOfRounds; }
    public void setNumberOfRounds(Integer numberOfRounds) { this.numberOfRounds = numberOfRounds; }
    
    public Boolean getSnakeOrder() { return snakeOrder; }
    public void setSnakeOrder(Boolean snakeOrder) { this.snakeOrder = snakeOrder; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public Boolean isStarted() {
        return !draftOrder.isEmpty();
    }

    public Boolean isComplete() {
        return isStarted() && picks.size() / teams.size() >= numberOfRounds;
    }

    public String getStatus() {
        if (isComplete()) {
            return "Complete";
        } else if (isStarted()) {
            return "In Progress";
        } else {
            return "Not Started";
        }
    }

    public boolean includes(User user) {
        return teams.stream().anyMatch(team -> team.getOwner().equals(user));
    }

    public Integer getCurrentRound() {
        return isStarted() ? (isComplete() ? numberOfRounds : picks.size() / teams.size() + 1) : 0;
    }

    public Integer getCurrentPick() {
        return isStarted() ? (isComplete() ? teams.size() : picks.size() % teams.size() + 1) : 0;
    }

    public List<DraftPick> getPicks() { return picks; }
    public void setPicks(List<DraftPick> picks) { this.picks = picks; }

    public List<Matchup> getMatchups() { return matchups; }
    public void setMatchups(List<Matchup> matchups) { this.matchups = matchups; }
    
    public List<Long> getDraftOrder() { return draftOrder; }
    public void setDraftOrder(List<Long> draftOrder) { this.draftOrder = draftOrder; }

    public Set<Team> getTeams() { return teams; }
    public void setTeams(Set<Team> teams) { this.teams = teams; }

}