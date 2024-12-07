package org.tbeerbower.wsfl_backend.model;

public class Standing implements Comparable<Standing>{
    private Team team;
    private int wins;
    private int losses;
    private int ties;
    private int totalScore;

    public Standing() {
    }

    public Standing(Team team, int wins, int losses, int ties, int totalScore) {
        this.team = team;
        this.wins = wins;
        this.losses = losses;
        this.ties = ties;
        this.totalScore = totalScore;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getTies() {
        return ties;
    }

    public void setTies(int ties) {
        this.ties = ties;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public int getTotalRaces() {
        return wins + losses + ties;
    }

    public double getWinPercentage() {
        return (2.0 * wins + ties) / (2.0 * getTotalRaces()) * 100.0;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    @Override
    public int compareTo(Standing o) {
        if (getWinPercentage() == o.getWinPercentage()) {
            return getTotalScore() - o.getTotalScore();
        } else if (getWinPercentage() < o.getWinPercentage()) {
            return 1;
        }
        return -1;
    }
}
