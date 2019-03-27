package com.example.foodhygieneratings.search;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchRequestHandler {
    public static enum QUERYTYPE{
        regions,
        authorities,
        businessTypes,
        establishments,
        pageNumber
    }
    private HttpResponseListener fragment;

    public SearchRequestHandler(HttpResponseListener fragment){
        this.fragment = fragment;
    }

    public void httpRequest(Context applicationContext, String query,final QUERYTYPE queryType) {
        RequestQueue requestQueue = Volley.newRequestQueue(applicationContext);
        String url = "http://api.ratings.food.gov.uk/" + query;
        Log.e("HttpRequest - query: ", url);
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.e(" httpRequest: ", "Response Received!");
                    //Toast.makeText(fragment.getContext(), "Response Received!", Toast.LENGTH_SHORT).show();
                    JSONArray resultsArray;
                    switch(queryType){
                        case regions:
                            resultsArray = response.getJSONArray("regions/basic");
                            fragment.responseSuccess(resultsArray,queryType);
                            break;
                        case authorities:
                            resultsArray = response.getJSONArray("authorities");
                            fragment.responseSuccess(resultsArray,queryType);
                            break;
                        case establishments:
                            resultsArray = response.getJSONArray(queryType.toString());
                            fragment.responseSuccess(resultsArray,queryType);
                            break;
                        case businessTypes:
                            resultsArray = response.getJSONArray(queryType.toString());
                            fragment.responseSuccess(resultsArray,queryType);
                            break;
                        case pageNumber:
                            resultsArray = response.getJSONArray("establishments");
                            fragment.responseSuccess(resultsArray,queryType);
                            break;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        String errorText = "Bad response(check internet connection)";

                        if(error.getMessage() != null)
                            fragment.responseError(error.getMessage());
                        else
                            fragment.responseError(errorText);
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> headers = new HashMap<String,String>();
                headers.put("x-api-version","2");
                return headers;
            }
        };
        requestQueue.add(getRequest);
        Log.e("", "REQUEST SEND!");
    }
}
