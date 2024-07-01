package de.htw_berlin.mob_sys.biketrackingberlin;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;

import androidx.core.content.ContextCompat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.osmdroid.util.GeoPoint;

import de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_Views.TrackingActivity;
import de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_model.TrackingDatabase;
import de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_model.TrackingModel;
import de.htw_berlin.mob_sys.biketrackingberlin.controller.TrackingController;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TrackingControllerTest {

    @Mock
    TrackingActivity view;

    @Mock
    TrackingDatabase db;

    @Mock
    TrackingModel model;

    @Mock
    Context context;

    @Mock
    LocationManager locationManager;

    @Mock
    Handler handler;

    @InjectMocks
    TrackingController controller;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(view.getSystemService(Context.LOCATION_SERVICE)).thenReturn(locationManager);
        when(view.getApplicationContext()).thenReturn(context);
        when(context.getApplicationContext()).thenReturn(context); // Sicherstellen, dass der Kontext korrekt zurückgegeben wird

        controller.setDatabase(db);
        controller.setModel(model);
        controller.setHandler(handler);
        controller.setLocationManager(locationManager);
    }

    @Test
    public void testStartTracking() {
        when(ContextCompat.checkSelfPermission(view, android.Manifest.permission.ACCESS_FINE_LOCATION))
                .thenReturn(PackageManager.PERMISSION_GRANTED);

        controller.startTracking();

        verify(locationManager).requestLocationUpdates(eq(LocationManager.GPS_PROVIDER), eq(0L), eq(2.0f), any(LocationListener.class));
        verify(handler).post(any(Runnable.class));
    }

    @Test
    public void testStopTracking() {
        controller.stopTracking();

        verify(locationManager).removeUpdates(any(LocationListener.class));
        verify(handler).removeCallbacks(any(Runnable.class));
    }

    @Test
    public void testOnLocationChanged() {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(52.5200);
        location.setLongitude(13.4050);

        controller.onLocationChanged(location);

        assertNotNull(controller.getLastKnownGeoPoint());
    }

    @Test
    public void testUpdateTrackingData() {
        controller.getGeoPoints().add(new GeoPoint(52.5200, 13.4050));
        controller.getGeoPoints().add(new GeoPoint(52.5201, 13.4051));

        controller.updateTrackingData();

        verify(model).setTotalDistance(anyDouble());
        verify(model).setElapsedTimeInSeconds(anyLong());
        verify(model).setSpeed(anyDouble());
        verify(view).updateUI(anyString(), anyLong(), anyDouble());
    }
}
