package de.htw_berlin.mob_sys.biketrackingberlin.controller;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import de.htw_berlin.mob_sys.biketrackingberlin.DatabaseClient;
import de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_Views.TrackingActivity;
import de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_model.TrackingData;
import de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_model.TrackingDatabase;
import de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_model.TrackingModel;

public class TrackingController implements LocationListener {

    private TrackingActivity view;
    private TrackingModel model;
    private LocationManager locationManager;
    private TrackingDatabase db;
    private long startTime;
    private double totalDistance = 0.0;
    private List<GeoPoint> geoPoints;

    public TrackingController(TrackingActivity view) {
        this.view = view;
        this.model = new TrackingModel();
        locationManager = (LocationManager) view.getSystemService(Context.LOCATION_SERVICE);
        geoPoints = new ArrayList<>();
        // Initialisiere die Room-Datenbank über den DatabaseClient
        db = DatabaseClient.getInstance(view.getApplicationContext()).getTrackingDatabase();
    }

    public void startTracking() {
        startTime = System.currentTimeMillis();
        totalDistance = 0.0;
        geoPoints.clear();
        if (ContextCompat.checkSelfPermission(view, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 2, this);
        }
    }

    public void stopTracking() {
        locationManager.removeUpdates(this);

        // Speichere die Tracking-Daten in die Datenbank
        new Thread(new Runnable() {
            @Override
            public void run() {
                TrackingData trackingData = new TrackingData();
                trackingData.totalDistance = model.getTotalDistance();
                trackingData.elapsedTimeInSeconds = model.getElapsedTimeInSeconds();
                trackingData.speed = model.getSpeed();
                db.trackingDataDao().insert(trackingData);
            }
        }).start();
    }

    public void onStartTrackingClicked() {
        startTracking();
    }

    public void onStopTrackingClicked() {
        stopTracking();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        geoPoints.add(geoPoint);

        if (geoPoints.size() > 1) {
            Location prevLocation = new Location("");
            prevLocation.setLatitude(geoPoints.get(geoPoints.size() - 2).getLatitude());
            prevLocation.setLongitude(geoPoints.get(geoPoints.size() - 2).getLongitude());
            totalDistance += location.distanceTo(prevLocation);
        }

        long elapsedTimeInSeconds = (System.currentTimeMillis() - startTime) / 1000;

        model.setTotalDistance(totalDistance / 1000); // km
        model.setElapsedTimeInSeconds(elapsedTimeInSeconds);
        model.setSpeed(location.getSpeed() * 3.6); // km/h

        view.updateUI(model.getTotalDistance(), model.getElapsedTimeInSeconds(), model.getSpeed());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(@NonNull String provider) {}

    @Override
    public void onProviderDisabled(@NonNull String provider) {}
}
