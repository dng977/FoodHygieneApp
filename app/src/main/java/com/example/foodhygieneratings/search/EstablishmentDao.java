package com.example.foodhygieneratings.search;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface EstablishmentDao {
    @Insert
    void insertEstablishment(Establishment establishment);

    @Query("SELECT * FROM establishment")
    List<Establishment> retrieveAll();

    @Query("SELECT * FROM establishment WHERE ID=:id LIMIT 1")
    Establishment retrieveEstablishmentByID(String id);

    @Query("DELETE FROM establishment WHERE ID=:id")
    void deleteEstablishmentByID(String id);
}
