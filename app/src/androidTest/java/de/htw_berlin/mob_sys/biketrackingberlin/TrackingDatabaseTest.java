package de.htw_berlin.mob_sys.biketrackingberlin;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_model.TrackingData;
import de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_model.TrackingDataDao;
import de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_model.TrackingDatabase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class TrackingDatabaseTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private TrackingDatabase db;
    private TrackingDataDao dao;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, TrackingDatabase.class)
                .allowMainThreadQueries()
                .build();
        dao = db.trackingDataDao();
    }

    @After
    public void tearDown() {
        db.close();
    }

    @Test
    public void testInsertAndGetAllTrackingData() {
        TrackingData trackingData = new TrackingData();
        trackingData.totalDistance = 10.5;
        trackingData.elapsedTimeInSeconds = 3600;
        trackingData.speed = 15.5;

        dao.insert(trackingData);

        List<TrackingData> allTrackingData = dao.getAllTrackingData();
        assertEquals(1, allTrackingData.size());
        assertEquals(10.5, allTrackingData.get(0).totalDistance, 0.01);
        assertEquals(3600, allTrackingData.get(0).elapsedTimeInSeconds);
        assertEquals(15.5, allTrackingData.get(0).speed, 0.01);
    }

    @Test
    public void testDeleteTrackingData() {
        TrackingData trackingData = new TrackingData();
        trackingData.totalDistance = 10.5;
        trackingData.elapsedTimeInSeconds = 3600;
        trackingData.speed = 15.5;

        dao.insert(trackingData);
        List<TrackingData> allTrackingData = dao.getAllTrackingData();
        assertEquals(1, allTrackingData.size());

        // Überprüfen, ob das Objekt korrekt gespeichert wurde
        TrackingData dataInDb = allTrackingData.get(0);
        assertEquals(trackingData.totalDistance, dataInDb.totalDistance, 0.01);
        assertEquals(trackingData.elapsedTimeInSeconds, dataInDb.elapsedTimeInSeconds);
        assertEquals(trackingData.speed, dataInDb.speed, 0.01);

        // Objekt löschen
        dao.delete(dataInDb);

        // Datenbankinhalt ausgeben und überprüfen
        allTrackingData = dao.getAllTrackingData();
        if (!allTrackingData.isEmpty()) {
            for (TrackingData data : allTrackingData) {
                System.out.println("Remaining entry: " + data.totalDistance + ", " + data.elapsedTimeInSeconds + ", " + data.speed);
            }
        }
        assertTrue("Database should be empty after deletion", allTrackingData.isEmpty());
    }
}
