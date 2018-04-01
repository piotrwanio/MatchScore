package com.example.piotr_wanio.matchscore;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.example.piotr_wanio.matchscore.R;

import java.util.ArrayList;

public class ScoresActivity extends AppCompatActivity {
    private SQLiteDatabase db;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);
        ListView listResults = (ListView) findViewById(R.id.scoreslist);


        try {
            SQLiteOpenHelper matchScoreDatabaseHelper = new MatchScoreDatabaseHelper(this);

            String[] from = {"TEAM_A", "GOALS_A", "GOALS_B", "TEAM_B"};
            int[] to = {R.id.listview_item_teamA, R.id.listview_item_goals_teamA,
                    R.id.listview_item_goals_teamB, R.id.listview_item_teamB};

            db = matchScoreDatabaseHelper.getReadableDatabase();
            cursor = db.query("RESULT", new String[]{"_id", "TEAM_A", "GOALS_A", "GOALS_B", "TEAM_B"}, null, null, null, null, null);
            CursorAdapter listAdapter = new SimpleCursorAdapter(this,
                    R.layout.listview_activity,
                    cursor,
              //      new String[]{"TEAM_A", "TEAM_B"},
               //     new int[]{android.R.id.text1},
                    from,
                    to,
                    0);
            listResults.setAdapter(listAdapter);
        }catch (SQLiteException e){
            Toast toast = Toast.makeText(this, "Baza danych jest niedostępna!", Toast.LENGTH_SHORT);

            toast.show();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        cursor.close();
        db.close();
    }

}
