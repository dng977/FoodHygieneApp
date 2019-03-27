package com.example.foodhygieneratings.search;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface BusinessTypeDao {
    @Insert
    void insertBusiness(BusinessType businessType);

    @Query("SELECT * FROM businesstype")
    List<BusinessType> retrieveAll();

    @Query("SELECT * FROM businesstype WHERE ID=:id LIMIT 1")
    BusinessType retrieveById(int id);

    @Query("SELECT name FROM businesstype ORDER BY listIndex")
    List<String> retrieveNames();

    @Query("DELETE FROM businesstype WHERE ID=:id")
    void deleteAuthority(String id);

    @Query("SELECT ID FROM businesstype WHERE listIndex=:ind ")
    int retrieveIdByIndex(int ind);

    @Query("SELECT COUNT(*) FROM businesstype")
    int itemsCount();

    @Query("DELETE FROM businesstype")
    void deleteAll();
}
