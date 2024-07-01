package de.htw_berlin.mob_sys.biketrackingberlin;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;


import java.util.ArrayList;
import java.util.List;

import de.htw_berlin.mob_sys.biketrackingberlin.R;
import de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_Views.FahrdatenDetailActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class FahrdatenDetailActivityTest {

    @Test
    public void testMapViewAndPolylineDisplay() {
        // Mock Intent erstellen
        Intent intent = new Intent();
        intent.putExtra("FAHRT_ID", 1);
        intent.putExtra("DATUM", "2024-06-29");
        intent.putExtra("STRECKE", "10.5 km");
        intent.putExtra("GESCHWINDIGKEIT", "25.0 km/h");
        intent.putExtra("DAUER", 3600L); // 1 Stunde in Sekunden
        intent.putExtra("POLYLINE", "encoded_polyline_string_here");

        // ActivityScenario für FahrdatenDetailActivity starten
        ActivityScenario<FahrdatenDetailActivity> scenario = ActivityScenario.launch(intent);

        // Prüfen, ob die MapView angezeigt wird
        onView(withId(R.id.map_view)).check(matches(isDisplayed()));

        // Prüfen, ob die Polyline korrekt gesetzt ist
        onView(withId(R.id.map_view)).check((view, noViewFoundException) -> {
            if (!(view instanceof MapView)) {
                throw noViewFoundException;
            }
            MapView mapView = (MapView) view;

            // Prüfen, ob die Karte TileSourceFactory.MAPNIK verwendet
            assert mapView.getTileProvider().getTileSource() == TileSourceFactory.MAPNIK;

            // Polyline(s) aus der MapView abrufen
            List<Polyline> polylines = new ArrayList<>();
            for (org.osmdroid.views.overlay.Overlay overlay : mapView.getOverlays()) {
                if (overlay instanceof Polyline) {
                    polylines.add((Polyline) overlay);
                }
            }

            assert polylines.size() > 0; // Stellen Sie sicher, dass mindestens eine Polyline vorhanden ist

            // Weitere Validierungen der Polyline nach Bedarf hier einfügen
        });

        // Szenario beenden
        scenario.close();
    }
}
