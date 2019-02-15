
package com.example.piotr_wanio.matchscore.apiResponses.standingsResponse;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Standing {

    @SerializedName("stage")
    @Expose
    private String stage;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("group")
    @Expose
    private Object group;
    @SerializedName("table")
    @Expose
    private List<Table> table = null;

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getGroup() {
        return group;
    }

    public void setGroup(Object group) {
        this.group = group;
    }

    public List<Table> getTable() {
        return table;
    }

    public void setTable(List<Table> table) {
        this.table = table;
    }

}
