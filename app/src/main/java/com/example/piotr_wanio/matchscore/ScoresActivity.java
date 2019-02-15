package com.example.piotr_wanio.matchscore;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DateSorter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.stream.Stream;

public class ScoresActivity extends Activity implements TableFragment.OnTeamSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    // Google API Location
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager locationManager;
    private LocationRequest mLocationRequest;
    private com.google.android.gms.location.LocationListener listener;
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */

    private String[] menuOptions;
    private ListView drawerList;
    DrawerLayout drawerLayout;
    private int currentPosition = 0;

    private ShareActionProvider shareActionProvider;
    private ActionBarDrawerToggle drawerToggle;

    SQLiteOpenHelper matchScoreDatabaseHelper;

    @Override
    public void onTeamSelected(int teamId, int leagueId) {


        Fragment fragment;
        Bundle args = new Bundle();
        fragment = new TeamFragment();
        args.putInt("teamId", teamId);
        args.putInt("leagueId", leagueId);

        fragment.setArguments(args);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment, "visible_fragment");
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
        getActionBar().setTitle("Zespół");

    }

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
        menuOptions = getResources().getStringArray(R.array.menu_options);
        drawerList = (ListView) findViewById(R.id.drawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Google API GPS
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
         checkLocation();

        checkLocationPermission();



        drawerList.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_activated_1, menuOptions));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());


        // Wyświetlamy odpowiedni fragment
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt("position");
            setActionBarTitle(currentPosition);
        } else {
            selectItem(0);
        }

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.open_drawer, R.string.close_drawer) {
            @Override
            public void onDrawerClosed(View view){
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView){
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };
        drawerLayout.addDrawerListener(drawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
//        getActionBar().setHomeButtonEnabled(true);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
        menu.findItem(R.id.action_share).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    private void selectItem(int position) {
        // Aktualizujemy główną zawartość aplikacji, podmieniając prezentowany fragment


        Fragment fragment;
        Bundle args = new Bundle();

        SimpleService simpleService = new SimpleService(this);
//        try {
//            simpleService.getStandingsResponse(null, 2021);
//            simpleService.getStandingsResponse(null, 2014);
//            simpleService.getStandingsResponse(null, 2002);
//        }
//        catch (Exception e){
//
//        }


        switch (position) {
            case 0:
                fragment = new LeagueFragment();
                // premier league
                args.putInt("leagueId", 2021);
                break;

            case 1:
                fragment = new LeagueFragment();
                // bundesliga
                args.putInt("leagueId", 2002);
                break;
            case 2:
                fragment = new LeagueFragment();
                // laliga
                args.putInt("leagueId", 2014);
                break;
            case 3:
                fragment = new MyMatchesFragment();
                args.putInt("leagueId", 2002);
                break;
            default:
                fragment = new ResultsFragment();
        }
        // Supply index input as an argument.
        fragment.setArguments(args);
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
        title = menuOptions[position];
        getActionBar().setTitle(title);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Synchronizujemy stan przycisku przełącznika po wywołaniu
        // metody onRestoreInstanceState.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Przygotowujemy menu; to wywołanie dodaje elementy do paska akcji jeśli jest używany
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider) menuItem.getActionProvider();
        setIntent("To jest przykładowy tekst.");
        return super.onCreateOptionsMenu(menu);
    }


    private void setIntent(String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        shareActionProvider.setShareIntent(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {

            case R.id.action_settings:
                // kod wykonywany po kliknięciu przycisku Ustawienia
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }



    }

    // Google API Location

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        } startLocationUpdates();
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLocation == null){
            startLocationUpdates();
        }
        if (mLocation != null) {
            double latitude = mLocation.getLatitude();
            double longitude = mLocation.getLongitude();
            /*------- To get city name from coordinates -------- */
            String cityName = null;
            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(mLocation.getLatitude(),
                        mLocation.getLongitude(), 1);
                addresses = gcd.getFromLocationName("Paris",1);

                Location location2 = new Location("");
                location2.setLatitude(addresses.get(0).getLatitude());
                location2.setLongitude(addresses.get(0).getLongitude());

                addresses = gcd.getFromLocationName("Berlin",1);

                Location location3 = new Location("");
                location3.setLatitude(addresses.get(0).getLatitude());
                location3.setLongitude(addresses.get(0).getLongitude());

                addresses = gcd.getFromLocationName("Wroclaw, Pomorska",1);

                Location location4 = new Location("");
                location4.setLatitude(addresses.get(0).getLatitude());
                location4.setLongitude(addresses.get(0).getLongitude());

                double dist = mLocation.distanceTo(location2);
                double dist2 = mLocation.distanceTo(location3);
                double dist3 = mLocation.distanceTo(location4);

                AsyncFindTeam asyncFindTeam = new AsyncFindTeam();
                asyncFindTeam.setGeocoder(gcd);
                asyncFindTeam.execute();
//                Toast.makeText(getApplicationContext(),"do paryza:"+dist+" do berlina:"+dist2 + "na pomorska: " + dist3,Toast.LENGTH_LONG).show();



                if (addresses.size() > 0) {
                    System.out.println(addresses.get(0).getLocality());
                    cityName = addresses.get(0).getLocality();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Toast.makeText(this, "Location not Detected", Toast.LENGTH_SHORT).show();
        }
    }

    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
        Log.d("reque", "--->>>>");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("", "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("", "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
    @Override
    public void onLocationChanged(Location location) {

    }

    private boolean checkLocation() {
        if(!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
        dialog.show();
    }

    private boolean isLocationEnabled() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    // Location permission check

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("")
                        .setMessage("")
                        .setPositiveButton("", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(ScoresActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }

    private class AsyncFindTeam extends AsyncTask{

        Geocoder geocoder;

        public void setGeocoder(Geocoder geocoder) {
            this.geocoder = geocoder;
        }


        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected Object doInBackground(Object[] objects) {
            findNearestTeam(geocoder);
            return null;
        }
    }
    public void findNearestTeam (Geocoder gcd){
        List<Address> addresses;
        MatchScoreDatabaseHelper matchScoreDatabaseHelper = MatchScoreDatabaseHelper.getInstance(this);
        SQLiteDatabase db = null;
        HashMap<String, Float> cities = new HashMap<String, Float>();
        Float distance = null;

        try {
            db = matchScoreDatabaseHelper.getReadableDatabase();

            Cursor  cursor = db.rawQuery("select * from Team",null);
            int count = cursor.getCount();
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String cityFromName = cursor.getString(cursor.getColumnIndex("ADDRESS"));
                    try {
                        addresses = gcd.getFromLocationName(cityFromName,1);
                        Location location = new Location("");
                        location.setLatitude(addresses.get(0).getLatitude());
                        location.setLongitude(addresses.get(0).getLongitude());
                        distance = mLocation.distanceTo(location);

                    }
                    catch (Exception e){

                    }
                    cities.put(cityFromName, distance);
                    cursor.moveToNext();
                }
                cities.size();
                Stream<Map.Entry<String,Float>> sorted =
                        cities.entrySet().stream()
                                .sorted(Map.Entry.comparingByValue());

                runOnUiThread(new Runnable() {
                    public void run() {

                        Toast toast = Toast.makeText(getApplicationContext(), "Najbliższe miasto: " + sorted.findFirst().toString(), Toast.LENGTH_SHORT);
                        toast.show();                    }
                });


            }
            cursor.close();
//            db.close();
        }
        catch (SQLiteException e){
            Toast toast = Toast.makeText(getApplicationContext(), "Baza danych jest niedostępna! " +e.getMessage(), Toast.LENGTH_SHORT);

            toast.show();
        }



    }
}
