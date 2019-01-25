
package com.example.piotr_wanio.matchscore.apiResponses.teamResultsResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Score {

    @SerializedName("winner")
    @Expose
    private String winner;
    @SerializedName("duration")
    @Expose
    private String duration;
    @SerializedName("fullTime")
    @Expose
    private FullTime fullTime;
    @SerializedName("halfTime")
    @Expose
    private HalfTime halfTime;
    @SerializedName("extraTime")
    @Expose
    private ExtraTime extraTime;
    @SerializedName("penalties")
    @Expose
    private Penalties penalties;

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public FullTime getFullTime() {
        return fullTime;
    }

    public void setFullTime(FullTime fullTime) {
        this.fullTime = fullTime;
    }

    public HalfTime getHalfTime() {
        return halfTime;
    }

    public void setHalfTime(HalfTime halfTime) {
        this.halfTime = halfTime;
    }

    public ExtraTime getExtraTime() {
        return extraTime;
    }

    public void setExtraTime(ExtraTime extraTime) {
        this.extraTime = extraTime;
    }

    public Penalties getPenalties() {
        return penalties;
    }

    public void setPenalties(Penalties penalties) {
        this.penalties = penalties;
    }

}
