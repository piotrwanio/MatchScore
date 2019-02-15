
package com.example.piotr_wanio.matchscore.apiResponses.standingsResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Table {

    @SerializedName("position")
    @Expose
    private Integer position;
    @SerializedName("team")
    @Expose
    private Team team;
    @SerializedName("playedGames")
    @Expose
    private Integer playedGames;
    @SerializedName("won")
    @Expose
    private Integer won;
    @SerializedName("draw")
    @Expose
    private Integer draw;
    @SerializedName("lost")
    @Expose
    private Integer lost;
    @SerializedName("points")
    @Expose
    private Integer points;
    @SerializedName("goalsFor")
    @Expose
    private Integer goalsFor;
    @SerializedName("goalsAgainst")
    @Expose
    private Integer goalsAgainst;
    @SerializedName("goalDifference")
    @Expose
    private Integer goalDifference;

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Integer getPlayedGames() {
        return playedGames;
    }

    public void setPlayedGames(Integer playedGames) {
        this.playedGames = playedGames;
    }

    public Integer getWon() {
        return won;
    }

    public void setWon(Integer won) {
        this.won = won;
    }

    public Integer getDraw() {
        return draw;
    }

    public void setDraw(Integer draw) {
        this.draw = draw;
    }

    public Integer getLost() {
        return lost;
    }

    public void setLost(Integer lost) {
        this.lost = lost;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getGoalsFor() {
        return goalsFor;
    }

    public void setGoalsFor(Integer goalsFor) {
        this.goalsFor = goalsFor;
    }

    public Integer getGoalsAgainst() {
        return goalsAgainst;
    }

    public void setGoalsAgainst(Integer goalsAgainst) {
        this.goalsAgainst = goalsAgainst;
    }

    public Integer getGoalDifference() {
        return goalDifference;
    }

    public void setGoalDifference(Integer goalDifference) {
        this.goalDifference = goalDifference;
    }

}
