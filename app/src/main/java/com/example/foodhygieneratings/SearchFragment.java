package com.example.foodhygieneratings;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodhygieneratings.search.FilterQuery;
import com.example.foodhygieneratings.search.HttpResponseListener;
import com.example.foodhygieneratings.search.SearchRequestHandler;
import com.example.foodhygieneratings.search.SearchResultsFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, HttpResponseListener {
    private SearchRequestHandler searchRequestHandler;
    private SearchResultsFragment searchResultsFragment;
    private Fragment locFilter;
    private Spinner tobSpinner;
    private Button searchButton;
    private int ratingValue;


    private HashMap<Integer, Integer> businessesIdMap;
    private ArrayList<String> businesses;
    private ArrayAdapter tobAdapter;

    private Bundle resultsBundle;
    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("SearchFragment: ","OnCreateView called!");
        View v = inflater.inflate(R.layout.fragment_search, container, false);

        // FILTER BY LOCATION

        RadioGroup locRadio = v.findViewById(R.id.locRadioGroup);
        locRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String tag = "";
                FragmentManager fragmentManager = getChildFragmentManager();
                switch (checkedId) {
                    case R.id.locRadioNearby:
                        tag = "locRadioNearby";
                        locFilter = (NearbyFilter) fragmentManager.findFragmentByTag(tag);
                        if (locFilter == null) {
                            locFilter = new NearbyFilter();
                            //locFilter.setArguments(arguments);
                        }

                        break;
                    case R.id.locRadioAny:
                        tag = "locRadioAny";
                        locFilter = (AddressFilter) fragmentManager.findFragmentByTag(tag);
                        if (locFilter == null) {
                            locFilter = new AddressFilter();
                            //locFilter.setArguments(arguments);
                        }
                        break;
                }
                fragmentManager.beginTransaction()
                        .replace(R.id.locFilterFrame, locFilter, tag)
                        .addToBackStack(null)
                        .commit();
            }
        });

        //locFilter = getChildFragmentManager().findFragmentByTag(tag);
        if (locFilter == null) {
            if(savedInstanceState!=null)
                locFilter = getChildFragmentManager().getFragment(savedInstanceState,"locFilter");
            else
                locFilter = new NearbyFilter();
            String tag = "locRadioNearby";
            getChildFragmentManager().beginTransaction().add(R.id.locFilterFrame, locFilter, tag).commit();
        }


        //FILTER BY RATING
        SeekBar ratingSeek = v.findViewById(R.id.ratingSeekBar);
        ratingSeek.setOnSeekBarChangeListener(this);
        ratingValue = 5 - ratingSeek.getProgress();

        //FILTER BY TYPE OF BUSINESS
        searchRequestHandler = new SearchRequestHandler(this);
        if(tobAdapter == null) {
            businessesIdMap = new HashMap<Integer,Integer>();

            businesses = new ArrayList<String>();
            tobAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item,businesses);

            String query = "businesstypes/basic";
            searchRequestHandler.httpRequest(getActivity().getApplicationContext(),query, SearchRequestHandler.QUERYTYPE.businessTypes);
        }
        tobSpinner = v.findViewById(R.id.spinnerBusinessFilter);
        tobSpinner.setAdapter(tobAdapter);

        //SEARCH BUTTON
        searchButton = v.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(this);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //CONFIG ACTION BAR
        ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        supportActionBar.setTitle(R.string.searchBar);
        setHasOptionsMenu(false);
        if (supportActionBar != null){
            supportActionBar.setDisplayHomeAsUpEnabled(false);
            supportActionBar.setDisplayShowHomeEnabled(false);
        }
        setHasOptionsMenu(false);

        //Update rating text
        updateRatingText();
    }

    private void updateRatingText(){
        TextView ratingText = getView().findViewById(R.id.raitingText);
        ratingText.setText(ratingValue + "-" + "5");
    }

    @Override
    public void onClick(View v){
        if(businessesIdMap.isEmpty()){
            String query = "businesstypes/basic";
            searchRequestHandler.httpRequest(getActivity().getApplicationContext(),query, SearchRequestHandler.QUERYTYPE.businessTypes);
            return;
        }
        int buttonID = v.getId();
        if (buttonID == R.id.searchButton) {
            FilterQuery query = new FilterQuery();
            TextView nameText = getView().findViewById(R.id.nameText);
            String name = nameText.getText().toString();
            Log.e("NAME", name);
            query.addName(name);
            if (locFilter instanceof NearbyFilter) {
                query.addDistanceFromYou(((NearbyFilter) locFilter).getDistance());
            } else if (locFilter instanceof AddressFilter) {
                query.addAddress(((AddressFilter) locFilter).getAddress());
            }
            query.addBusinessType(businessesIdMap.get(tobSpinner.getSelectedItemPosition()));
            resultsBundle = new Bundle();
            resultsBundle.putString("rawQuery", query.getRawQuery());
            //Log.e("QUERY: ", query.getQueryString());

            searchRequestHandler.httpRequest(getContext(),query.getQueryString(), SearchRequestHandler.QUERYTYPE.establishments);
            searchButton.setEnabled(false);

        }

    }

    @Override
    public void responseSuccess(JSONArray resultsArray, SearchRequestHandler.QUERYTYPE queryType) {
        switch(queryType){
            case businessTypes:
                businesses.clear();
                try{
                    for (int i=0;i<resultsArray.length();i++){
                        JSONObject jo = resultsArray.getJSONObject(i);
                        businessesIdMap.put(i,Integer.parseInt(jo.getString("BusinessTypeId")));
                        businesses.add(jo.getString("BusinessTypeName"));
                    }
                }
                catch (JSONException err){}
                tobAdapter.notifyDataSetChanged();
                break;
            case establishments:
                FragmentTransaction fT;
                String tag = Integer.toString(R.id.searchButton);
                searchResultsFragment = new SearchResultsFragment();
                resultsBundle.putString("resultsJSON", resultsArray.toString());
                searchResultsFragment.setArguments(resultsBundle);
                //Log.e(" SearchResult:", resultsArray.toString());
                fT = getFragmentManager().beginTransaction();
                fT.replace(R.id.frag_frame, searchResultsFragment, tag).addToBackStack(null);
                fT.commit();
                break;
        }
    }

    @Override
    public void responseError(String error) {
        if(error != null)
            Log.e("BAD RESPONSE",error);
        else
            Log.e("BAD RESPONSE","!");
        Toast.makeText(getContext(),"Bad response(Check internet connection)!",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        ratingValue = 5- progress;
        updateRatingText();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        String tag = "OnSave";
        super.onSaveInstanceState(outState);
        Log.e("SearchFragment: ","OnSaveInstanceState called!");
        getFragmentManager().putFragment(outState,"locFilter",locFilter);
    }

    public View getSearchButton() {
        return searchButton;
    }

}
