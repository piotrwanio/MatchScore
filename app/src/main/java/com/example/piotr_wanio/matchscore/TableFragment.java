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

/**
 * A simple {@link Fragment} subclass.
 */
public class TableFragment extends Fragment {


    private SQLiteDatabase db;
    private Cursor cursor;
    SQLiteOpenHelper matchScoreDatabaseHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_table, container, false);
        ListView listResults = (ListView)view.findViewById(R.id.table);

        try {
            if(db == null){
                matchScoreDatabaseHelper = new MatchScoreDatabaseHelper(getActivity());
            }

            String[] from = {"_id", "NAME", "POINTS", "GOALS_SCORED"};
            int[] to = {R.id.tablerow_item_place, R.id.tablerow_item_team,
                    R.id.tablerow_item_points, R.id.tablerow_item_goals};

            db = matchScoreDatabaseHelper.getReadableDatabase();
            matchScoreDatabaseHelper.onUpgrade(db,1,1);
            cursor = db.query("TEAM", new String[]{"_id", "NAME", "POINTS", "GOALS_SCORED"}, null, null, null, null, null);
            CursorAdapter listAdapter = new SimpleCursorAdapter(getActivity(),
                    R.layout.tablerow_activity,
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
