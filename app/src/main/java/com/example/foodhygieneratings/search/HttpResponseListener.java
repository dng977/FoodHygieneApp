package com.example.foodhygieneratings.search;

import android.content.Context;

import org.json.JSONArray;

public interface HttpResponseListener {
    public void responseSuccess(JSONArray array, SearchRequestHandler.QUERYTYPE queryType);
    public void responseError(String error);

}
