package com.example.piotr_wanio.matchscore;

import android.app.ListActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.piotr_wanio.matchscore.R;

public class ScoresActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);
        ListView listResults = (ListView)findViewById(R.id.scoreslist);
        ArrayAdapter<Result> listAdapter = new ArrayAdapter<Result>(
                this,
                android.R.layout.simple_list_item_1,
                Result.results);
        listResults.setAdapter(listAdapter);
    }
}
