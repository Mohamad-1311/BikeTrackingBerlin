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
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osmdroid.util.GeoPoint;

import de.htw_berlin.mob_sys.biketrackingberlin.R;
import de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_Views.TrackingActivity;
import de.htw_berlin.mob_sys.biketrackingberlin.controller.TrackingController;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.core.StringContains.containsString;

import java.util.Locale;

@RunWith(AndroidJUnit4.class)
public class TrackingActivityTest {

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION);

    @Rule
    public ActivityScenarioRule<TrackingActivity> activityScenarioRule =
            new ActivityScenarioRule<>(TrackingActivity.class);

    private TrackingController controller;

    @Before
    public void setup() {
        Locale.setDefault(Locale.US);
        ActivityScenario<TrackingActivity> scenario = activityScenarioRule.getScenario();
        scenario.onActivity(activity -> {
            controller = activity.getController();
        });
    }

    private void simulateLocationUpdate(double latitude, double longitude, float accuracy, float speed) {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setAccuracy(accuracy);
        location.setSpeed(speed);
        location.setTime(System.currentTimeMillis());
        location.setElapsedRealtimeNanos(System.nanoTime());

        activityScenarioRule.getScenario().onActivity(activity -> {
            activity.getController().onLocationChanged(location);
        });
    }


    @Test
    public void testStartTrackingButton() {
        onView(withId(R.id.start_tracking)).perform(click());
        onView(withId(R.id.textview_distance)).check(matches(CustomMatchers.withTextIgnoringCommas("0.00 km")));
        onView(withId(R.id.textview_time)).check(matches(withText("00:00:00")));
        onView(withId(R.id.textview_speed)).check(matches(withText("0.00 km/h")));
    }

    @Test
    public void testStopTrackingButton() {
        onView(withId(R.id.start_tracking)).perform(click());
        onView(withId(R.id.stop_tracking)).perform(click());
        onView(withId(R.id.textview_distance)).check(matches(CustomMatchers.withTextIgnoringCommas("0.00 km")));
        onView(withId(R.id.textview_time)).check(matches(withText("00:00:00")));
        onView(withId(R.id.textview_speed)).check(matches(withText("0.00 km/h")));
    }

    @Test
    public void testUIUpdatesOnStartTracking() {
        onView(withId(R.id.start_tracking)).perform(click());
        onView(withId(R.id.textview_distance)).check(matches(CustomMatchers.withTextIgnoringCommas("0.00 km")));
        onView(withId(R.id.textview_time)).check(matches(withText("00:00:00")));
        onView(withId(R.id.textview_speed)).check(matches(withText("0.00 km/h")));
    }

    @Test
    public void testUIUpdatesOnStopTracking() {
        onView(withId(R.id.start_tracking)).perform(click());
        onView(withId(R.id.stop_tracking)).perform(click());
        onView(withId(R.id.textview_distance)).check(matches(CustomMatchers.withTextIgnoringCommas("0.00 km")));
        onView(withId(R.id.textview_time)).check(matches(withText("00:00:00")));
        onView(withId(R.id.textview_speed)).check(matches(withText("0.00 km/h")));
    }

    @Test
    public void testTrackingInProgress() throws InterruptedException {
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
        activityScenarioRule.getScenario().onActivity(activity -> {
            TrackingController controller = activity.getController();
            double totalDistanceKm = controller.getTotalDistance();

            // Überprüfe, ob die UI-Elemente aktualisiert wurden
            onView(withId(R.id.textview_distance)).check(matches(CustomMatchers.withTextIgnoringCommas(String.format(Locale.US, "%.2f km", totalDistanceKm))));
            onView(withId(R.id.textview_time)).check(matches(withText("00:00:05")));
        });
    }
    @Test
    public void testPolylineUpdate() {
        onView(withId(R.id.start_tracking)).perform(click());

        // Simuliere das Hinzufügen von GPS-Punkten zur Polyline
        // Hier könntest du später spezifische Tests für die Polyline hinzufügen
        // ...

        onView(withId(R.id.stop_tracking)).perform(click());
        // Überprüfen, ob die Polyline auf der Karte angezeigt wird
        // Dies ist ein Platzhaltertest, der angepasst werden kann, sobald die Polyline implementiert ist
    }

    @Test
    public void testPermissionsGranted() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        int fineLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarseLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
        assert(fineLocationPermission == PackageManager.PERMISSION_GRANTED);
        assert(coarseLocationPermission == PackageManager.PERMISSION_GRANTED);
    }
}
