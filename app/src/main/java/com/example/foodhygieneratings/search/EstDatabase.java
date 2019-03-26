package com.example.foodhygieneratings.search;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Establishment.class}, version = 2)
public abstract class EstDatabase extends RoomDatabase {
        public abstract EstablishmentDao establishmentDao();

}
