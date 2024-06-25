package de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {TrackingData.class}, version = 1)
public abstract class TrackingDatabase extends RoomDatabase {
    private static TrackingDatabase instance;

    public abstract TrackingDataDao trackingDataDao();

    public static synchronized TrackingDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            TrackingDatabase.class, "biketracking_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
