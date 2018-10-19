package com.example.piotr_wanio.matchscore;

/**
 * Created by Piotr_Wanio on 24.03.2018.
 */

public class Team {
    private String name;
    private String shortName;
    private int points;
    private int goalsScored;
    private int goalsLoosed;
    private int imageResourceId;



    public Team(String name, String shortName, int points, int goalsScored, int goalsLoosed, int imageResourceId){
        this.name = name;
        this.shortName = shortName;
        this.points = points;
        this.goalsScored = goalsScored;
        this.goalsLoosed = goalsLoosed;
        this.imageResourceId = imageResourceId;
    }

    public String getName(){
        return name;
    }

    public String getShortName(){
        return shortName;
    }

    public int getPoints(){
        return points;
    }

    public int getGoalsScored(){
        return goalsScored;
    }

    public int getGoalsLoosed(){
        return goalsLoosed;
    }

    public int getImageResourceId(){
        return imageResourceId;
    }

    public String toString(){
        return this.name;
    }

    public void addpoints(String result){
        if(result == "win") {
            this.points += 3;
        }
        else if(result == "draw"){
            this.points++;
        }
    }

}
