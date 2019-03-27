package com.example.foodhygieneratings.search;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.foodhygieneratings.MainActivity;
import com.example.foodhygieneratings.R;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class SingleResultFragment extends Fragment implements View.OnClickListener {


    private String queryResult;
    private Establishment establishment;
    private boolean inFavourites = false;
    public SingleResultFragment() {
        // Required empty public constructor
    }
    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View v = inflater.inflate(R.layout.fragment_single_result, container, false);
        Button favButton = v.findViewById(R.id.addToFavButton);
        favButton.setOnClickListener(this);
        this.queryResult = getArguments().getString("resultJSON");

        try {
            JSONObject jsonObject = new JSONObject(queryResult);
            establishment = new Establishment(jsonObject);

            AppDatabase database = ((MainActivity)getActivity()).getDatabase();
            if(database.establishmentDao().retrieveEstablishmentByID(establishment.getID()+ "") != null){
                inFavourites = true;
            }
            changeFavButton(favButton);

            TextView indResultName = v.findViewById(R.id.indResultName);
            indResultName.setText(establishment.getName());
            //Button moreInfoButton = viewHolder.moreInfoButton;
            //moreInfoButton.setText(position + " More info");
            TextView indBusinessType = v.findViewById(R.id.indBusinessType);
            indBusinessType.setText("Type: " + establishment.getBusinessType());
            TextView indResultRating = v.findViewById(R.id.indResultRating);
            EstablishmentsAdapter.setRating(getResources(),establishment.getRatingValue(),indResultRating);
            TextView ratedOn = v.findViewById(R.id.indRatedOn);
            ratedOn.setText(getResources().getString(R.string.date_of_last_inspection) + establishment.getRatingDate());
            TextView indResultAddress = v.findViewById(R.id.indResultAddress);
            indResultAddress.setText(establishment.getAddress());
        } catch (JSONException e) {
            e.printStackTrace();
        }

       return v;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        supportActionBar.setTitle(establishment.getDistanceString() + " " + getResources().getString(R.string.distance));
        supportActionBar.setSubtitle("");
        if (supportActionBar != null){
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowHomeEnabled(true);
        }
        setHasOptionsMenu(true);
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

    private void changeFavButton(Button b){
        if(inFavourites){
            b.setText("Remove from Favourites");
            b.setBackgroundColor(getResources().getColor(R.color.removeFromFavColor));
        }
        else{
            b.setText("Add to Favourites");
            b.setBackgroundColor(getResources().getColor(R.color.addToFavColor));
        }
    }
    @Override
    public void onClick(View v) {
        Log.e("onClick", "Tritggerd");
        if(v.getId() == R.id.addToFavButton){
            MainActivity activity = (MainActivity) getActivity();
            AppDatabase database = activity.getDatabase();
            if(inFavourites){
                database.establishmentDao().deleteEstablishmentByID(establishment.getID());
                inFavourites = false;
            }
            else{
                Log.e("Establishment id: ", establishment.getID());
                database.establishmentDao().insertEstablishment(establishment);
                inFavourites = true;
            }
            changeFavButton((Button)v);
        }

    }
}
