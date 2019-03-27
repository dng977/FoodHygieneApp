package com.example.foodhygieneratings;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class NearbyFilter extends Fragment implements SeekBar.OnSeekBarChangeListener {

    private TextView distance;
    private int distanceValue;
    private SeekBar seekBar;
    public NearbyFilter() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_nearby_filter, container, false);
        distance = v.findViewById(R.id.distanceText);
        seekBar = v.findViewById(R.id.seekBarDistance);
        seekBar.setOnSeekBarChangeListener(this);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        distanceValue = seekBar.getProgress() +1;
        distance.setText(distanceValue + " miles");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //Log.d("", "OnsaveIntstanceState called!");
        outState.putInt("distance", distanceValue);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.d("NEARBYFILTER", "onDESTROYcalled!");
    }

    private void changeDistance(int newDistance){
        if(newDistance <=3){
            distanceValue = newDistance + 1;
        }
        else
            if(newDistance<=8) {
                distanceValue = (newDistance-3)*5;
            }
            else{
                distanceValue = 100;
            }
    }

    public int getDistance(){
        return distanceValue;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        changeDistance(progress);
        distance.setText(distanceValue + " miles");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
