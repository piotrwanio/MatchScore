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
import android.util.Log;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

import com.caverock.androidsvg.SVG;
import com.example.piotr_wanio.matchscore.apiResponses.standingsResponse.StandingsResponse;
import com.example.piotr_wanio.matchscore.apiResponses.standingsResponse.Table;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.IOException;
import java.io.InputStream;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;


/**
 * A simple {@link Fragment} subclass.
 */
public class TableFragment extends Fragment {


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
        View view = inflater.inflate(R.layout.fragment_table, container, false);
        ListView listResults = (ListView)view.findViewById(R.id.table);

        try {
            if(db == null){
                matchScoreDatabaseHelper = new MatchScoreDatabaseHelper(getActivity());
            }

            String[] from = {"_id", "IMAGE_RESOURCE", "NAME", "POINTS", "GOALS_SCORED"};
            int[] to = {R.id.tablerow_item_place, R.id.tablerow_item_logo, R.id.tablerow_item_team,
                    R.id.tablerow_item_points, R.id.tablerow_item_goals};


            db = matchScoreDatabaseHelper.getReadableDatabase();

//            updateStandings(db, 1);
//            matchScoreDatabaseHelper.onUpgrade(db,1,1);
//            matchScoreDatabaseHelper.onUpgrade(db,1,1);

            Bundle bundle = getArguments();
            int leagueId = bundle.getInt("index", 0);
            String league = "";
            leagueApiId = 0;
            switch (leagueId)
            {
                case 1:
                    leagueApiId = 2021;
                    league = "StandingsEngland";
                    break;
                case 2:
                    leagueApiId = 2002;
                    league = "StandingsGermany";
            }
            cursor = db.query(league, new String[]{"_id", "ID", "IMAGE_RESOURCE", "NAME", "POINTS", "GOALS_SCORED"}, null, null, null, null, null);
            listAdapter = new SimpleCursorAdapter(getActivity(),
                    R.layout.tablerow_activity,
                    cursor,
                    //      new String[]{"TEAM_A", "TEAM_B"},
                    //     new int[]{android.R.id.text1},
                    from,
                    to,
                    0);

            ((SimpleCursorAdapter) listAdapter).setViewBinder(new SimpleCursorAdapter.ViewBinder(){
                /** Binds the Cursor column defined by the specified index to the specified view */
                public boolean setViewValue(View view, Cursor cursor, int columnIndex){
                    if(view.getId() == R.id.tablerow_item_logo){
                        ImageView iconImageView = (ImageView) view;
                        String team = cursor.getString(2);

                        Context context = iconImageView.getContext();

                        int id = context.getResources().getIdentifier("drawable/" + team.replaceAll("&", "and").toLowerCase(),
                                null, context.getPackageName());

//                        iconImageView.setImageURI("http://pluspng.com/img-png/bournemouth-fc-logo-vector-png-logo-of-afc-bournemouth-195.png");
//                        Glide.with(context)
//                                .as(PictureDrawable.class)
//                                .load(team)
//                                .into(iconImageView);

                        if(team.contains(".cvg")) {
                            Uri uri = Uri.parse(team);

                            RequestBuilder<PictureDrawable> requestBuilder;
                            requestBuilder = GlideApp.with(context)
                                    .as(PictureDrawable.class)
                                    .placeholder(R.drawable.arsenal_fc)
                                    .error(R.drawable.chelsea_fc)
                                    .listener(new SvgSoftwareLayerSetter());


                            requestBuilder.load(uri).into(iconImageView);
                        }
                        else {
                            Glide.with(context)
                                    .load(team)
                                    .into(iconImageView);
                        }
                        return true;
                    }
                    return false;
                }
            });

            listResults.setAdapter(listAdapter);
            SimpleService simpleService = new SimpleService(this.getActivity());
            listResults.setOnItemClickListener((parent,
                                                v,
                                                position,
                                                id)->{
                Object o = parent.getItemAtPosition(position);
                cursor.moveToPosition(position);
                String name = cursor.getString(1);

                onTeamSelectedListener.onTeamSelected(Integer.parseInt(name));
//                try {
//                    simpleService.getStandingsResponse(db, listAdapter, leagueApiId);
//                }
//                catch (IOException e) {
//                    Log.d("IOException", "IOEXCEPTION");
//                }
            });


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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onTeamSelectedListener = (OnTeamSelectedListener) context;
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
