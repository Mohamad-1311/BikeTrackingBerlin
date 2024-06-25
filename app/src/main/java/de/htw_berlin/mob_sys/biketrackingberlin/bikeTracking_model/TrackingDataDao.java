package de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TrackingDataDao {
    @Insert
    void insert(TrackingData trackingData);

    @Query("SELECT * FROM tracking_data")
    List<TrackingData> getAllTrackingData();
}
