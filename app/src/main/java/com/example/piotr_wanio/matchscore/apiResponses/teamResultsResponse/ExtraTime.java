
package com.example.piotr_wanio.matchscore.apiResponses.teamResultsResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ExtraTime {

    @SerializedName("homeTeam")
    @Expose
    private Object homeTeam;
    @SerializedName("awayTeam")
    @Expose
    private Object awayTeam;

    public Object getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(Object homeTeam) {
        this.homeTeam = homeTeam;
    }

    public Object getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(Object awayTeam) {
        this.awayTeam = awayTeam;
    }

}
