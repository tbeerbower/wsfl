package org.tbeerbower.wsfl_backend.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.stream.Collectors;


@Entity
@Table(name = "matchups")
public class Matchup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "race_id")
    private Race race;

    @ManyToOne
    @JoinColumn(name = "team1_id")
    private Team team1;

    @ManyToOne
    @JoinColumn(name = "team2_id")
    private Team team2;

    @Column(name = "team1score")
    private Integer team1Score;

    @Column(name = "team2score")
    private Integer team2Score;

    @ManyToOne
    @JoinColumn(name = "draft_id")
    private Draft draft;

    // Default constructor
    public Matchup() {
    }

    // Constructor for DTO conversion
    public Matchup(Long id, Race race, Team team1, Team team2, Integer team1Score, Integer team2Score, Draft draft) {
        this.id = id;
        this.race = race;
        this.team1 = team1;
        this.team2 = team2;
        this.team1Score = team1Score;
        this.team2Score = team2Score;
        this.draft = draft;
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

    public Integer getTeam1Score() {
      //  return team1Score;
        return getTeamScore(team1);
    }

    public void setTeam1Score(Integer team1Score) {
        this.team1Score = team1Score;
    }

    public Integer getTeam2Score() {
        return getTeamScore(team2);
     //   return team2Score;
    }

    public void setTeam2Score(Integer team2Score) {
        this.team2Score = team2Score;
    }

    public Draft getDraft() {
        return draft;
    }

    public void setDraft(Draft draft) {
        this.draft = draft;
    }

    public Boolean isComplete() {
        return !race.getResults().isEmpty();
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