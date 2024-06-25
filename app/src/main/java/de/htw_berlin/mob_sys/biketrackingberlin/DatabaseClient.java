package de.htw_berlin.mob_sys.biketrackingberlin;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_model.TrackingDatabase;

public class DatabaseClient {
    private Context mContext;
    private static DatabaseClient mInstance;

    // Room Database Object
    private TrackingDatabase trackingDatabase;

    private DatabaseClient(Context mContext) {
        this.mContext = mContext;

        // Erstellen der Room-Datenbank mit dem Namen "biketracking_database"
        trackingDatabase = Room.databaseBuilder(mContext.getApplicationContext(), TrackingDatabase.class, "biketracking_database")
                .build();

    }

    public static synchronized DatabaseClient getInstance(Context mContext) {
        if (mInstance == null) {
            mInstance = new DatabaseClient(mContext);
        }
        return mInstance;
    }

    public TrackingDatabase getTrackingDatabase() {
        return trackingDatabase;
    }
}
