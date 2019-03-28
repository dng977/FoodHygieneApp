package com.example.foodhygieneratings;

import android.Manifest;
import android.app.AlertDialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.example.foodhygieneratings.search.AppDatabase;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MainActivity extends AppCompatActivity{
    private Toolbar toolbar;
    private Fragment fragment;
    private FragmentManager fm;
    private BottomNavigationView navigation;
    private int previousItem;
    private AppDatabase database;

    private final int FINE_LOCATION_PERMISSION = 1;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private double longitude;
    private double latitude;
    private final boolean deleteDatabase = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        //TOOLBAR
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //BOTOM NAVIGATAION
        navigation = findViewById(R.id.navigation);
        setupBottomNavigation(navigation);

        //PUT SEARCH FRAGMENT
        fm = getSupportFragmentManager();
        String tag = Integer.toString(R.id.navigation_search);
        if (savedInstanceState == null) {
            fragment = new SearchFragment();
            fm.beginTransaction().add(R.id.frag_frame, fragment, tag).commit();
        }
        //DATABASE
        database = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "FavouritesDatabase")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        if(deleteDatabase)
            deleteDatabase();

        //LOCATION
        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                Log.e("(CHANGED)LONG, LAT :", longitude + " , " + latitude);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationRequest();
    }

    private void deleteDatabase(){
        database.businessTypeDao().deleteAll();
        database.localAuthorityDao().deleteAll();
    }

    private void locationRequest(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setMessage(R.string.loc_request)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                requestLocPerms();
                            }
                        })
                        .create()
                        .show();
            } else {
                requestLocPerms();
            }
        } else {
                attachLocManager();
                Location initialLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                longitude = initialLocation.getLongitude();
                latitude = initialLocation.getLatitude();
                Log.e(" INITIAL LONG, LAT :", longitude + " , " + latitude);
            }
            else{
                Log.e("Main activity: ", " No location");
            }

        }
    }
    private void requestLocPerms() {
        Log.e("RequestLocPerms", " notify");
        ActivityCompat.requestPermissions(MainActivity.this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_PERMISSION);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case FINE_LOCATION_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    attachLocManager();
                } else {
                }
                return;
            }
        }
    }

    public void attachLocManager(){
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,10,locationListener);
        }catch (SecurityException err){
            Log.wtf("Security Exception: ",err.getMessage());
        }
    }

    private void setupBottomNavigation(final BottomNavigationView bottomNavigation) {
        bottomNavigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        if (previousItem != menuItem.getItemId()) {
                            String tag = Integer.toString(menuItem.getItemId());
                            switch (menuItem.getItemId()) {
                                case R.id.navigation_search:
                                    fragment = fm.findFragmentByTag(tag);
                                    if (fragment == null) {
                                        fragment = new SearchFragment();
                                        //locFilter.setArguments(arguments);
                                    }
                                    break;
                                case R.id.navigation_favourites:
                                    fragment = fm.findFragmentByTag(tag);
                                    if (fragment == null) {
                                        fragment = new FavouritesFragment();
                                        //locFilter.setArguments(arguments);
                                    }
                                    break;
                                case R.id.navigation_about:
                                    fragment = fm.findFragmentByTag(tag);
                                    if (fragment == null) {
                                        fragment = new InfoFragment();
                                        //locFilter.setArguments(arguments);
                                    }
                                    break;
                                default:
                                    return false;
                            }
                            FragmentTransaction fT = fm.beginTransaction();
                            fT.replace(R.id.frag_frame, fragment, tag).addToBackStack(null);
                            fT.commit();
                            previousItem = menuItem.getItemId();
                        }
                        return true;
                    }
                });
    }

    @Override
    public void onBackPressed() {
        //ALLOW USING THE BACK BUTTON ONLY WHEN LISTING RESULT VISIBLE
        Fragment fr = fm.findFragmentByTag(Integer.toString(R.id.searchButton));
        if (fr != null && fr.isVisible()){
            super.onBackPressed();
        }
    }

    public AppDatabase getDatabase() {
        return database;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public boolean isLocationOn() {
        if(latitude != 0 || longitude != 0)
            return true;
        else
            return false;
    }
}
