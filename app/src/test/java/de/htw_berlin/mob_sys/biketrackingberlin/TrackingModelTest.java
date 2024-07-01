package de.htw_berlin.mob_sys.biketrackingberlin;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_model.TrackingModel;

public class TrackingModelTest {

    private TrackingModel model;

    @Before
    public void setUp() {
        model = new TrackingModel();
    }

    @Test
    public void testTotalDistance() {
        model.setTotalDistance(12.34);
        assertEquals(12.34, model.getTotalDistance(), 0.01);
    }

    @Test
    public void testElapsedTimeInSeconds() {
        model.setElapsedTimeInSeconds(1234);
        assertEquals(1234, model.getElapsedTimeInSeconds());
    }

    @Test
    public void testSpeed() {
        model.setSpeed(23.45);
        assertEquals(23.45, model.getSpeed(), 0.01);
    }
}
