package com.example.piotr_wanio.matchscore;

/**
 * Created by Piotr_Wanio on 26.03.2018.
 */

public class Result {
    private Team teamA;
    private Team teamB;
    private int goalsA;
    private int goalsB;

/*
    static {
        results = new Result[]{
                new Result(new Team("Arsenal", "ARS", 0, 0, 0, 0), new Team("Real Madrit", "RM", 0, 0, 0, 0), 5, 2),
                new Result(new Team("Arsenal", "ARS", 0, 0, 0, 0), new Team("Real Madrit", "RM", 0, 0, 0, 0), 3, 3),
                new Result(new Team("Arsenal", "ARS", 0, 0, 0, 0), new Team("Real Madrit", "RM", 0, 0, 0, 0), 4, 4)
        };
    }
*/
    public Result(Team teamA, Team teamB, int goalsA, int goalsB){
        this.teamA = teamA;
        this.teamB = teamB;
        this.goalsA = goalsA;
        this.goalsB = goalsB;
        if(goalsA>goalsB){
            teamA.addpoints("win");
        }
        else if(goalsA == goalsB){
            teamA.addpoints("draw");
            teamB.addpoints("draw");
        }
    }

    public Team getTeamA(){
        return this.teamA;
    }

    public Team getTeamB(){
        return this.teamB;
    }

    public int getGoalsA(){
        return this.goalsA;
    }

    public int getGoalsB() {
        return this.goalsB;
    }

    @Override
    public String toString(){
        String ss = this.teamA + " "+ this.goalsA + " : " + this.goalsB + " " + this.teamB;
        return ss;
    }
}
