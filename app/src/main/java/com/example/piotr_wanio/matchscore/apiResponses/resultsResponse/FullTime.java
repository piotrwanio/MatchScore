
package com.example.piotr_wanio.matchscore.apiResponses.resultsResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FullTime {

    @SerializedName("homeTeam")
    @Expose
    private Integer homeTeam;
    @SerializedName("awayTeam")
    @Expose
    private Integer awayTeam;

    public Integer getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(Integer homeTeam) {
        this.homeTeam = homeTeam;
    }

    public Integer getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(Integer awayTeam) {
        this.awayTeam = awayTeam;
    }

}
