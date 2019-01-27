package com.example.piotr_wanio.matchscore;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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

    private static Activity mActivity;
    private static CursorAdapter adapter;


    public SimpleService(Activity activity){
        this.mActivity = activity;
    }

    public interface API {
        @GET("/teams")
        Call<List<Team>> teams();
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
//    public interface LeagueResultsService {
//        @Headers("X-Auth-Token: 23377c48baed4a65b9ac16a0499d62e9")
//        @GET("v2/competitions/{leagueId}/matches")
//        Call<StandingsResponse> leagueResults(@Path("leagueId") int leagueId);
//    }



    public static StandingsResponse getStandingsResponse(SQLiteDatabase db, CursorAdapter adapter,  final int leagueId) throws IOException {
        database = db;
        // Create a very simple REST adapter which points the GitHub API.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .callbackExecutor(Executors.newSingleThreadExecutor())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create an instance of our Standings API interface.
        StandingsService standingsService = retrofit.create(StandingsService.class);

        // Create a call instance for looking up Retrofit contributors.
        Call<StandingsResponse> call = standingsService.standings(leagueId);

        // Fetch and print a list of the contributors to the library.

//        List<Team> teams = call.execute().body();
//        for (Team team : teams) {
//            System.out.println(team.name + " (" + team.points + ")");
//        }

        call.enqueue(new Callback<StandingsResponse>() {
            @Override
            public void onResponse(Call<StandingsResponse> call, Response<StandingsResponse> response) {
                if(response.body() != null) {
                    standingsResponse = response.body();
                    responseApi = response;
                    table = standingsResponse.getStandings().get(0).getTable();
                    for (Table team : table) {
                        System.out.println(team.getTeam().getName() + " (" + team.getPoints() + ")");
                    }
                    updateStandings(database, standingsResponse, leagueId);
                    Cursor cursor = db.query("StandingsGermany", new String[]{"_id", "IMAGE_RESOURCE","NAME", "POINTS", "GOALS_SCORED"}, null, null, null, null, null);

                    if(adapter != null) {
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.changeCursor(cursor);
                                    }
                                });
                            }
                        };
                        thread.start();
                    }
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

    public static ResultsResponse getResultsResponse(SQLiteDatabase db, final int leagueId) throws IOException {
        database = db;
        // Create a very simple REST adapter which points the GitHub API.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .callbackExecutor(Executors.newSingleThreadExecutor())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create an instance of our Standings API interface.
        ResultsService resultsService = retrofit.create(ResultsService.class);

        // Create a call instance for looking up Retrofit contributors.
        Call<ResultsResponse> call = resultsService.results(leagueId);

        // Fetch and print a list of the contributors to the library.

//        List<Team> teams = call.execute().body();
//        for (Team team : teams) {
//            System.out.println(team.name + " (" + team.points + ")");
//        }

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
                    updateResults(database, resultsResponse, leagueId, "no");
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
        return resultsResponse;
    }

    public static TeamsResponse getTeamsResponse(SQLiteDatabase db, final int teamId, ImageView teamsLogo, TextView teamsName) throws IOException {
        database = db;
        // Create a very simple REST adapter which points the GitHub API.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .callbackExecutor(Executors.newSingleThreadExecutor())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create an instance of our Standings API interface.
        TeamsService teamsService = retrofit.create(TeamsService.class);

        // Create a call instance for looking up Retrofit contributors.
        Call<TeamsResponse> call = teamsService.team(teamId);

        // Fetch and print a list of the contributors to the library.

//        List<Team> teams = call.execute().body();
//        for (Team team : teams) {
//            System.out.println(team.name + " (" + team.points + ")");
//        }

        call.enqueue(new Callback<TeamsResponse>() {
            @Override
            public void onResponse(Call<TeamsResponse> call, Response<TeamsResponse> response) {
                if(response.body() != null) {
                    teamsResponse = response.body();
                    responseApi = response;

                    updateTeam(database, teamsResponse, teamId);
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateTeamFragment(db, teamId);
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
        return teamsResponse;
    }

    public static void updateStandings(SQLiteDatabase db, StandingsResponse teamsList, int leagueId){

        String league = "";
        switch (leagueId)
        {
            case 2021:
                league = "StandingsEngland";
                db.execSQL("UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME='StandingsEngland';");
                break;

            case 2014:
                league = "StandingsSpain";
                db.execSQL("UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME='StandingsSpain';");
                break;
            case 2002:
                league = "StandingsGermany";
                db.execSQL("UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME='StandingsGermany';");
        }
        db.delete(league,"1",null);

        if(teamsList != null){
            for (Table team : teamsList.getStandings().get(0).getTable()) {
                insertTeam(db, team.getTeam().getId(), league, team.getTeam().getName(),"",(int)team.getPoints(),0,0,team.getTeam().getCrestUrl());
            }
        }
    }

    public static void updateTeam(SQLiteDatabase db, TeamsResponse teamsDetails, int teamId){

      //  db.delete("Team","ID ="+teamsDetails.getId(),null);

        ContentValues teamValues = new ContentValues();
        teamValues.put("ID", teamsDetails.getId());
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
//        String league = "";
//        switch (leagueId)
//        {
//            case 2021:
//                league = "ResultsEngland";
//                db.execSQL("UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME='ResultsEngland';");
//                break;
//            case 2002:
//                league = "ResultsGermany";
//                db.execSQL("UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME='ResultsGermany';");
//        }

        db.execSQL("UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME='RESULT';");
        String whereClause = "LEAGUE_ID = ? AND IS_FOLLOWED != ?";
        String[] whereArgs = new String[] {
                String.valueOf(leagueId), "yes"
        };
        db.delete("Result",whereClause,whereArgs);

        if(resultsList != null){
            for (Match match : resultsList.getMatches()) {
                int homeGoals = 0;
                int awayGoals = 0;
                if(match.getScore().getFullTime().getHomeTeam() != null && match.getScore().getFullTime().getAwayTeam() != null){
                    homeGoals = match.getScore().getFullTime().getHomeTeam();
                    awayGoals = match.getScore().getFullTime().getAwayTeam();
                }
                insertResult(db, leagueId, match.getId(), match.getMatchday(), match.getStatus(), match.getUtcDate(), match.getLastUpdated(), isFollowed,
                        match.getHomeTeam().getName(), match.getAwayTeam().getName(),homeGoals,
                        awayGoals);
            }
        }
    }

    private static void insertTeam(SQLiteDatabase db, int id, String league,  String name, String shortname,
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
        db.insert(league, null, teamValues);
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
        db.insert("Player", null, playerValues);
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

        teamValues.put("GOALS_HOME", goalsHome);
        teamValues.put("GOALS_AWAY", goalsAway);

        // check if apiID exist in database
        String whereClause = "(ID = ?)";
        String[] whereArgs = new String[] {
                String.valueOf(apiMatchId)
        };
        Cursor checkCursor = db.query("Result", new String[]{"_id", "LEAGUE_WEEK", "HOME_TEAM", "GOALS_HOME", "GOALS_AWAY", "AWAY_TEAM", "STATUS", "LEAGUE_ID", "IS_FOLLOWED", "ID"}, whereClause, whereArgs, null, null, null);

        if(checkCursor.getCount() == 0) {
            db.insert("Result", null, teamValues);
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
