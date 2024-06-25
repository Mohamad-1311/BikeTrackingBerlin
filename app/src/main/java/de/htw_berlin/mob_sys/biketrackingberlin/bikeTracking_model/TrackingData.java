package de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tracking_data")
public class TrackingData {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public double totalDistance;
    public long elapsedTimeInSeconds;
    public double speed;


}
