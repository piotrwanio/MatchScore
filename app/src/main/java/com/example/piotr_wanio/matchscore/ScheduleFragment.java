package com.example.piotr_wanio.matchscore;

import android.app.Fragment;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.io.IOException;

public class ScheduleFragment extends Fragment {

    private SQLiteDatabase db;
    private Cursor cursor;
    SQLiteOpenHelper matchScoreDatabaseHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        ListView listResults = (ListView)view.findViewById(R.id.scoreslist);

        try {
            if(db == null){
                matchScoreDatabaseHelper = new MatchScoreDatabaseHelper(getActivity());
            }

            db = matchScoreDatabaseHelper.getReadableDatabase();

            String[] from = {"MATCH_DATE", "LEAGUE_WEEK", "HOME_TEAM", "GOALS_HOME", "GOALS_AWAY", "AWAY_TEAM", "IS_FOLLOWED"};
            int[] to = {R.id.schedule_item_date, R.id.schedule_item_leagueweek, R.id.schedule_item_teamA, R.id.schedule_item_goals_teamA,
                    R.id.schedule_item_goals_teamB, R.id.schedule_item_teamB, R.id.followStar};


            Bundle bundle = getArguments();
            int leagueAPIId = bundle.getInt("leagueId", 0);

            SimpleService simpleService = new SimpleService(this.getActivity());
            try {
                simpleService.getResultsResponse(db, leagueAPIId);
            }
            catch (IOException e) {
                Log.d("IOException", "IOEXCEPTION");
            }

            String whereClause = "(STATUS = ? OR STATUS = ? ) AND LEAGUE_ID = ?";
            String[] whereArgs = new String[] {
                    "SCHEDULED","IN_PLAY", String.valueOf(leagueAPIId)
            };
            cursor = db.query("Result", new String[]{"_id", "LEAGUE_WEEK", "HOME_TEAM", "GOALS_HOME", "GOALS_AWAY", "AWAY_TEAM",
                    "STATUS", "LEAGUE_ID", "IS_FOLLOWED", "MATCH_DATE"}, whereClause, whereArgs, null, null, "LEAGUE_WEEK ASC");
            CursorAdapter listAdapter = new SimpleCursorAdapter(getActivity(),
                    R.layout.listview_schedule_row,
                    cursor,
                    //      new String[]{"TEAM_A", "TEAM_B"},
                    //     new int[]{android.R.id.text1},
                    from,
                    to,
                    0);
            ((SimpleCursorAdapter)listAdapter).setViewBinder(new SimpleCursorAdapter.ViewBinder(){
                /** Binds the Cursor column defined by the specified index to the specified view */
                public boolean setViewValue(View view, Cursor cursor, int columnIndex){
                    if(view.getId() == R.id.followStar){
//                        int position = cursor.getPosition();
                        view.setTag(cursor.getPosition());   // here you set position on a tag
                        String isFollowed = cursor.getString(8);
                        if(isFollowed.equals("no")) {
                            ((ImageView)view).setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(),android.R.drawable.btn_star_big_off));
                        }
                        if(isFollowed.equals("yes")) {
                            ((ImageView) view).setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), android.R.drawable.btn_star_big_on));
                        }

                        ((ImageView)view).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //  use a tag to get the right position
                                int position =  (int)v.getTag();
                                cursor.moveToPosition(position);
                                String isFollowed = cursor.getString(8);
                                if(isFollowed.equals("no")) {
                                    ContentValues contentValues= new ContentValues();
                                    contentValues.put("IS_FOLLOWED","yes");
                                    db.update("Result", contentValues,"_id = ?",new String[] {cursor.getString(0)});
                                    ((ImageView) view).setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), android.R.drawable.btn_star_big_on));
                                }
                                if(isFollowed.equals("yes")) {
                                    ContentValues contentValues= new ContentValues();
                                    contentValues.put("IS_FOLLOWED","no");
                                    db.update("Result", contentValues,"_id = ?",new String[] {cursor.getString(0)});
                                    ((ImageView) view).setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), android.R.drawable.btn_star_big_off));

                                }

                                Cursor newCursor =  db.query("Result", new String[]{"_id", "LEAGUE_WEEK", "HOME_TEAM", "GOALS_HOME", "GOALS_AWAY", "AWAY_TEAM",
                                        "STATUS", "LEAGUE_ID", "IS_FOLLOWED", "MATCH_DATE"}, whereClause, whereArgs, null, null, "LEAGUE_WEEK ASC");


                                Thread thread = new Thread() {
                                    @Override
                                    public void run() {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                listAdapter.changeCursor(newCursor);
                                                if(isFollowed.equals("no")) ((ImageView) view).setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), android.R.drawable.btn_star_big_on));
                                                if(isFollowed.equals("yes")) ((ImageView) view).setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), android.R.drawable.btn_star_big_off));
                                            }
                                        });
                                    }
                                };
                                thread.start();
                                listAdapter.notifyDataSetChanged();
                            }
                        });
                        return true; //true because the data was bound to the view
                    }
                    return false;
                }
            });

            listResults.setAdapter(listAdapter);
            listResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    cursor.moveToPosition(position);

                }

            });



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