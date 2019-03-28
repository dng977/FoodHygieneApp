package com.example.foodhygieneratings.search;


import android.app.Activity;
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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class SingleResultFragment extends Fragment implements View.OnClickListener {


    private String queryResult;
    private Establishment establishment;
    private boolean inFavourites = false;

    MapView mMapView;
    private GoogleMap googleMap;

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
            indBusinessType.setText(establishment.getBusinessType());
            TextView indResultRating = v.findViewById(R.id.indResultRating);
            EstablishmentsAdapter.setRating(getResources(),establishment.getRatingValue(),indResultRating);
            TextView ratedOn = v.findViewById(R.id.indRatedOn);
            ratedOn.setText(getResources().getString(R.string.date_of_last_inspection) + " " + establishment.getRatingDate());
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

        mMapView = (MapView) getView().findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                LatLng thisLatLng = new LatLng(establishment.getLatitude(),establishment.getLongitude());
                Log.e("latlong", establishment.getLatitude()+ " "  +establishment.getLongitude());
                googleMap.addMarker(new MarkerOptions()
                        .position(thisLatLng)
                        .title(establishment.getName())
                        .zIndex(1.0f)
                        .flat(true)
                        .icon(BitmapDescriptorFactory.defaultMarker()));

                //HANDLE NO LOCATION
                MainActivity mainActivity = (MainActivity) getActivity();
                if(mainActivity.isLocationOn()){
                    Log.e("no location!", "");
                    LatLng myLocation = new LatLng(mainActivity.getLatitude(),mainActivity.getLongitude());
                    googleMap.addMarker(new MarkerOptions()
                            .position(myLocation)
                            .title("Your location")
                            .icon(BitmapDescriptorFactory.fromResource(android.R.drawable.ic_menu_mylocation)));

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(thisLatLng);
                    builder.include(myLocation);

                    int padding = 200; // offset from edges of the map in pixels
                    CameraUpdate cu;
                    cu = CameraUpdateFactory.newLatLngBounds(builder.build(),padding);
                    googleMap.moveCamera(cu);

                }
                else{
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(thisLatLng).zoom(15).build();
                    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }


                //googleMap.animateCamera(cu);

            }
        });
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
