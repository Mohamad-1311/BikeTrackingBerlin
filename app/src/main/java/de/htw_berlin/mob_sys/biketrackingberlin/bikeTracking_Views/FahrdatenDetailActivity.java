package de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_Views;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;

import java.util.ArrayList;
import java.util.List;

import de.htw_berlin.mob_sys.biketrackingberlin.R;

public class FahrdatenDetailActivity extends AppCompatActivity {

    private TextView textViewFahrtID, textViewDatum, textViewStrecke, textViewGeschwindigkeit, textViewDauer;
    private MapView map;
    private CompassOverlay mCompassOverlay;
    private ScaleBarOverlay mScaleBarOverlay;
    private Polyline polyline;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fahrdaten_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textViewFahrtID = findViewById(R.id.textView_fahrtID_detail);
        textViewDatum = findViewById(R.id.textView_datum_detail);
        textViewStrecke = findViewById(R.id.textView_strecke_detail);
        textViewGeschwindigkeit = findViewById(R.id.textView_geschwindigkeit_detail);
        textViewDauer = findViewById(R.id.textView_dauer_detail);
        map = findViewById(R.id.map_view);

        // Osmdroid Konfiguration
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.getController().setZoom(15.0);

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

        // Polyline initialisieren
        polyline = new Polyline();
        map.getOverlayManager().add(polyline);

        // Daten aus Intent erhalten
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int fahrtID = extras.getInt("FAHRT_ID");
            String datum = extras.getString("DATUM");
            String strecke = extras.getString("STRECKE");
            String geschwindigkeit = extras.getString("GESCHWINDIGKEIT");
            long dauer = extras.getLong("DAUER");
            String polylineEncoded = extras.getString("POLYLINE");

            textViewFahrtID.setText(String.valueOf(fahrtID));
            textViewDatum.setText(datum);
            textViewStrecke.setText(strecke);
            textViewGeschwindigkeit.setText(geschwindigkeit);
            textViewDauer.setText(String.valueOf(dauer) + " s");

            // Polyline zeichnen
            if (polylineEncoded != null && !polylineEncoded.isEmpty()) {
                List<GeoPoint> geoPoints = decodePolyline(polylineEncoded);
                polyline.setPoints(geoPoints);
                if (!geoPoints.isEmpty()) {
                    map.getController().setCenter(geoPoints.get(0));
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private List<GeoPoint> decodePolyline(String encoded) {
        List<GeoPoint> geoPoints = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            GeoPoint p = new GeoPoint(lat / 1E5, lng / 1E5);
            geoPoints.add(p);
        }

        return geoPoints;
    }
}
