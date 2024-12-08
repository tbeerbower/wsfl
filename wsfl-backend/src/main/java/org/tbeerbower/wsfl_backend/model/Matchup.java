package org.tbeerbower.wsfl_backend.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.stream.Collectors;


@Entity
@Table(name = "matchups")
public class Matchup extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean isPlayoff;
    private boolean isChampionship;

    @ManyToOne
    @JoinColumn(name = "race_id")
    private Race race;

    @ManyToOne
    @JoinColumn(name = "team1_id")
    private Team team1;

    @ManyToOne
    @JoinColumn(name = "team2_id")
    private Team team2;

    @ManyToOne
    @JoinColumn(name = "draft_id")
    private Draft draft;

    // Default constructor
    public Matchup() {
    }

    // Constructor for DTO conversion
    public Matchup(Long id, Race race, Team team1, Team team2, Draft draft, boolean isPlayoff, boolean isChampionship) {
        this.id = id;
        this.race = race;
        this.team1 = team1;
        this.team2 = team2;
        this.draft = draft;
        this.isPlayoff = isPlayoff;
        this.isChampionship = isChampionship;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        this.race = race;
    }

    public Team getTeam1() {
        return team1;
    }

    public void setTeam1(Team team1) {
        this.team1 = team1;
    }

    public Team getTeam2() {
        return team2;
    }

    public void setTeam2(Team team2) {
        this.team2 = team2;
    }

    public boolean includes(Team team) {
        return team1.equals(team) || team2.equals(team);
    }

    public boolean includes(User user) {
        return team1.getOwner().equals(user) || team2.getOwner().equals(user);
    }

    @Transient
    public Integer getTeam1Score() {
        return getTeamScore(team1);
    }

    @Transient
    public Integer getTeam2Score() {
        return getTeamScore(team2);
    }

    public Draft getDraft() {
        return draft;
    }

    public void setDraft(Draft draft) {
        this.draft = draft;
    }

    public boolean isPlayoff() {
        return isPlayoff;
    }

    public void setPlayoff(boolean playoff) {
        isPlayoff = playoff;
    }

    public boolean isChampionship() {
        return isChampionship;
    }

    public void setChampionship(boolean championship) {
        isChampionship = championship;
    }

    @Transient
    public Boolean isComplete() {
        return !race.getResults().isEmpty();
    }

    @Transient
    public Team getWinner(boolean includeTies) {
        return isComplete() ? getTeam1Score() > getTeam2Score() ? team1 :
                getTeam2Score() > getTeam1Score() ? team2 :
                        includeTies ? null :
                                team1.getTotalScore() > team2.getTotalScore() ? team1 :
                                        team2.getTotalScore() > team1.getTotalScore() ? team2 :
                                                null : null;
    }

    // TODO: move to service layer
    private Integer getTeamScore(Team team) {
        List<Long> teamRunnerIds = draft.getPicks().stream()
                .filter(pick -> pick.getTeam().getId().equals(team.getId()))
                .map(pick -> pick.getRunner().getId())
                .toList();

        Integer score = 0;
        for (RaceResult result : race.getResults()) {
            if (teamRunnerIds.contains(result.getRunner().getId())) {
                score += result.getGenderPlace();
            }
        }
        return score;
    }
}