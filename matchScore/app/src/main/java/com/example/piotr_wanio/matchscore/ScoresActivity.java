package com.example.piotr_wanio.matchscore;

import android.app.ListActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.piotr_wanio.matchscore.R;

import java.util.ArrayList;

public class ScoresActivity extends AppCompatActivity {


    public ArrayList<Result> resultsList = new ArrayList<Result>();
    Result r1 = new Result(new Team("Arsenal", "ARS", 0, 0, 0, 0), new Team("Real Madrit", "RM", 0, 0, 0, 0), 5, 2);
    Result r2 = new Result(new Team("Arsenal", "ARS", 0, 0, 0, 0), new Team("Real Madrit", "RM", 0, 0, 0, 0), 3, 3);
    Result r3 = new Result(new Team("Arsenal", "ARS", 0, 0, 0, 0), new Team("Real Madrit", "RM", 0, 0, 0, 0), 4, 4);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);
        ListView listResults = (ListView) findViewById(R.id.scoreslist);
        resultsList.add(r1);
        resultsList.add(r2);
        ArrayAdapter<Result> listAdapter = new ArrayAdapter<Result>(
                this,
                android.R.layout.simple_list_item_1,
                resultsList);
        listResults.setAdapter(listAdapter);
    }

}
