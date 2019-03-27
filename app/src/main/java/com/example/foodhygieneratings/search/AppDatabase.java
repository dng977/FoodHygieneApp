package com.example.foodhygieneratings.search;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Establishment.class, LocalAuthority.class, BusinessType.class}, version =8)
public abstract class AppDatabase extends RoomDatabase {
        public abstract EstablishmentDao establishmentDao();
        public abstract BusinessTypeDao businessTypeDao();
        public abstract LocalAuthorityDao localAuthorityDao();

}
