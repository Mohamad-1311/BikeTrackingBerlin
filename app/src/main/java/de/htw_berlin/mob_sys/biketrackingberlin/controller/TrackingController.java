package de.htw_berlin.mob_sys.biketrackingberlin.controller;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import org.osmdroid.util.GeoPoint;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.htw_berlin.mob_sys.biketrackingberlin.DatabaseClient;
import de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_Views.TrackingActivity;
import de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_model.TrackingData;
import de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_model.TrackingDatabase;
import de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_model.TrackingModel;

public class TrackingController implements LocationListener {

    private static final float MIN_MOVEMENT_THRESHOLD = 2.0f;

    private TrackingActivity view;
    private TrackingModel model;
    private LocationManager locationManager;
    private TrackingDatabase db;
    private long startTime;
    private double totalDistance = 0.0;
    private List<GeoPoint> geoPoints;
    private GeoPoint lastKnownGeoPoint;

    private Handler handler = new Handler();
    private Runnable timerRunnable;
    private String startDate; // Hinzufügen des Datumsfeldes

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
        lastKnownGeoPoint = null;
        startDate = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(new Date());

        if (ContextCompat.checkSelfPermission(view, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, MIN_MOVEMENT_THRESHOLD, this);
        }

        // Start the timer
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                updateTrackingData();
                handler.postDelayed(this, 1000); // update every second
            }
        };
        handler.post(timerRunnable);
    }

    public void stopTracking() {
        locationManager.removeUpdates(this);
        handler.removeCallbacks(timerRunnable); // Stop the timer

        // Speichere die Tracking-Daten in die Datenbank
        new Thread(new Runnable() {
            @Override
            public void run() {
                TrackingData trackingData = new TrackingData();
                trackingData.totalDistance = model.getTotalDistance();
                trackingData.elapsedTimeInSeconds = model.getElapsedTimeInSeconds();
                trackingData.speed = model.getSpeed();
                trackingData.date = startDate;
                trackingData.geoPoints = encodePolyline(geoPoints);
                db.trackingDataDao().insert(trackingData);
            }
        }).start();
    }

    // Methode zum Encodieren der Polyline-Daten
    private String encodePolyline(List<GeoPoint> geoPoints) {
        StringBuilder encoded = new StringBuilder();
        long lastLat = 0, lastLng = 0;

        for (GeoPoint point : geoPoints) {
            long lat = Math.round(point.getLatitude() * 1e5);
            long lng = Math.round(point.getLongitude() * 1e5);

            long dLat = lat - lastLat;
            long dLng = lng - lastLng;

            lastLat = lat;
            lastLng = lng;

            encoded.append(encodeSignedNumber(dLat));
            encoded.append(encodeSignedNumber(dLng));
        }
        return encoded.toString();
    }

    private String encodeSignedNumber(long num) {
        long sgnNum = num << 1;
        if (num < 0) {
            sgnNum = ~(sgnNum);
        }
        return encodeNumber(sgnNum);
    }

    private String encodeNumber(long num) {
        StringBuilder encodeString = new StringBuilder();
        while (num >= 0x20) {
            encodeString.append((char)((0x20 | ((int)num & 0x1f)) + 63));
            num >>= 5;
        }
        encodeString.append((char)(num + 63));
        return encodeString.toString();
    }



    public void onStartTrackingClicked() {
        startTracking();
    }

    public void onStopTrackingClicked() {
        stopTracking();
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        GeoPoint currentGeoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());

        if (lastKnownGeoPoint != null) {
            float distance = distanceBetween(lastKnownGeoPoint, currentGeoPoint);

            if (distance >= MIN_MOVEMENT_THRESHOLD) {
                // Bewegungsschwelle überschritten, Position aktualisieren
                geoPoints.add(currentGeoPoint);
                lastKnownGeoPoint = currentGeoPoint;

                // Berechnungen für Strecke, Zeit usw. aktualisieren
                updateTrackingData();

                // Polyline aktualisieren
                view.updatePolyline(geoPoints);
            }
        } else {
            // Erste Position setzen
            geoPoints.add(currentGeoPoint);
            lastKnownGeoPoint = currentGeoPoint;

            // Berechnungen für Strecke, Zeit usw. aktualisieren
            updateTrackingData();

            // Polyline aktualisieren
            view.updatePolyline(geoPoints);
        }
    }

    public void savePolyline() {
        // Polyline in die Datenbank speichern
        new Thread(new Runnable() {
            @Override
            public void run() {
                TrackingData trackingData = new TrackingData();
                trackingData.totalDistance = model.getTotalDistance();
                trackingData.elapsedTimeInSeconds = model.getElapsedTimeInSeconds();
                trackingData.speed = model.getSpeed();
                trackingData.geoPoints = geoPointsToString(geoPoints); // GeoPoints als String speichern
                db.trackingDataDao().insert(trackingData);
            }
        }).start();
    }

    private String geoPointsToString(List<GeoPoint> geoPoints) {
        StringBuilder sb = new StringBuilder();
        for (GeoPoint point : geoPoints) {
            sb.append(point.getLatitude()).append(",").append(point.getLongitude()).append(";");
        }
        return sb.toString();
    }


    public void updateTrackingData() {
        // Berechne die Gesamtstrecke
        totalDistance = calculateTotalDistance();

        // Berechne die vergangene Zeit seit dem Start
        long elapsedTimeInSeconds = (System.currentTimeMillis() - startTime) / 1000;

        // Berechne die aktuelle Geschwindigkeit
        double speed = calculateSpeed(elapsedTimeInSeconds);

        // Setze die Werte im Modell
        model.setTotalDistance(totalDistance);
        model.setElapsedTimeInSeconds(elapsedTimeInSeconds);
        model.setSpeed(speed);

        // Aktualisiere die Benutzeroberfläche
        view.updateUI(formatDistance(totalDistance), elapsedTimeInSeconds, speed);
    }

    public double calculateTotalDistance() {
        double distance = 0.0;
        for (int i = 1; i < geoPoints.size(); i++) {
            GeoPoint startPoint = geoPoints.get(i - 1);
            GeoPoint endPoint = geoPoints.get(i);
            float[] results = new float[1];
            Location.distanceBetween(startPoint.getLatitude(), startPoint.getLongitude(),
                    endPoint.getLatitude(), endPoint.getLongitude(), results);
            distance += results[0];
        }
        return distance / 1000.0; // in Kilometern zurückgeben
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    private float distanceBetween(GeoPoint startPoint, GeoPoint endPoint) {
        float[] results = new float[1];
        Location.distanceBetween(startPoint.getLatitude(), startPoint.getLongitude(),
                endPoint.getLatitude(), endPoint.getLongitude(), results);
        return results[0];
    }

    private double calculateSpeed(long elapsedTimeInSeconds) {
        if (elapsedTimeInSeconds == 0) {
            return 0.0;
        }
        // Geschwindigkeit = Gesamtstrecke (in km) / Zeit (in Stunden)
        double speed = totalDistance / (elapsedTimeInSeconds / 3600.0);
        return speed;
    }

    private String formatDistance(double distance) {
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        return decimalFormat.format(distance);
    }

    public void setModel(TrackingModel model) {
        this.model = model;
    }

    public void setDatabase(TrackingDatabase db) {
        this.db = db;
    }

    public void setLocationManager(LocationManager locationManager) {
        this.locationManager = locationManager;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public GeoPoint getLastKnownGeoPoint() {
        return lastKnownGeoPoint;
    }

    public List<GeoPoint> getGeoPoints() {
        return geoPoints;
    }



    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(@NonNull String provider) {}

    @Override
    public void onProviderDisabled(@NonNull String provider) {}
}
