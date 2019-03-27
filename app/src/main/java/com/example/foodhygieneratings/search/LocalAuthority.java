package com.example.foodhygieneratings.search;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class LocalAuthority {
    @PrimaryKey
    private int ID;

    @ColumnInfo(name="name")
    private String name;

    @ColumnInfo(name="listindex")
    private int listIndex;

    @ColumnInfo(name="region")
    private String regionName;


    /*

     */
    public LocalAuthority(int ID, String name,String regionName, int listIndex) {
        this.ID = ID;
        this.name = name;
        this.regionName = regionName;
        this.listIndex = listIndex;

    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getListIndex() {
        return listIndex;
    }

    public void setListIndex(int listIndex) {
        this.listIndex = listIndex;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }
}
