package com.example.piotr_wanio.matchscore;

import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

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

            String[] from = {"TEAM_A", "GOALS_A", "GOALS_B", "TEAM_B"};
            int[] to = {R.id.listview_item_teamA, R.id.listview_item_goals_teamA,
                    R.id.listview_item_goals_teamB, R.id.listview_item_teamB};

            db = matchScoreDatabaseHelper.getReadableDatabase();
            cursor = db.query("RESULT", new String[]{"_id", "TEAM_A", "GOALS_A", "GOALS_B", "TEAM_B"}, null, null, null, null, null);
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
            Toast toast = Toast.makeText(inflater.getContext(), "Baza danych jest niedostępna!", Toast.LENGTH_SHORT);

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