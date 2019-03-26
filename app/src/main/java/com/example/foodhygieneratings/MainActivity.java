package com.example.foodhygieneratings;

import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomMasterTable;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.example.foodhygieneratings.search.EstDatabase;


public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Fragment fragment;
    private FragmentManager fm;
    private BottomNavigationView navigation;
    private int previousItem;
    private EstDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fm = getSupportFragmentManager();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigation = findViewById(R.id.navigation);
        setupBottomNavigation(navigation);

        String tag = Integer.toString(R.id.navigation_search);
        if (savedInstanceState == null) {
            fragment = new SearchFragment();
            fm.beginTransaction().add(R.id.frag_frame, fragment, tag).commit();
        }
        database = Room.databaseBuilder(getApplicationContext(),
                EstDatabase.class, "FavouritesDatabase")
                .allowMainThreadQueries()
                //.fallbackToDestructiveMigration()
                .build();
        Log.e("Room master table: ", RoomMasterTable.TABLE_NAME);
        Log.e("Current hash: ", String.valueOf(database.hashCode()));

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

    public EstDatabase getDatabase() {
        return database;
    }
}
