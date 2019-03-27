package com.example.foodhygieneratings.search;

import android.arch.persistence.room.ColumnInfo;

public class FilterDataMap {
    @ColumnInfo(name="id")
    public String firstName;

    @ColumnInfo(name="last_name")
    public String lastName;
}
