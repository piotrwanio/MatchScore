package com.example.piotr_wanio.matchscore;

import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.io.IOException;

public class ResultsFragment extends Fragment {

    private SQLiteDatabase db;
    private Cursor cursor;
    SQLiteOpenHelper matchScoreDatabaseHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_results, container, false);
        ListView listResults = (ListView)view.findViewById(R.id.scoreslist);

        try {
            if(db == null){
                matchScoreDatabaseHelper = new MatchScoreDatabaseHelper(getActivity());
            }

            db = matchScoreDatabaseHelper.getReadableDatabase();

            String[] from = {"LEAGUE_WEEK", "HOME_TEAM", "GOALS_HOME", "GOALS_AWAY", "AWAY_TEAM"};
            int[] to = {R.id.results_item_leagueweek, R.id.results_item_teamA, R.id.results_item_goals_teamA,
                    R.id.results_item_goals_teamB, R.id.results_item_teamB};


            Bundle bundle = getArguments();
            int leagueAPIId = bundle.getInt("leagueId", 0);
            SimpleService simpleService = new SimpleService(this.getActivity());

            try {
                simpleService.getResultsResponse(db, leagueAPIId);
            }
            catch (IOException e) {
                Log.d("IOException", "IOEXCEPTION");
            }

            String whereClause = "STATUS = ? AND LEAGUE_ID = ?";
            String[] whereArgs = new String[] {
                    "FINISHED", String.valueOf(leagueAPIId)
            };
            cursor = db.query("Result", new String[]{"_id", "LEAGUE_WEEK", "HOME_TEAM", "GOALS_HOME", "GOALS_AWAY", "AWAY_TEAM", "STATUS", "LEAGUE_ID"},
                    whereClause, whereArgs, null, null, "LEAGUE_WEEK DESC");
            CursorAdapter listAdapter = new SimpleCursorAdapter(getActivity(),
                    R.layout.listview_activity,
                    cursor,
                    //      new String[]{"TEAM_A", "TEAM_B"},
                    //     new int[]{android.R.id.text1},
                    from,
                    to,
                    0);
            listResults.setAdapter(listAdapter);
        }catch (SQLiteException e){
            Toast toast = Toast.makeText(inflater.getContext(), "Baza danych jest niedostępna! " +e.getMessage(), Toast.LENGTH_SHORT);

            toast.show();
        }
        return view;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(null !=cursor){
            cursor.close();
        }
        if(null !=db){
            db.close();
        }
    }
}