package de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_model;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {TrackingData.class}, version = 1)
public abstract class TrackingDatabase extends RoomDatabase {
    public abstract TrackingDataDao trackingDataDao();
}
