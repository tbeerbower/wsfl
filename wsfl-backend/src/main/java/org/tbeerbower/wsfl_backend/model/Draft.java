package org.tbeerbower.wsfl_backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "drafts")
public class Draft {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "league_id")
    private League league;
    
    private Integer season;
    private Integer numberOfRounds;
    private Boolean snakeOrder;
    private LocalDateTime startTime;
    private Boolean isComplete;
    private Integer currentRound;
    private Integer currentPick;
    
    @OneToMany(mappedBy = "draft", cascade = CascadeType.ALL)
    @OrderBy("pickNumber")
    private List<DraftPick> picks = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "draft_order", 
                    joinColumns = @JoinColumn(name = "draft_id"))
    @Column(name = "team_id")
    @OrderColumn(name = "position")
    private List<Long> draftOrder = new ArrayList<>();
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public League getLeague() { return league; }
    public void setLeague(League league) { this.league = league; }
    
    public Integer getSeason() { return season; }
    public void setSeason(Integer season) { this.season = season; }
    
    public Integer getNumberOfRounds() { return numberOfRounds; }
    public void setNumberOfRounds(Integer numberOfRounds) { this.numberOfRounds = numberOfRounds; }
    
    public Boolean getSnakeOrder() { return snakeOrder; }
    public void setSnakeOrder(Boolean snakeOrder) { this.snakeOrder = snakeOrder; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    
    public Boolean getIsComplete() { return isComplete; }
    public void setIsComplete(Boolean isComplete) { this.isComplete = isComplete; }
    
    public Integer getCurrentRound() { return currentRound; }
    public void setCurrentRound(Integer currentRound) { this.currentRound = currentRound; }
    
    public Integer getCurrentPick() { return currentPick; }
    public void setCurrentPick(Integer currentPick) { this.currentPick = currentPick; }
    
    public List<DraftPick> getPicks() { return picks; }
    public void setPicks(List<DraftPick> picks) { this.picks = picks; }
    
    public List<Long> getDraftOrder() { return draftOrder; }
    public void setDraftOrder(List<Long> draftOrder) { this.draftOrder = draftOrder; }
} 