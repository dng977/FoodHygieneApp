package com.example.foodhygieneratings.search;

import android.util.Log;

public class FilterQuery {
    private String name;
    private String address;
    private int rating;
    private int businessID;

    private final String schemeTypeKeyQuery = "schemeTypeKey=FHRS";
    private int pageSize;
    private int pageNumber;

    private int distanceFromYou;
    private double latitude;
    private double longitude;
    private boolean locationSet = false;

    private String sortFilter;
    private String query;
    private String queryType;
    public FilterQuery() {
        this.query = "?";
        this.query += schemeTypeKeyQuery;
        this.pageSize = 40;
        this.pageNumber = 1;
        this.sortFilter = "&sortOptionKey=" + "distance";
        this.queryType = "establishments";
        this.distanceFromYou = 500;
        this.query = queryType + this.query;

    }
    public FilterQuery(String rawQuery){
        this.query = rawQuery;
        this.pageSize = 50;
        this.pageNumber = 1;
        this.sortFilter = "&sortOptionKey=" + "distance";
    }

    public void addName(String name) {
        if(!name.matches("[/s]*")){
            this.query +="&name=" + name;
        }
    }

    public void setSortFilter(String sortOptionKey){
        this.sortFilter = "&sortOptionKey=" + sortOptionKey;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    /**
     *
     * @param latitude
     * @param longitude
     */
    public void setCoodinates(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public void addCoordinates(double latitude, double longitude){
        setCoodinates(latitude,longitude);
        this.query += "&longitude=" + longitude + "&latitude=" + latitude;
    }
    public String getPageNumberQuery(){
        return  "&pageNumber=" + this.pageNumber;
    }
    public String getPageSizeQuery(){
        return  "&pageSize=" + this.pageSize;
    }

    public void addDistanceFromYou(int distanceFromYou){
        this.query += "&maxDistanceLimit=" + distanceFromYou;
    }
    private String getDistanceFromYou(){
        return "&maxDistanceLimit=" + distanceFromYou;
    }
    public void addAddress(String address) {
        if(!address.matches("[/s]*")) {
            this.query += "&address=" + address;
        }
    }

    public void addRating(int rating) {
        this.query += "&ratingKey=" + rating +"&ratingOperatorKey=GreaterThanOrEqual";
    }

    public void addLocalAuthority(int localAuthId){
            this.query += "&localAuthorityId=" + localAuthId;
    }
    public void addBusinessType(int businessID) {
        if(businessID != -1)
            this.query += "&businessTypeId=" + businessID;
    }
    public String getRawQuery(){
        return query;
    }

    public String getQueryString(){
        return query + getPageSizeQuery() + getPageNumberQuery() + sortFilter;
    }

    public void increasePageNumber() {
        this.pageNumber++;
    }


}
