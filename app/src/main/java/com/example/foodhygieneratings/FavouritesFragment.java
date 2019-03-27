package com.example.foodhygieneratings;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.foodhygieneratings.search.AppDatabase;
import com.example.foodhygieneratings.search.Establishment;
import com.example.foodhygieneratings.search.EstablishmentsAdapter;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavouritesFragment extends Fragment {

    private ArrayList<Establishment> establishments;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;

    private AppDatabase database;

    public FavouritesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_favourites, container, false);

        database = ((MainActivity)getActivity()).getDatabase();
        establishments = new ArrayList<Establishment>();
        populateEstablishments();

        recyclerView = v.findViewById(R.id.favouritesRecycler);
        recyclerView.setHasFixedSize(true);
        Log.e("EST LENGTH: ",establishments.size() + "");
        recyclerViewAdapter = new EstablishmentsAdapter(establishments, getResources(), getFragmentManager());

        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);


        return v;
    }
    private void populateEstablishments(){
        establishments = (ArrayList<Establishment>)database.establishmentDao().retrieveAll();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //CONFIG ACTION BAR
        ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        supportActionBar.setTitle(R.string.menu_favourites);
        supportActionBar.setSubtitle("");
        setHasOptionsMenu(false);
        if (supportActionBar != null){
            supportActionBar.setDisplayHomeAsUpEnabled(false);
            supportActionBar.setDisplayShowHomeEnabled(false);
        }
        setHasOptionsMenu(false);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().getSupportFragmentManager().popBackStack();
                return true;
        }
        return true;
    }

}
