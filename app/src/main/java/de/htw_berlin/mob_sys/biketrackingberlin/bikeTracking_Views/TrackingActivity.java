package de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_Views;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.widget.Toolbar;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

import de.htw_berlin.mob_sys.biketrackingberlin.R;
import de.htw_berlin.mob_sys.biketrackingberlin.controller.TrackingController;
import de.htw_berlin.mob_sys.biketrackingberlin.databinding.ActivityTrackingBinding;

public class TrackingActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    private MapView map;
    private MyLocationNewOverlay mLocationOverlay;
    private CompassOverlay mCompassOverlay;
    private ScaleBarOverlay mScaleBarOverlay;
    private Button startTrackingButton, stopTrackingButton;
    private TextView distanceTextView, timeTextView, speedTextView;
    private ActivityTrackingBinding binding;
    private TrackingController controller;
    private Polyline polyline;
    private List<GeoPoint> geoPoints;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTrackingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        controller = new TrackingController(this);

        // Osmdroid Konfiguration
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        map = binding.map;
        map.setTileSource(TileSourceFactory.MAPNIK);

        // Zoom- und Positionseinstellungen
        map.setMultiTouchControls(true);
        map.getController().setZoom(15.0);
        map.getController().setCenter(new GeoPoint(52.52, 13.40)); // Beispiel: Berlin

        // MyLocationNewOverlay initialisieren
        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(ctx), map);
        mLocationOverlay.enableMyLocation();
        mLocationOverlay.enableFollowLocation();
        map.getOverlays().add(mLocationOverlay);

        // CompassOverlay hinzufügen
        mCompassOverlay = new CompassOverlay(ctx, map);
        mCompassOverlay.enableCompass();
        map.getOverlays().add(mCompassOverlay);

        // ScaleBarOverlay hinzufügen
        mScaleBarOverlay = new ScaleBarOverlay(map);
        mScaleBarOverlay.setAlignBottom(true);
        map.getOverlays().add(mScaleBarOverlay);

        // RotationGestureOverlay hinzufügen
        RotationGestureOverlay mRotationGestureOverlay = new RotationGestureOverlay(map);
        mRotationGestureOverlay.setEnabled(true);
        map.setMultiTouchControls(true);
        map.getOverlays().add(mRotationGestureOverlay);

        // Toolbar und Zurückpfeil aktivieren
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialisiere die Polyline und GeoPoints
        polyline = new Polyline();
        geoPoints = new ArrayList<>();
        polyline.setPoints(geoPoints);
        map.getOverlayManager().add(polyline);

        // Initialisiere den LocationManager
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Initialisiere TextViews und Buttons
        distanceTextView = binding.textviewDistance;
        timeTextView = binding.textviewTime;
        speedTextView = binding.textviewSpeed;
        startTrackingButton = binding.startTracking;
        stopTrackingButton = binding.stopTracking;

        updateUI("0.00", 0, 0.0);

        // Button-Click-Listener
        startTrackingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onStartTrackingClicked();
                showToast("Tracking gestartet");
            }
        });

        stopTrackingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.onStopTrackingClicked();
                showToast("Tracking gestoppt");
                updateUI("0.00", 0, 0.0);
            }
        });

        requestPermissionsIfNecessary(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        List<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSIONS_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Berechtigung wurde erteilt, starte die Standortanzeige
                    mLocationOverlay.enableMyLocation();
                    mLocationOverlay.enableFollowLocation();
                } else {
                    showToast("Berechtigung wurde verweigert");
                }
                return;
            }
        }
    }

    public void updateUI(String distance, long timeInSeconds, double speed) {
        distanceTextView.setText(getString(R.string.textview_distance, distance));
        timeTextView.setText(getString(R.string.textview_time, formatTime(timeInSeconds)));
        speedTextView.setText(getString(R.string.textview_speed, String.format("%.2f", speed)));
    }

    private String formatTime(long timeInSeconds) {
        long hours = timeInSeconds / 3600;
        long minutes = (timeInSeconds % 3600) / 60;
        long seconds = timeInSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
