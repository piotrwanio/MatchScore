package com.example.piotr_wanio.matchscore;


import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.example.piotr_wanio.matchscore.apiResponses.standingsResponse.StandingsResponse;
import com.example.piotr_wanio.matchscore.apiResponses.standingsResponse.Table;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlayersFragment extends Fragment {


    private SQLiteDatabase db;
    private Cursor cursor;
    SQLiteOpenHelper matchScoreDatabaseHelper;
    CursorAdapter listAdapter;

    int leagueApiId;

    private static StandingsResponse teamsList;

    OnTeamSelectedListener onTeamSelectedListener;

    // Define the events that the fragment will use to communicate
    public interface OnTeamSelectedListener {
        // This can be any number of events to be sent to the activity
        public void onTeamSelected(int teamId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_players, container, false);
        ListView listResults = (ListView)view.findViewById(R.id.tablePlayers);

        try {
            if(db == null){
                matchScoreDatabaseHelper = new MatchScoreDatabaseHelper(getActivity());
            }

            String[] from = {"NAME", "SHIRT_NUMBER", "NATIONALITY", "POSITION", "BIRTH_DATE"};
            int[] to = {R.id.row_player_name, R.id.row_player_shirt, R.id.row_player_nationality,
                    R.id.row_player_position, R.id.row_player_birth};


            db = matchScoreDatabaseHelper.getReadableDatabase();

//            updateStandings(db, 1);
//            matchScoreDatabaseHelper.onUpgrade(db,1,1);
//            matchScoreDatabaseHelper.onUpgrade(db,1,1);

            Bundle bundle = getArguments();
            int teamId = bundle.getInt("teamId", 0);
            String whereClause = "TEAM_ID = ?";
            String[] whereArgs = new String[] {
                     String.valueOf(teamId)
            };

            cursor = db.query("Player", new String[]{"_id", "NAME", "SHIRT_NUMBER", "NATIONALITY", "POSITION", "TEAM_ID", "BIRTH_DATE"}, whereClause, whereArgs, null, null, "SHIRT_NUMBER");
            listAdapter = new SimpleCursorAdapter(getActivity(),
                    R.layout.listview_player_row,
                    cursor,
                    //      new String[]{"TEAM_A", "TEAM_B"},
                    //     new int[]{android.R.id.text1},
                    from,
                    to,
                    0);


            listResults.setAdapter(listAdapter);


        }catch (SQLiteException e){
            Toast toast = Toast.makeText(inflater.getContext(), "Baza danych jest niedostępna!" + e.getMessage() + e.getLocalizedMessage(), Toast.LENGTH_SHORT);

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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        onTeamSelectedListener = (OnTeamSelectedListener) context;
    }

    public void updateStandings(SQLiteDatabase db, int leagueId){

        db.delete("TEAM","1",null);
        db.execSQL("UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME='TEAM';");
//        SimpleService service = new SimpleService();
        try {
//            teamsList = service.run();
        }catch (Exception e){
            System.out.println("Service exception :/");
        }
        if(teamsList != null){
            for (Table team : teamsList.getStandings().get(0).getTable()) {
                insertTeam(db,team.getTeam().getName(),"",(int)team.getPoints(),0,0,0);
            }
        }
    }

    private static void insertTeam(SQLiteDatabase db, String name, String shortname,
                                   int points, int goalsScored, int goalsLoosed, int resourceID){
        ContentValues teamValues = new ContentValues();
        teamValues.put("NAME", name);
        teamValues.put("SHORTNAME", shortname);
        teamValues.put("POINTS", points);
        teamValues.put("GOALS_SCORED", goalsScored);
        teamValues.put("GOALS_LOOSED", goalsLoosed);
        teamValues.put("IMAGE_RESOURCE_ID", resourceID);
        db.insert("TEAM", null, teamValues);
    }




}
