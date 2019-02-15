package com.example.piotr_wanio.matchscore;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.util.Log;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.example.piotr_wanio.matchscore.apiResponses.resultsResponse.Match;
import com.example.piotr_wanio.matchscore.apiResponses.resultsResponse.ResultsResponse;
import com.example.piotr_wanio.matchscore.apiResponses.standingsResponse.StandingsResponse;
import com.example.piotr_wanio.matchscore.apiResponses.standingsResponse.Table;
import com.example.piotr_wanio.matchscore.apiResponses.teamsResponse.Squad;
import com.example.piotr_wanio.matchscore.apiResponses.teamsResponse.TeamsResponse;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public final class SimpleService {

    private static final String API_URL = "https://api.football-data.org/";
    private static List<Table> table;
    private static List<Match> matches;
    private static StandingsResponse standingsResponse;
    private static ResultsResponse resultsResponse;
    private static TeamsResponse teamsResponse;
    private static Response responseApi;

    private static StandingsResponse teamsList;
    private static SQLiteDatabase database;
    private static MatchScoreDatabaseHelper matchScoreDatabaseHelper;

    private static Activity mActivity;
    private static CursorAdapter adapter;


    public SimpleService(Activity activity){
        this.mActivity = activity;
    }

    public interface StandingsService {
        @Headers("X-Auth-Token: 23377c48baed4a65b9ac16a0499d62e9")
        @GET("v2/competitions/{leagueId}/standings")
        Call<StandingsResponse> standings(@Path("leagueId") int leagueId);
    }

    public interface ResultsService {
        @Headers("X-Auth-Token: 23377c48baed4a65b9ac16a0499d62e9")
        @GET("v2/competitions/{leagueId}/matches")
        Call<ResultsResponse> results(@Path("leagueId") int leagueId);
    }

    public interface TeamsService {
        @Headers("X-Auth-Token: 23377c48baed4a65b9ac16a0499d62e9")
        @GET("v2/teams/{teamId}")
        Call<TeamsResponse> team(@Path("teamId") int teamId);
    }



    public static StandingsResponse getStandingsResponse(CursorAdapter adapter,  final int leagueId) throws IOException {


            // Create a very simple REST adapter which points the AllSports API.
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API_URL)
                    .callbackExecutor(Executors.newSingleThreadExecutor())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            // Create an instance of our Standings API interface.
            StandingsService standingsService = retrofit.create(StandingsService.class);

            // Create a call instance for looking up standings.
            Call<StandingsResponse> call = standingsService.standings(leagueId);


            call.enqueue(new Callback<StandingsResponse>() {
                @Override
                public void onResponse(Call<StandingsResponse> call, Response<StandingsResponse> response) {
                    try {
                        matchScoreDatabaseHelper = MatchScoreDatabaseHelper.getInstance(mActivity.getApplicationContext());
                        database = matchScoreDatabaseHelper.getWritableDatabase();
//                        database.enableWriteAheadLogging();


                        if (response.body() != null && database.isOpen()) {
                            standingsResponse = response.body();
                            responseApi = response;
                            table = standingsResponse.getStandings().get(0).getTable();
                            for (Table team : table) {
                                System.out.println(team.getTeam().getName() + " (" + team.getPoints() + ")");
                            }
                            updateStandings(database, standingsResponse, leagueId);
                            String league = "";
                            switch (leagueId)
                            {
                                case 2021:
                                    league = "StandingsEngland";
                                    break;
                                case 2014:
                                    league = "StandingsSpain";
                                    break;
                                case 2002:
                                    league = "StandingsGermany";
                            }
                            Cursor cursor = database.query(league, new String[]{"_id", "ID", "IMAGE_RESOURCE", "NAME", "POINTS",
                                    "GOALS_SCORED", "GOALS_LOOSED"}, null, null, null, null, "POINTS DESC");

                            if (adapter != null) {
                                Thread thread = new Thread() {
                                    @Override
                                    public void run() {
                                        mActivity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                adapter.changeCursor(cursor);
//                                                database.close();
                                            }
                                        });
                                    }
                                };
                                thread.start();
                            }
                        }
//                        if (database.isOpen()) database.close();


                    }
                    catch (SQLiteException e){

                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast toast = Toast.makeText(mActivity.getApplicationContext(), "Baza danych jest niedostępna! " +e.getMessage(), Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                });
                            }
                        };
                        thread.start();


                    }
                }

                @Override
                public void onFailure(Call<StandingsResponse> call, Throwable t) {
                    if (t instanceof IOException) {
                        System.out.println("Network trouble");
                        System.out.println(t.getMessage());   // logging necessary
                    } else {
                        System.out.println("Enqueue fail...");
                        System.out.println(t.getMessage());   // logging necessary
                    }
                }
            });


        return standingsResponse;

    }

    public static ResultsResponse getResultsResponse(CursorAdapter adapter, final int leagueId, String isFollowed, String scoreKind) throws IOException {

        try {
            matchScoreDatabaseHelper = MatchScoreDatabaseHelper.getInstance(mActivity.getApplicationContext());
            database = matchScoreDatabaseHelper.getWritableDatabase();

            // Create a very simple REST adapter which points the AllSports API.
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API_URL)
                    .callbackExecutor(Executors.newSingleThreadExecutor())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            // Create an instance of our Results API interface.
            ResultsService resultsService = retrofit.create(ResultsService.class);

            // Create a call instance for looking up match results.
            Call<ResultsResponse> call = resultsService.results(leagueId);

            call.enqueue(new Callback<ResultsResponse>() {
                @Override
                public void onResponse(Call<ResultsResponse> call, Response<ResultsResponse> response) {
                    if(response.body() != null) {
                        resultsResponse = response.body();
                        responseApi = response;
                        matches = resultsResponse.getMatches();
                        for (Match match : matches) {
                            System.out.println(match.getHomeTeam().getName() + " (" + match.getScore().getFullTime().toString()+ ")");
                        }
                        if(database.isOpen()) {
                            updateResults(database, resultsResponse, leagueId, isFollowed);
                            String whereClause;
                            String[] whereArgs;
                            if(scoreKind.equals("schedule")) {
                                whereClause = "(STATUS = ? OR STATUS = ? ) AND LEAGUE_ID = ?";
                                whereArgs = new String[] {
                                        "SCHEDULED","IN_PLAY", String.valueOf(leagueId)
                                };
                            }
                            else if(scoreKind.equals("all")) {
                                whereClause = "IS_FOLLOWED = ?";
                                whereArgs = new String[] {
                                        "yes"
                                };
                            }
                            else {
                                whereClause = "STATUS = ? AND LEAGUE_ID = ?";
                                whereArgs = new String[] {
                                        "FINISHED", String.valueOf(leagueId)
                                };
                            }

                            Cursor cursor;
                            if(database.isOpen()) {
                                cursor = database.query("Result", new String[]{"_id", "LEAGUE_WEEK", "HOME_TEAM", "GOALS_HOME", "GOALS_AWAY", "AWAY_TEAM",
                                        "STATUS", "LEAGUE_ID", "IS_FOLLOWED", "MATCH_DATE"}, whereClause, whereArgs, null, null, "LEAGUE_WEEK ASC");
                                Thread thread = new Thread() {
                                    @Override
                                    public void run() {
                                        mActivity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if(adapter!=null) {
                                                    adapter.swapCursor(cursor);
                                                    adapter.notifyDataSetChanged();
//                                                    database.close();
                                                }
                                            }
                                        });
                                    }
                                };
                                thread.start();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResultsResponse> call, Throwable t) {
                    if (t instanceof IOException) {
                        System.out.println("Network trouble");
                        System.out.println(t.getMessage());   // logging necessary
                    } else {
                        System.out.println("Enqueue fail...");
                        System.out.println(t.getMessage());   // logging necessary
                    }
                }
            });
        }
        catch (SQLiteException e){
            Toast toast = Toast.makeText(mActivity.getApplicationContext(), "Baza danych jest niedostępna! " +e.getMessage(), Toast.LENGTH_SHORT);

            toast.show();
        }
        return resultsResponse;
    }


    public static TeamsResponse getTeamsResponse(final int teamId, int leagueId, ImageView teamsLogo, TextView teamsName) throws IOException {
        try {
            matchScoreDatabaseHelper = MatchScoreDatabaseHelper.getInstance(mActivity.getApplicationContext());
            database = matchScoreDatabaseHelper.getReadableDatabase();
            // Create a very simple REST adapter which points the AllSports API.
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API_URL)
                    .callbackExecutor(Executors.newSingleThreadExecutor())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            // Create an instance of our Teams API interface.
            TeamsService teamsService = retrofit.create(TeamsService.class);

            // Create a call instance for looking up team.
            Call<TeamsResponse> call = teamsService.team(teamId);

            call.enqueue(new Callback<TeamsResponse>() {
                @Override
                public void onResponse(Call<TeamsResponse> call, Response<TeamsResponse> response) {
                    if(response.body() != null) {
                        teamsResponse = response.body();
                        responseApi = response;

                        updateTeam(database, teamsResponse, teamId, leagueId);
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateTeamFragment(database, teamId);
//                                        database.close();
                                    }
                                });
                            }
                        };
                        thread.start();
                    }
                }

                @Override
                public void onFailure(Call<TeamsResponse> call, Throwable t) {
                    if (t instanceof IOException) {
                        System.out.println("Network trouble");
                        System.out.println(t.getMessage());   // logging necessary
                    } else {
                        System.out.println("Enqueue fail...");
                        System.out.println(t.getMessage());   // logging necessary
                    }
                }
            });
        }
        catch (SQLiteException e){
            Toast toast = Toast.makeText(mActivity.getApplicationContext(), "Baza danych jest niedostępna! " +e.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }
        return teamsResponse;
    }


    public static void updateStandings(SQLiteDatabase database, StandingsResponse teamsList, int leagueId){

            String league = "";
            switch (leagueId)
            {
                case 2021:
                    league = "StandingsEngland";
                    database.execSQL("UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME='StandingsEngland';");
                    break;

                case 2014:
                    league = "StandingsSpain";
                    database.execSQL("UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME='StandingsSpain';");
                    break;
                case 2002:
                    league = "StandingsGermany";
                    database.execSQL("UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME='StandingsGermany';");
            }
            database.delete(league,"1",null);

            if(teamsList != null){
                for (Table team : teamsList.getStandings().get(0).getTable()) {
                    insertTeam(database, team.getTeam().getId(), league, team.getTeam().getName(),"",(int)team.getPoints(),team.getGoalsFor(),team.getGoalsAgainst(),team.getTeam().getCrestUrl());
                }
            }
    }


    public static void updateTeam(SQLiteDatabase db, TeamsResponse teamsDetails, int teamId, int leagueId){


        ContentValues teamValues = new ContentValues();
        teamValues.put("ID", teamsDetails.getId());
        teamValues.put("LEAGUE_ID", leagueId);

        teamValues.put("NAME", teamsDetails.getName());
        teamValues.put("SHORTNAME", teamsDetails.getShortName());
        teamValues.put("ADDRESS", teamsDetails.getAddress());
        teamValues.put("WEBSITE", teamsDetails.getWebsite());
        teamValues.put("VENUE", teamsDetails.getVenue());
        teamValues.put("FOUNDED", teamsDetails.getFounded());

//        teamValues.put("IMAGE_RESOURCE", name.replaceAll(" ", "_").toLowerCase());
        teamValues.put("IMAGE_RESOURCE", teamsDetails.getCrestUrl());
        for(Squad player : teamsDetails.getSquad()){
            String position, shirtNumber;
            if(player.getPosition() == null) position = "Coach";
            else position = player.getPosition().toString();
            if(player.getShirtNumber() == null) shirtNumber = "";
            else shirtNumber = player.getShirtNumber().toString();
            insertPlayer(db,player.getId(),teamsDetails.getId(),position,player.getNationality(),
                    player.getName(),shirtNumber, player.getDateOfBirth());
        }
        db.insert("Team", null, teamValues);
    }

    public static void updateResults(SQLiteDatabase db, ResultsResponse resultsList, int leagueId, String isFollowed){


        db.execSQL("UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME='RESULT';");
        if(resultsList != null){
            for (Match match : resultsList.getMatches()) {
                int homeGoals = 0;
                int awayGoals = 0;
                if(match.getScore().getFullTime().getHomeTeam() != null && match.getScore().getFullTime().getAwayTeam() != null){
                    homeGoals = match.getScore().getFullTime().getHomeTeam();
                    awayGoals = match.getScore().getFullTime().getAwayTeam();
                }

                if(isFollowed.equals("yes")) {
                    String whereClause = "LEAGUE_ID = ? AND IS_FOLLOWED = ? AND ID = ? ";
                    String[] whereArgs = new String[]{
                            String.valueOf(leagueId), isFollowed, match.getId().toString()
                    };

                    Cursor cursor = null;
                    try {
                        cursor = db.query("Result", new String[]{"_id", "ID", "LEAGUE_WEEK", "HOME_TEAM", "GOALS_HOME",
                                "GOALS_AWAY", "AWAY_TEAM", "STATUS", "LEAGUE_ID", "IS_FOLLOWED"}, whereClause, whereArgs, null, null, null);
                        if(!cursor.isClosed()) cursor.moveToFirst();
                        if (db.isOpen() && cursor.getCount() > 0) {
                            db.delete("Result", whereClause, whereArgs);
                            insertResult(db, leagueId, match.getId(), match.getMatchday(), match.getStatus(), match.getUtcDate(), match.getLastUpdated(), isFollowed,
                                    match.getHomeTeam().getName(), match.getAwayTeam().getName(), homeGoals,
                                    awayGoals);
                        }
                    }
                    catch (Exception e){
                        Log.d("błąd kursora", "błąd kursora" + e.getMessage());
                    }
                    finally {
                        // this gets called even if there is an exception somewhere above
                        if(cursor != null)
                            cursor.close();
                    }

                }
                else {
                    String whereClause = "LEAGUE_ID = ? AND IS_FOLLOWED = ? AND ID = ? ";
                    String[] whereArgs = new String[]{
                            String.valueOf(leagueId), isFollowed, match.getId().toString()
                    };

                    if(db.isOpen()) {
                        db.delete("Result", whereClause, whereArgs);
                        insertResult(db, leagueId, match.getId(), match.getMatchday(), match.getStatus(), match.getUtcDate(), match.getLastUpdated(), isFollowed,
                                match.getHomeTeam().getName(), match.getAwayTeam().getName(), homeGoals,
                                awayGoals);
                    }


                }
            }
        }
    }


    private static void insertTeam(SQLiteDatabase db, int id,  String league,  String name, String shortname,
                                   int points, int goalsScored, int goalsLoosed, String resource){
        ContentValues teamValues = new ContentValues();
        teamValues.put("ID", id);

        teamValues.put("NAME", name);
        teamValues.put("SHORTNAME", shortname);
        teamValues.put("POINTS", points);
        teamValues.put("GOALS_SCORED", goalsScored);
        teamValues.put("GOALS_LOOSED", goalsLoosed);
//        teamValues.put("IMAGE_RESOURCE", name.replaceAll(" ", "_").toLowerCase());
        teamValues.put("IMAGE_RESOURCE", resource);
        if(db.isOpen()) db.insert(league, null, teamValues);
    }

    private static void insertPlayer(SQLiteDatabase db, int id, int teamId,  String position, String nationality,
                                   String name, String shirtNumber, String birthDate){
        ContentValues playerValues = new ContentValues();

        playerValues.put("ID", id);
        playerValues.put("NAME", name);
        playerValues.put("TEAM_ID", teamId);
        playerValues.put("POSITION", position);
        playerValues.put("NATIONALITY", nationality);
        playerValues.put("SHIRT_NUMBER", shirtNumber);
//        teamValues.put("IMAGE_RESOURCE", name.replaceAll(" ", "_").toLowerCase());
        playerValues.put("BIRTH_DATE", birthDate);
        if(db.isOpen()) db.insert("Player", null, playerValues);
    }

    private static void insertTeamDetails(SQLiteDatabase db, String league, String name, String shortname,
                                   int points, int goalsScored, int goalsLoosed, String resource){
        ContentValues teamValues = new ContentValues();
        teamValues.put("NAME", name);
        teamValues.put("SHORTNAME", shortname);
        teamValues.put("POINTS", points);
        teamValues.put("GOALS_SCORED", goalsScored);
        teamValues.put("GOALS_LOOSED", goalsLoosed);
//        teamValues.put("IMAGE_RESOURCE", name.replaceAll(" ", "_").toLowerCase());
        teamValues.put("IMAGE_RESOURCE", resource);
        db.insert(league, null, teamValues);
    }

    private static void insertResult(SQLiteDatabase db, int leagueId , int apiMatchId, int leagueWeek, String status, String matchDate, String lastUpdated,
                                     String isFollowed, String teamHome, String teamAway, int goalsHome, int goalsAway){
        ContentValues teamValues = new ContentValues();
        teamValues.put("ID", apiMatchId);
        teamValues.put("LEAGUE_ID", leagueId);
        teamValues.put("LEAGUE_WEEK", leagueWeek);
        teamValues.put("STATUS", status);
        teamValues.put("MATCH_DATE", matchDate);
        teamValues.put("LAST_UPDATED", lastUpdated);
        teamValues.put("IS_FOLLOWED", isFollowed);
        teamValues.put("HOME_TEAM", teamHome);
        teamValues.put("AWAY_TEAM", teamAway);

        if(status.equals("SCHEDULED")) {
            teamValues.put("GOALS_HOME", "-");
            teamValues.put("GOALS_AWAY", "-");
        }
        else {
            teamValues.put("GOALS_HOME", goalsHome);
            teamValues.put("GOALS_AWAY", goalsAway);
        }
        // check if apiID exist in database
        String whereClause = "(ID = ?)";
        String[] whereArgs = new String[] {
                String.valueOf(apiMatchId)
        };

        Cursor checkCursor = null;
        try {
            checkCursor = db.query("Result", new String[]{"_id", "LEAGUE_WEEK", "HOME_TEAM", "GOALS_HOME", "GOALS_AWAY", "AWAY_TEAM", "STATUS", "LEAGUE_ID", "IS_FOLLOWED", "ID"}, whereClause, whereArgs, null, null, null);
            checkCursor.moveToFirst();

            if (db.isOpen() && checkCursor.getCount() == 0) {
                if (db.isOpen()) db.insert("Result", null, teamValues);
            }
        }
        catch (Exception e){
            Log.d("błąd kursora", "błąd kursora" + e.getMessage());
        }
        finally {
            // this gets called even if there is an exception somewhere above
            if(checkCursor != null)
                checkCursor.close();
        }
    }

    private static void updateTeamFragment(SQLiteDatabase db, int teamId) {
        String whereClause = "ID = ?";
        String[] whereArgs = new String[]{
                String.valueOf(teamId)
        };

        ImageView teamsLogo = mActivity.findViewById(R.id.teamsLogo);
        TextView teamName = mActivity.findViewById(R.id.teamsName);
        TextView teamStadium = mActivity.findViewById(R.id.stadiumTextIn2);
        TextView teamAddress = mActivity.findViewById(R.id.addressTextIn);
        TextView teamWebsite = mActivity.findViewById(R.id.websiteTextIn);

        Cursor cursor = db.query("Team", new String[]{"_id", "ID", "IMAGE_RESOURCE", "NAME", "ADDRESS", "WEBSITE", "VENUE"}, whereClause, whereArgs, null, null, null);

        if (teamsLogo != null) {
            cursor.moveToPosition(0);
            Context context = teamsLogo.getContext();


            if (cursor.getCount() > 0) {
                String team = cursor.getString(3);
                teamName.setText(cursor.getString(3));
                teamAddress.setText(cursor.getString(4));
                teamWebsite.setText(cursor.getString(5));
                teamStadium.setText(cursor.getString(6));
                String teamLogoUrl = cursor.getString(2);
                if (teamLogoUrl.contains(".cvg")) {
                    Uri uri = Uri.parse(teamLogoUrl);

                    RequestBuilder<PictureDrawable> requestBuilder;
                    requestBuilder = GlideApp.with(context)
                            .as(PictureDrawable.class)
                            .placeholder(R.drawable.arsenal_fc)
                            .error(R.drawable.chelsea_fc)
                            .listener(new SvgSoftwareLayerSetter());


                    requestBuilder.load(uri).into(teamsLogo);
                } else {
                    Glide.with(context)
                            .load(teamLogoUrl)
                            .into(teamsLogo);
                }
            }
        }
    }
}
