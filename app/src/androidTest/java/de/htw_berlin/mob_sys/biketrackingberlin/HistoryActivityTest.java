package de.htw_berlin.mob_sys.biketrackingberlin;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_Views.FahrdatenDetailActivity;
import de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_Views.HistoryActivity;
import de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_model.TrackingData;
import de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_model.TrackingDatabase;
import de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_model.TrackingDataDao;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;

import android.view.View;
import android.util.Log;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class HistoryActivityTest {

    private static final String TAG = "HistoryActivityTest";

    @Rule
    public ActivityScenarioRule<HistoryActivity> activityScenarioRule =
            new ActivityScenarioRule<>(HistoryActivity.class);

    @Before
    public void setup() {
        Log.d(TAG, "Setup: Checking existing database data");

        // Initialisiere die Datenbank
        TrackingDatabase db = TrackingDatabase.getInstance(InstrumentationRegistry.getInstrumentation().getTargetContext());
        TrackingDataDao trackingDataDao = db.trackingDataDao();

        // Überprüfe, ob bereits Daten vorhanden sind
        if (trackingDataDao.getAllTrackingData().isEmpty()) {
            // Falls keine Daten vorhanden sind, füge Testdaten hinzu
            Log.d(TAG, "No data found in database. Inserting test data.");
            TrackingData ride1 = new TrackingData();
            ride1.totalDistance = 10.0;
            ride1.elapsedTimeInSeconds = 1800;
            ride1.speed = 20.0;
            trackingDataDao.insert(ride1);

            TrackingData ride2 = new TrackingData();
            ride2.totalDistance = 15.0;
            ride2.elapsedTimeInSeconds = 2700;
            ride2.speed = 20.0;
            trackingDataDao.insert(ride2);
        } else {
            Log.d(TAG, "Data found in database. Using existing data.");
        }

        ActivityScenario.launch(HistoryActivity.class);
    }

    @Test
    public void testDisplayRideList() {
        Log.d(TAG, "Test: Checking if recycler view is displayed");
        onView(withId(R.id.recycler_view_history))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testNavigateToDetailActivity() {
        Log.d(TAG, "Test: Checking if recycler view is displayed and has items");

        // Check if RecyclerView is displayed
        onView(withId(R.id.recycler_view_history))
                .check(matches(isDisplayed()));

        // Wait to ensure data is loaded
        try {
            Thread.sleep(2000); // 2 Sekunden warten
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check if RecyclerView has items
        onView(withId(R.id.recycler_view_history))
                .check(new RecyclerViewItemCountAssertion(1));

        // Click on the first item if available
        onView(withId(R.id.recycler_view_history))
                .perform(actionOnItemAtPosition(0, click()));

        Log.d(TAG, "Test: Checking if detail view is displayed");
        Espresso.onView(withId(R.id.textView_fahrtID_detail))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testSwipeToDelete() {
        Log.d(TAG, "Test: Swiping to delete the first item in the recycler view");

        // Check if RecyclerView is displayed
        onView(withId(R.id.recycler_view_history))
                .check(matches(isDisplayed()));

        // Perform swipe to delete on the first item
        onView(withId(R.id.recycler_view_history))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, swipeLeft()));

        // Wait to ensure data is updated
        try {
            Thread.sleep(2000); // 2 Sekunden warten
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check if the first item is deleted by checking item count
        onView(withId(R.id.recycler_view_history))
                .check(new RecyclerViewItemCountAssertion(1));

        // Check if the database is updated correctly
        TrackingDatabase db = TrackingDatabase.getInstance(InstrumentationRegistry.getInstrumentation().getTargetContext());
        TrackingDataDao trackingDataDao = db.trackingDataDao();
        int itemCount = trackingDataDao.getAllTrackingData().size();
        assertTrue("Database should have 1 item left", itemCount == 1);
    }

    // Custom RecyclerView assertion to check item count
    public class RecyclerViewItemCountAssertion implements ViewAssertion {
        private final int minExpectedCount;

        public RecyclerViewItemCountAssertion(int minExpectedCount) {
            this.minExpectedCount = minExpectedCount;
        }

        @Override
        public void check(View view, NoMatchingViewException noViewFoundException) {
            if (noViewFoundException != null) {
                throw noViewFoundException;
            }

            RecyclerView recyclerView = (RecyclerView) view;
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            assertNotNull("RecyclerView Adapter is null", adapter);
            int itemCount = adapter.getItemCount();
            Log.d(TAG, "RecyclerView item count: " + itemCount);
            assertTrue("RecyclerView should have at least " + minExpectedCount + " items", itemCount >= minExpectedCount);
        }
    }
}
