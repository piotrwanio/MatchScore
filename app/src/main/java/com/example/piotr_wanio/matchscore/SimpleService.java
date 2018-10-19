package com.example.piotr_wanio.matchscore;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public final class SimpleService {

    private static final String API_URL = "http://10.0.2.2:8080";
    private static List<Team> teams;

    public static class Team {
        public final long points;
        public final String name;
        public final String shortName;

        public Team(String name, String shortName, long points) {
            this.name = name;
            this.points = points;
            this.shortName = shortName;
        }
    }

    public interface API {
        @GET("/teams")
        Call<List<Team>> teams();
    }

    public static List<Team> run() throws IOException {
        // Create a very simple REST adapter which points the GitHub API.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create an instance of our GitHub API interface.
        API api = retrofit.create(API.class);

        // Create a call instance for looking up Retrofit contributors.
        Call<List<Team>> call = api.teams();

        // Fetch and print a list of the contributors to the library.

//        List<Team> teams = call.execute().body();
//        for (Team team : teams) {
//            System.out.println(team.name + " (" + team.points + ")");
//        }
        call.enqueue(new Callback<List<Team>>() {
            @Override
            public void onResponse(Call<List<Team>> call, Response<List<Team>> response) {
                if(response.body() != null) {
                    teams = response.body();
                    for (Team team : teams) {
                        System.out.println(team.name + " (" + team.points + ")");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Team>> call, Throwable t) {
                if (t instanceof IOException) {
                    System.out.println("Network trouble");
                    System.out.println(t.getMessage());   // logging necessary
                } else {
                    System.out.println("Enqueue fail...");
                    System.out.println(t.getMessage());   // logging necessary
                }
            }
        });
        return teams;
    }
}
