package com.example.foodhygieneratings.search;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class BusinessType {
    @PrimaryKey
    private int ID;

    @ColumnInfo(name="name")
    private String name;

    @ColumnInfo(name="listIndex")
    private int listIndex;

    /*

     */
    public BusinessType(int ID, String name, int listIndex) {
        this.ID = ID;
        this.name = name;
        this.listIndex = listIndex;
    }

    @NonNull
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
}
