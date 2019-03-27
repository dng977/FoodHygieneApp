package com.example.foodhygieneratings.search;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.foodhygieneratings.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchResultsFragment extends Fragment implements HttpResponseListener {

    private ArrayList<Establishment> establishments;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private String queryResults;
    private String rawQuery;
    private FilterQuery filterQuery;
    private SearchRequestHandler searchRequestHandler;
    private String sortString;

    public SearchResultsFragment() {
        // Required empty public constructor
    }

    private void populateEstablishments(String queryResults, boolean clearArray) throws JSONException {
        if(clearArray)
            establishments.clear();
        JSONArray array = new JSONArray(queryResults);
        for (int i=0;i< array.length();i++){
            JSONObject estJson = array.getJSONObject(i);
            establishments.add(new Establishment(estJson));
        }
    }
    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search_results, container, false);
        if(queryResults == null)
            queryResults = getArguments().getString("resultsJSON");
        if(rawQuery == null)
            rawQuery = getArguments().getString("rawQuery");

        filterQuery = new FilterQuery(rawQuery);
        establishments = new ArrayList<Establishment>();
        if (queryResults != null){
            try {
                populateEstablishments(queryResults, false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            Log.e("SearchResults: ", "QUERY STRING IS NULL");
        }
        searchRequestHandler = new SearchRequestHandler(this);
        recyclerView = v.findViewById(R.id.searchResultsRecycler);
        recyclerView.setHasFixedSize(true);

        recyclerViewAdapter = new EstablishmentsAdapter(establishments, getResources(), getFragmentManager());

        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        ((EstablishmentsAdapter) recyclerViewAdapter).setOnBottomReachedListener(new OnBottomReachedListener() {
            @Override
            public void onBottomReached(int position) {
                filterQuery.increasePageNumber();
                String query = filterQuery.getQueryString();
                searchRequestHandler.httpRequest(getContext(),query, SearchRequestHandler.QUERYTYPE.pageNumber);
            }
        });

        return v;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        supportActionBar.setTitle(R.string.resultsBar);
        if(sortString == null)
            supportActionBar.setSubtitle("distance");
        else{
            supportActionBar.setSubtitle(sortString);
        }
        if (supportActionBar != null){
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowHomeEnabled(true);
        }
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu,menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String query;
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().getSupportFragmentManager().popBackStack();
                return true;
            case R.id.distanceSort:
                filterQuery.setSortFilter("distance");
                sortString = item.getTitle().toString();
                break;
            case R.id.alphaSort:
                filterQuery.setSortFilter("alpha");
                sortString = item.getTitle().toString();
                break;
            case R.id.alphaDSort:
                filterQuery.setSortFilter("desc_alpha");
                sortString = item.getTitle().toString();
                break;
            case R.id.ratingSort:
                filterQuery.setSortFilter("rating");
                sortString = item.getTitle().toString();
                break;
            case R.id.ratingDSort:
                filterQuery.setSortFilter("desc_rating");
                sortString = item.getTitle().toString();
                break;
            case R.id.relevanceSort:
                filterQuery.setSortFilter("Relevance");
                sortString = item.getTitle().toString();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        Log.e("onOptionsItemSelected", "--");
        filterQuery.setPageNumber(1);
        query = filterQuery.getQueryString();

        searchRequestHandler.httpRequest(getContext(),query, SearchRequestHandler.QUERYTYPE.establishments);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle(sortString);


        return super.onOptionsItemSelected(item);
    }


    @Override
    public void responseSuccess(JSONArray resultsArray, SearchRequestHandler.QUERYTYPE queryType) {
        try {
            Log.e("RESPONSE: ", resultsArray.toString());
            queryResults = resultsArray.toString();
            switch(queryType){
                case establishments:
                    populateEstablishments(resultsArray.toString(), true);
                    recyclerViewAdapter.notifyDataSetChanged();
                    break;
                case pageNumber:
                    populateEstablishments(resultsArray.toString(),false);
                    recyclerViewAdapter.notifyItemRangeInserted(recyclerViewAdapter.getItemCount(),resultsArray.length());
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
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

}
