package de.htw_berlin.mob_sys.biketrackingberlin.controller;

import android.os.Handler;
import android.os.Looper;

import java.util.List;

import de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_model.TrackingData;
import de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_model.TrackingDatabase;
import de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_Views.HistoryAdapter;

public class HistoryController {

    private TrackingDatabase db;
    private HistoryAdapter historyAdapter;

    public HistoryController(TrackingDatabase db, HistoryAdapter historyAdapter) {
        this.db = db;
        this.historyAdapter = historyAdapter;
    }

    public void loadTrackingData() {
        // Datenbankzugriff in einem separaten Thread ausführen
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Tracking-Daten aus der Datenbank abrufen
                List<TrackingData> trackingDataList = db.trackingDataDao().getAllTrackingData();

                // Auf dem UI-Thread aktualisieren
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        historyAdapter.notifyDataSetChanged(); // Adapter informieren, dass sich die Daten geändert haben
                    }
                });
            }
        }).start();
    }
}
