package com.example.foodhygieneratings.search;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface LocalAuthorityDao {
    @Insert
    void insertAuthority(LocalAuthority localAuthority);

    @Query("SELECT * FROM LocalAuthority")
    List<LocalAuthority> retrieveAll();

    @Query("SELECT * FROM LocalAuthority WHERE ID=:id LIMIT 1")
    LocalAuthority retriveAuthorityById(int id);

    @Query("SELECT name FROM LocalAuthority ORDER BY listindex")
    List<String> retrieveNames();

    @Query("DELETE FROM LocalAuthority WHERE ID=:id")
    void deleteAuthority(String id);

    @Query("SELECT ID FROM LocalAuthority WHERE listindex=:ind ")
    int retrieveIdByIndex(int ind);

    @Query("SELECT region FROM LocalAuthority WHERE id=:id")
    int retrieveRegion(int id);

    @Query("SELECT COUNT(*) FROM LocalAuthority")
    int itemsCount();

    @Query("DELETE FROM localauthority")
    void deleteAll();
}
