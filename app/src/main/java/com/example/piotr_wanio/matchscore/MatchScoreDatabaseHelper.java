package com.example.piotr_wanio.matchscore;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.piotr_wanio.matchscore.apiResponses.standingsResponse.StandingsResponse;
import com.example.piotr_wanio.matchscore.apiResponses.standingsResponse.Table;

import java.util.List;

/**
 * Created by Piotr_Wanio on 01.04.2018.
 */

public class MatchScoreDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "matchScoreDB";
    private static final int DB_VERSION = 1;
    private static StandingsResponse teamsList;

    MatchScoreDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
//        SimpleService service = new SimpleService();
//        try {
//            teamsList = service.getStandingsResponse(db, 2012);
//        }catch (Exception e){
//            System.out.println("Service exception :/");
//        }

        db.execSQL("CREATE TABLE StandingsEngland (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "ID INTEGER, "
                + "NAME TEXT, "
                + "SHORTNAME TEXT, "
                + "POINTS INTEGER, "
                + "GOALS_SCORED INTEGER, "
                + "GOALS_LOOSED INTEGER, "
                + "IMAGE_RESOURCE TEXT);");

        db.execSQL("CREATE TABLE StandingsGermany (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "ID INTEGER, "
                + "NAME TEXT, "
                + "SHORTNAME TEXT, "
                + "POINTS INTEGER, "
                + "GOALS_SCORED INTEGER, "
                + "GOALS_LOOSED INTEGER, "
                + "IMAGE_RESOURCE TEXT);");

        db.execSQL("CREATE TABLE StandingsSpain (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "ID INTEGER, "
                + "NAME TEXT, "
                + "SHORTNAME TEXT, "
                + "POINTS INTEGER, "
                + "GOALS_SCORED INTEGER, "
                + "GOALS_LOOSED INTEGER, "
                + "IMAGE_RESOURCE TEXT);");

        db.execSQL("CREATE TABLE Team (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "ID INTEGER, "
                + "NAME TEXT, "
                + "SHORTNAME TEXT, "
                + "FOUNDED INTEGER, "
                + "ADDRESS TEXT, "
                + "VENUE TEXT, "
                + "WEBSITE TEXT, "
                + "IMAGE_RESOURCE TEXT);");

        if(teamsList != null){
            for (Table team : teamsList.getStandings().get(0).getTable()) {
                insertTeam(db,team.getTeam().getName(),"",(int)team.getPoints(),0,0,0);
            }
        }

        db.execSQL("CREATE TABLE Result (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "ID INTEGER, "
                + "LEAGUE_ID INTEGER, "
                + "LEAGUE_WEEK INTEGER,"
                + "HOME_TEAM TEXT, "
                + "AWAY_TEAM TEXT, "
                + "STATUS TEXT, "
                + "MATCH_DATE TEXT,"
                + "LAST_UPDATED TEXT,"
                + "IS_FOLLOWED TEXT, "
                + "GOALS_HOME INTEGER, "
                + "GOALS_AWAY INTEGER);");

        db.execSQL("CREATE TABLE Player (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "NAME TEXT, "
                + "ID INTEGER,"
                + "TEAM_ID INTEGER,"
                + "POSITION TEXT, "
                + "NATIONALITY TEXT, "
                + "SHIRT_NUMBER INTEGER, "
                + "BIRTH_DATE TEXT);");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.delete("TEAM","1",null);
        db.execSQL("UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME='TEAM';");
//        SimpleService service = new SimpleService();
//        try {
//            teamsList = service.getStandingsResponse(db, 2012);
//        }catch (Exception e){
//            System.out.println("Service exception :/");
//        }
        if(teamsList != null){
            for (Table team : teamsList.getStandings().get(0).getTable()) {
                insertTeam(db,team.getTeam().getName(),"",(int)team.getPoints(),0,0,0);
            }
        }
    }


    public void updateStandings(SQLiteDatabase db, int leagueId){
        db.delete("TEAM","1",null);
        db.execSQL("UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME='TEAM';");
//        SimpleService service = new SimpleService();
//        try {
//            teamsList = service.getStandingsResponse(db, 2012);
//        }catch (Exception e){
//            System.out.println("Service exception :/");
//        }
        if(teamsList != null){
            for (Table team : teamsList.getStandings().get(0).getTable()) {
                insertTeam(db,team.getTeam().getName(),"",(int)team.getPoints(),0,0,0);
            }
        }
    }

    private static void insertTeam(SQLiteDatabase db, String name, String shortname,
                                   int points, int goalsScored, int goalsLoosed, int resourceID){
        ContentValues teamValues = new ContentValues();
        teamValues.put("NAME", name);
        teamValues.put("SHORTNAME", shortname);
        teamValues.put("POINTS", points);
        teamValues.put("GOALS_SCORED", goalsScored);
        teamValues.put("GOALS_LOOSED", goalsLoosed);
        teamValues.put("IMAGE_RESOURCE_ID", resourceID);
        db.insert("StandingsEngland", null, teamValues);
    }

    private static void insertResult(SQLiteDatabase db, String teamA, String teamB,
                                   int goalsA, int goalsB){
        ContentValues teamValues = new ContentValues();
        teamValues.put("TEAM_A", teamA);
        teamValues.put("TEAM_B", teamB);
        teamValues.put("GOALS_A", goalsA);
        teamValues.put("GOALS_B", goalsB);
        db.insert("ResultsEngland", null, teamValues);
    }


}
