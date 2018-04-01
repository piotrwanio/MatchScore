package com.example.piotr_wanio.matchscore;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Piotr_Wanio on 01.04.2018.
 */

public class MatchScoreDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "matchScore";
    private static final int DB_VERSION = 1;

    MatchScoreDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE TEAM (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "NAME TEXT, "
                + "SHORTNAME TEXT, "
                + "POINTS INTEGER, "
                + "GOALS_SCORED INTEGER, "
                + "GOALS_LOOSED INTEGER, "
                + "IMAGE_RESOURCE_ID INTEGER);");
        insertTeam(db,"Arsenal FC","AFC",0,0,0,0);
        insertTeam(db,"Real Madrit CF","REA",0,0,0,0);
        insertTeam(db,"FC Barcelona","FCB",0,0,0,0);
        insertTeam(db,"Bayern Munchen","BAY",0,0,0,0);

        db.execSQL("CREATE TABLE RESULT (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "TEAM_A TEXT, "
                + "TEAM_B TEXT, "
                + "GOALS_A INTEGER, "
                + "GOALS_B INTEGER);");
        insertResult(db,"Arsenal FC", "Real Madrit CF", 5, 2);
        insertResult(db,"Arsenal FC", "Real Madrit CF", 5, 2);
        insertResult(db,"Arsenal FC", "Real Madrit CF", 5, 2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

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
        db.insert("TEAM", null, teamValues);
    }

    private static void insertResult(SQLiteDatabase db, String teamA, String teamB,
                                   int goalsA, int goalsB){
        ContentValues teamValues = new ContentValues();
        teamValues.put("TEAM_A", teamA);
        teamValues.put("TEAM_B", teamB);
        teamValues.put("GOALS_A", goalsA);
        teamValues.put("GOALS_B", goalsB);
        db.insert("RESULT", null, teamValues);
    }
}
