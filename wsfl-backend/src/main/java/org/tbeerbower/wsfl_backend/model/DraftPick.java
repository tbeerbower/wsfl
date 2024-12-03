package org.tbeerbower.wsfl_backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "draft_picks")
public class DraftPick extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "draft_id")
    private Draft draft;
    
    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;
    
    @ManyToOne
    @JoinColumn(name = "runner_id")
    private Runner runner;
    
    private Integer round;
    private Integer pickNumber;
    private LocalDateTime pickTime;
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Draft getDraft() { return draft; }
    public void setDraft(Draft draft) { this.draft = draft; }
    
    public Team getTeam() { return team; }
    public void setTeam(Team team) { this.team = team; }
    
    public Runner getRunner() { return runner; }
    public void setRunner(Runner runner) { this.runner = runner; }
    
    public Integer getRound() { return round; }
    public void setRound(Integer round) { this.round = round; }
    
    public Integer getPickNumber() { return pickNumber; }
    public void setPickNumber(Integer pickNumber) { this.pickNumber = pickNumber; }
    
    public LocalDateTime getPickTime() { return pickTime; }
    public void setPickTime(LocalDateTime pickTime) { this.pickTime = pickTime; }
} 