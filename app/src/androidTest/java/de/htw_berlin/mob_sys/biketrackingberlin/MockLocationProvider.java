package de.htw_berlin.mob_sys.biketrackingberlin;

import android.location.Location;
import android.location.LocationManager;

public class MockLocationProvider {
    private String providerName;
    private LocationManager locationManager;

    public MockLocationProvider(String name, LocationManager locationManager) {
        this.providerName = name;
        this.locationManager = locationManager;
        locationManager.addTestProvider(providerName, false, false, false, false, true, true, true, 0, 5);
        locationManager.setTestProviderEnabled(providerName, true);
    }

    public void pushLocation(double lat, double lon) {
        Location mockLocation = new Location(providerName);
        mockLocation.setLatitude(lat);
        mockLocation.setLongitude(lon);
        mockLocation.setAltitude(0);
        mockLocation.setTime(System.currentTimeMillis());
        mockLocation.setAccuracy(1);
        mockLocation.setElapsedRealtimeNanos(System.nanoTime());
        locationManager.setTestProviderLocation(providerName, mockLocation);
    }

    public void shutdown() {
        locationManager.removeTestProvider(providerName);
    }
}
