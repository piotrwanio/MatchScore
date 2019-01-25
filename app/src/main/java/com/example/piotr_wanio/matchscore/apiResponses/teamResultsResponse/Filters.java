
package com.example.piotr_wanio.matchscore.apiResponses.teamResultsResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Filters {

    @SerializedName("permission")
    @Expose
    private String permission;
    @SerializedName("limit")
    @Expose
    private Integer limit;

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

}
