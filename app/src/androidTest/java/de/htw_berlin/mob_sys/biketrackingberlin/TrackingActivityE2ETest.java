package de.htw_berlin.mob_sys.biketrackingberlin;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;

import androidx.core.content.ContextCompat;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.intent.rule.IntentsTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osmdroid.util.GeoPoint;

import java.util.List;
import java.util.Locale;

import de.htw_berlin.mob_sys.biketrackingberlin.R;
import de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_Views.TrackingActivity;
import de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_Views.HistoryActivity;
import de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_Views.FahrdatenDetailActivity;
import de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_model.TrackingData;
import de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_model.TrackingDatabase;
import de.htw_berlin.mob_sys.biketrackingberlin.controller.TrackingController;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class TrackingActivityE2ETest {

    @Rule
    public ActivityScenarioRule<TrackingActivity> activityScenarioRule =
            new ActivityScenarioRule<>(TrackingActivity.class);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION);

    private Context context;
    private TrackingDatabase db;
    private LocationManager locationManager;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        db = TrackingDatabase.getInstance(context);
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Intents.init();
    }

    @After
    public void tearDown() {
        db.close();
        Intents.release();
    }

    private void simulateLocationUpdate(double latitude, double longitude, float accuracy, float speed) {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setAccuracy(accuracy);
        location.setSpeed(speed);
        location.setTime(System.currentTimeMillis());
        location.setElapsedRealtimeNanos(System.nanoTime());
        locationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, location);
    }

    @Test
    public void testFullScenario() throws InterruptedException {
        // Start tracking
        onView(withId(R.id.start_tracking)).perform(click());

        // Simuliere Bewegung vom Schloss Charlottenburg zur HTW Berlin Wilhelminenhof
        double[][] points = {
                {52.5208, 13.2950}, // Schloss Charlottenburg
                {52.5200, 13.4000}, // Zwischenpunkt 1
                {52.5150, 13.4500}, // Zwischenpunkt 2
                {52.5050, 13.5000}, // Zwischenpunkt 3
                {52.4900, 13.5200}, // Zwischenpunkt 4
                {52.4850, 13.5250}  // HTW Berlin Wilhelminenhof
        };

        // Simuliere die Bewegung mit der Geschwindigkeit von 50 km/h (13.89 m/s)
        for (double[] point : points) {
            simulateLocationUpdate(point[0], point[1], 1, 13.89f); // 50 km/h = 13.89 m/s
            Thread.sleep(1000); // Warte eine Sekunde zwischen den Aktualisierungen
        }

        // Berechne die erwartete Strecke in Kilometern
        double expectedDistanceKm = calculateDistance(points) / 1000.0;

        // Überprüfe, ob die UI-Elemente aktualisiert wurden
        onView(withId(R.id.textview_distance)).check(matches(CustomMatchers.withTextIgnoringCommas(String.format(Locale.US, "%.2f km", expectedDistanceKm))));
        onView(withId(R.id.textview_time)).check(matches(withText("00:00:05")));

        // Stop tracking
        onView(withId(R.id.stop_tracking)).perform(click());

        // Warte kurz, damit die Daten in die Datenbank geschrieben werden
        Thread.sleep(2000);

        // Wechsel zur History-Activity
        onView(withId(R.id.show_history)).perform(click());

        // Überprüfen, ob die History-Activity gestartet wurde
        Intents.intended(hasComponent(HistoryActivity.class.getName()));

        // Lade die Daten aus der Datenbank und überprüfe sie
        List<TrackingData> allTrackingData = db.trackingDataDao().getAllTrackingData();
        assertEquals(1, allTrackingData.size());

        TrackingData savedData = allTrackingData.get(0);
        assertEquals(expectedDistanceKm, savedData.totalDistance, 0.01);
        assertEquals(5, savedData.elapsedTimeInSeconds);
        assertEquals(18.0, savedData.speed, 0.01);

        // Klicke auf den ersten Eintrag in der Historie
        onView(withId(R.id.recycler_view_history))
                .perform(actionOnItemAtPosition(0, click()));

        // Überprüfen, ob die Detail-Activity gestartet wurde
        Intents.intended(hasComponent(FahrdatenDetailActivity.class.getName()));

        // Überprüfen, ob die Detail-Activity die richtigen Daten anzeigt
        onView(withId(R.id.textview_distance)).check(matches(CustomMatchers.withTextIgnoringCommas(String.format(Locale.US, "%.2f km", expectedDistanceKm))));
        onView(withId(R.id.textview_time)).check(matches(withText("00:00:05")));
        onView(withId(R.id.textview_speed)).check(matches(withText("18.00 km/h")));
    }

    private double calculateDistance(double[][] points) {
        double totalDistance = 0.0;
        for (int i = 1; i < points.length; i++) {
            float[] results = new float[1];
            Location.distanceBetween(points[i - 1][0], points[i - 1][1], points[i][0], points[i][1], results);
            totalDistance += results[0];
        }
        return totalDistance;
    }
}
