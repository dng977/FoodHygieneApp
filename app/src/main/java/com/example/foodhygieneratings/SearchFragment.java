package com.example.foodhygieneratings;

import android.os.Bundle;
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

import com.example.foodhygieneratings.search.AppDatabase;
import com.example.foodhygieneratings.search.BusinessType;
import com.example.foodhygieneratings.search.LocalAuthority;
import com.example.foodhygieneratings.search.FilterQuery;
import com.example.foodhygieneratings.search.HttpResponseListener;
import com.example.foodhygieneratings.search.SearchRequestHandler;
import com.example.foodhygieneratings.search.SearchResultsFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, HttpResponseListener {
    private SearchRequestHandler searchRequestHandler;

    private SearchResultsFragment searchResultsFragment;

    private AddressFilter addressFilter;
    private NearbyFilter nearbyFilter;
    private boolean nearbyFilterIsOn;
    private Spinner tobSpinner;
    private Button searchButton;

    //SEARCH DATA
    private int ratingValue;
    //DATABASE
    private ArrayList<LocalAuthority> filterData;

    private ArrayList<String> businessTypes;
    private ArrayList<String> authorities;
    private AppDatabase appDatabase;
    private int databaseFetched;
    private boolean filterDataEmpty;

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
        if(searchRequestHandler == null)
            searchRequestHandler = new SearchRequestHandler(this);

        appDatabase = ((MainActivity) getActivity()).getDatabase();

        //REQUEST FILTERDATA IF NEEDED
        fetchFilterData();
        //INITIALIZE FILTERDATA fields
        if(authorities == null) {
            authorities = new ArrayList<>();
            if (databaseFetched == 2) {
                this.authorities.add("Any");
                this.authorities.addAll(appDatabase.localAuthorityDao().retrieveNames());
            }
        }
        if(businessTypes == null){
            businessTypes = new ArrayList<>();
            if(databaseFetched == 2){
                this.businessTypes.addAll(appDatabase.businessTypeDao().retrieveNames());
            }
        }
        Log.e("authority size: ", "" + authorities.size());
        Log.e("businesses size: ", "" + businessTypes.size());
        // FILTER BY LOCATION
        RadioGroup locRadio = v.findViewById(R.id.locRadioGroup);
        locRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String tag = "";
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction fT = getChildFragmentManager().beginTransaction();
                switch (checkedId) {
                    case R.id.locRadioNearby:
                        tag = "locRadioNearby";
                        nearbyFilter = (NearbyFilter) fragmentManager.findFragmentByTag(tag);
                        if (nearbyFilter == null) {
                            nearbyFilter = new NearbyFilter();
                        }
                        fT.replace(R.id.locFilterFrame, nearbyFilter, tag);
                        nearbyFilterIsOn = true;
                        break;
                    case R.id.locRadioAny:
                        tag = "locRadioAny";
                        addressFilter = (AddressFilter) fragmentManager.findFragmentByTag(tag);
                        if (addressFilter == null) {
                            addressFilter = new AddressFilter();
                            Bundle bundle = new Bundle();

                            bundle.putStringArrayList("authorities", authorities);
                            addressFilter.setArguments(bundle);
                        }
                        fT.replace(R.id.locFilterFrame, addressFilter, tag);
                        nearbyFilterIsOn = false;
                        break;
                }
                fT.addToBackStack(null).commit();
            }
        });

        //locFilter = getChildFragmentManager().findFragmentByTag(tag);
        if (nearbyFilter == null) {
            nearbyFilter = new NearbyFilter();
            String tag = "locRadioNearby";
            getChildFragmentManager().beginTransaction().add(R.id.locFilterFrame, nearbyFilter, tag).commit();
            nearbyFilterIsOn = true;
        }

        //FILTER BY RATING
        SeekBar ratingSeek = v.findViewById(R.id.ratingSeekBar);
        ratingSeek.setOnSeekBarChangeListener(this);
        ratingValue = 5 - ratingSeek.getProgress();

        //FILTER BY TYPE OF BUSINESS
        if(businessTypes != null)
            tobAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item,businessTypes);
        tobSpinner = v.findViewById(R.id.spinnerBusinessFilter);
        tobSpinner.setAdapter(tobAdapter);

        //SEARCH BUTTON
        searchButton = v.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(this);
        return v;
    }

    private void getDatabaseNames(String type){
        if(type == "authority"){

        }
        if(type == "businessType"){
            this.businessTypes.addAll(appDatabase.businessTypeDao().retrieveNames());
        }
    }

    private void fetchFilterData(){
        databaseFetched = 0;
        if(appDatabase.businessTypeDao().itemsCount() == 0) {
            searchRequestHandler.httpRequest(getActivity().getApplicationContext(), "businesstypes/basic", SearchRequestHandler.QUERYTYPE.businessTypes);
        }
        else{
            databaseFetched ++;
        }
        if(appDatabase.localAuthorityDao().itemsCount() == 0){
            searchRequestHandler.httpRequest(getActivity().getApplicationContext(),"authorities/basic", SearchRequestHandler.QUERYTYPE.authorities);
        }
        else{
            databaseFetched ++;
        }
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //CONFIG ACTION BAR
        ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        supportActionBar.setTitle(R.string.searchBar);
        supportActionBar.setSubtitle("");
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
        //CHECK Whether filter data has been fetched
        if(databaseFetched !=2){
            Toast.makeText(getContext(),"Missing data - trying to fetch data from the server!",Toast.LENGTH_SHORT).show();
            fetchFilterData();
            return;
        }
        int buttonID = v.getId();
        if (buttonID == R.id.searchButton) {
            //ADD QUERY INFORMATION
            FilterQuery query = new FilterQuery();
            TextView nameText = getView().findViewById(R.id.nameText);
            String name = nameText.getText().toString();

            //addName
            query.addName(name);

            //ADDLOCATION
            MainActivity activity = ((MainActivity)getActivity());
            boolean locationOn = activity.isLocationOn();
            if(locationOn) {
                query.addCoordinates(activity.getLatitude(),activity.getLongitude());
                if (nearbyFilter.isVisible()) {
                    Log.e("", "NEARBY FILTER VISIBLE!");
                    query.addDistanceFromYou((nearbyFilter.getDistance()));
                } else {
                    query.addDistanceFromYou(9999); // measure the distance from wherever to you
                }
            }
            else{
                Toast.makeText(getContext(),"No access to location!",Toast.LENGTH_SHORT).show();
            }
            if (!nearbyFilter.isVisible()) {
                //AUTHORITIES AND ADDRESS
                Log.e("addressFilter: ", String.valueOf(addressFilter.isVisible()));
                query.addAddress(addressFilter.getAddress());

                int selectedPosition = addressFilter.getSpinner().getSelectedItemPosition();
                if(selectedPosition !=0) //not Any
                    query.addLocalAuthority(appDatabase.localAuthorityDao().retrieveIdByIndex(selectedPosition));
            }

            //addRating
            query.addRating(ratingValue);

            //addBusinessType

            query.addBusinessType(appDatabase.businessTypeDao().retrieveIdByIndex(tobSpinner.getSelectedItemPosition()));
            resultsBundle = new Bundle();
            resultsBundle.putString("rawQuery", query.getRawQuery());
            //Log.e("QUERY: ", query.getQueryString());

            searchRequestHandler.httpRequest(getContext(),query.getQueryString(), SearchRequestHandler.QUERYTYPE.establishments);
            //searchButton.setEnabled(false);

        }
    }

    private boolean populateFilterDataDB(JSONArray resultsArray, SearchRequestHandler.QUERYTYPE queryType){
        String ID;
        String name;
        switch(queryType){
            case authorities:
                ID = "LocalAuthorityId";
                name = "Name";
                try {
                    int index = 1; //index 0 is for ANY
                    for(int i=0; i<resultsArray.length();i++) {
                        JSONObject jsonObject = resultsArray.getJSONObject(i);
                        //DON'T ADD SCOTLAND SCHEMETYPES
                        if(jsonObject.getInt("SchemeType")==2)
                            continue;
                        //String regionName = jsonObject.getString("RegionName");
                        LocalAuthority data = new LocalAuthority(jsonObject.getInt(ID),jsonObject.getString(name),"empty",index);
                        appDatabase.localAuthorityDao().insertAuthority(data);
                        index++;
                    }
                    databaseFetched ++;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case businessTypes:
                ID = "BusinessTypeId";
                name = "BusinessTypeName";
                try {
                    for(int i=0; i<resultsArray.length();i++) {
                        JSONObject jsonObject = resultsArray.getJSONObject(i);
                        //if(jsonObject.getInt(ID)==-1)
                        //    continue;
                        BusinessType data = new BusinessType(jsonObject.getInt(ID),jsonObject.getString(name),i);
                        Log.e("BUSSINESS ID: ",String.valueOf(jsonObject.getInt(ID)));

                        appDatabase.businessTypeDao().insertBusiness(data);
                    }
                    databaseFetched ++;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            default:
                return false;
        }
        return true;

    }

    @Override
    public void responseSuccess(JSONArray resultsArray, SearchRequestHandler.QUERYTYPE queryType) {
        if(queryType == SearchRequestHandler.QUERYTYPE.establishments) {
            FragmentTransaction fT;
            String tag = Integer.toString(R.id.searchButton);
            searchResultsFragment = new SearchResultsFragment();
            resultsBundle.putString("resultsJSON", resultsArray.toString());
            searchResultsFragment.setArguments(resultsBundle);
            //Log.e(" SearchResult:", resultsArray.toString());
            fT = getFragmentManager().beginTransaction();
            fT.replace(R.id.frag_frame, searchResultsFragment, tag).addToBackStack(null);
            fT.commit();
        }
        else if (queryType != SearchRequestHandler.QUERYTYPE.pageNumber){
            populateFilterDataDB(resultsArray,queryType);
            Log.e("PopulateDatabase!", "");
            Log.e("queryType ", queryType.toString());
            Log.e("Database fetch: ", " " + databaseFetched);
            if(databaseFetched == 2){
                notifyAdapters();
            }
        }
    }


    private void notifyAdapters() {
        businessTypes.clear();
        this.businessTypes.addAll(appDatabase.businessTypeDao().retrieveNames());
        authorities.clear();
        this.authorities.add("Any");
        this.authorities.addAll(appDatabase.localAuthorityDao().retrieveNames());

        tobAdapter.notifyDataSetChanged();

        if(addressFilter != null && addressFilter.isVisible()){
            addressFilter.getAdapter().notifyDataSetChanged();
        }
        else{
            addressFilter = new AddressFilter();
            Bundle bundle = new Bundle();

            bundle.putStringArrayList("authorities", authorities);
            addressFilter.setArguments(bundle);
        }

    }

    @Override
    public void responseError(String error) {
        if(error != null)
            Log.e("BAD RESPONSE",error);
        else
            Log.e("BAD RESPONSE","!");
        Toast.makeText(getContext(),"Can't fetch data from Server: Check Internet Connection or Provide a filter!",Toast.LENGTH_SHORT).show();
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
        super.onSaveInstanceState(outState);
        Log.e("SearchFragment: ","OnSaveInstanceState called!");
    }

    public View getSearchButton() {
        return searchButton;
    }

}
