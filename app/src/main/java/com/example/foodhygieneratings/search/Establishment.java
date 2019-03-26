package com.example.foodhygieneratings.search;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;


/**
Holds establishment details.
 */
@Entity
public class Establishment {
    @PrimaryKey
    @NonNull
    private String ID;

    @ColumnInfo(name="jsonObject")
    private String jsonObject;

    @Ignore
    private String businessName;
    @Ignore
    private String businessType;
    @Ignore
    private int businessTypeID;
    @Ignore
    private String address;
    @Ignore
    private String postCode;
    @Ignore
    private String ratingValue;
    @Ignore
    private String ratingDate;

    public Establishment(String jsonObject){
        this.jsonObject = jsonObject;
        try {
            unzipJson(new JSONObject(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Establishment(JSONObject establishmentJson) {
        this.jsonObject = establishmentJson.toString();
        unzipJson(establishmentJson);
    }
    private void unzipJson(JSONObject establishmentJson){
        try {
        this.ID = String.valueOf(establishmentJson.getInt("FHRSID"));
        //if(this.ID == null){
        //    Random random = new Random();
        //    this.ID = String.valueOf(random.nextInt(10000000));

        //}
        this.businessName = establishmentJson.getString("BusinessName");
        this.businessType = establishmentJson.getString("BusinessType");
        this.businessTypeID = establishmentJson.getInt("BusinessTypeID");
        this.address =
                establishmentJson.getString("AddressLine1")+ "\n" +
                        establishmentJson.getString("AddressLine2")+ "\n" +
                        establishmentJson.getString("AddressLine3")+ "\n" +
                        establishmentJson.getString("AddressLine4");
        if(address.trim().isEmpty())
            this.address = "--No address--";
        this.postCode = establishmentJson.getString("PostCode");
        this.ratingValue = establishmentJson.getString("RatingValue");

        if(ratingValue.matches("Exempt"))
            ratingValue = "-";
        else if (ratingValue.matches("Awaiting.*"))
            ratingValue = "due";

        this.ratingDate = establishmentJson.getString("RatingDate");
        this.ratingDate = this.ratingDate.substring(0,10);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(String jsonObject) {
        this.jsonObject = jsonObject;
    }

    public double getDistanceFromYou(){
        return -1;
    }

    public String getName(){
        return businessName;
    }
    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getBusinessType() {
        return businessType;
    }

    public int getBusinessTypeID() {
        return businessTypeID;
    }

    public String getAddress(){
        return address;
    }
    public String getTypeAddress() {
        return address + "\n" + businessType;
    }

    public String getPostCode() {
        return postCode;
    }

    public String getRatingValue() {
        return ratingValue;
    }

    public String getRatingDate() {
        return ratingDate;
    }
}
