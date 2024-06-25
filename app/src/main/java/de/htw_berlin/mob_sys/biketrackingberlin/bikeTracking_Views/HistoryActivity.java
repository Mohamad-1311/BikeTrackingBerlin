package de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_Views;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.htw_berlin.mob_sys.biketrackingberlin.R;
import de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_model.TrackingData;
import de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_model.TrackingDatabase;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;
    private TrackingDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Initialisierung der Toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialisierung der RecyclerView und des Adapters
        recyclerView = findViewById(R.id.recycler_view_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        historyAdapter = new HistoryAdapter();
        recyclerView.setAdapter(historyAdapter);

        // Initialisierung der Room-Datenbank
        db = TrackingDatabase.getInstance(getApplicationContext());

        // Laden der Tracking-Daten aus der Datenbank und Aktualisieren des Adapters
        loadTrackingData();
    }

    private void loadTrackingData() {
        // Datenbankzugriff in einem separaten Thread ausführen
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Tracking-Daten aus der Datenbank abrufen
                List<TrackingData> trackingDataList = db.trackingDataDao().getAllTrackingData();

                // Auf dem UI-Thread aktualisieren
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        historyAdapter.updateList(trackingDataList);
                    }
                });
            }
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
