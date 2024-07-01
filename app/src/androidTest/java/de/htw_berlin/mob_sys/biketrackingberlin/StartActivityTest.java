package de.htw_berlin.mob_sys.biketrackingberlin;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_Views.HistoryActivity;
import de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_Views.StartActivity;
import de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_Views.TrackingActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class StartActivityTest {

    @Before
    public void setup() {
        Intents.init();
        ActivityScenario<StartActivity> scenario = ActivityScenario.launch(StartActivity.class);
        assertNotNull("Activity should be launched successfully", scenario);
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testNavigationToTrackingActivity() {
        onView(withId(R.id.show_tracking)).perform(click());
        Intents.intended(IntentMatchers.hasComponent(TrackingActivity.class.getName()));
    }

    @Test
    public void testNavigationToHistoryActivity() {
        onView(withId(R.id.show_history)).perform(click());
        Intents.intended(IntentMatchers.hasComponent(HistoryActivity.class.getName()));
    }
}
