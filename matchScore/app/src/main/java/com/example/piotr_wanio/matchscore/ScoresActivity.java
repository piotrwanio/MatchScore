package com.example.piotr_wanio.matchscore;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.example.piotr_wanio.matchscore.R;

import java.util.ArrayList;

public class ScoresActivity extends Activity {

    private String[] titles;
    private ListView drawerList;
    DrawerLayout drawerLayout;

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // Kod wykonywany po kliknięciu elementu w szufladzie nawigacyjnej
            selectItem(position);
        }
    }

    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);
        titles = getResources().getStringArray(R.array.titles);
        drawerList = (ListView) findViewById(R.id.drawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawerList.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_activated_1, titles));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
        // Wyświetlamy odpowiedni fragment
        if (savedInstanceState == null) {
            selectItem(0);
        }

    }

    private void selectItem(int position) {
        // Aktualizujemy główną zawartość aplikacji, podmieniając prezentowany fragment


        Fragment fragment;
        switch (position) {
            case 1:
                fragment = new ResultsFragment();
                break;
            case 2:
                fragment = new ResultsFragment();
                break;
            default:
                fragment = new ResultsFragment();
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment, "visible_fragment");
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
        // Ustawiamy tytuł paska akcji
        setActionBarTitle(position);
        // Zamykamy szufladę nawigacyjną
        drawerLayout.closeDrawer(drawerList);
    }

    private void setActionBarTitle(int position) {
        String title;
        if (position == 0) {
            title = getResources().getString(R.string.app_name);
        } else {
            title = titles[position];
        }
        getActionBar().setTitle(title);
    }


}
