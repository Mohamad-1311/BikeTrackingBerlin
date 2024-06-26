package de.htw_berlin.mob_sys.biketrackingberlin.controller;

import android.content.Context;

import java.util.List;

import de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_Views.HistoryActivity;
import de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_Views.HistoryAdapter;
import de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_model.TrackingData;
import de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_model.TrackingDatabase;

public class HistoryController {

    private Context context;
    private TrackingDatabase db;
    private HistoryAdapter historyAdapter;

    public HistoryController(Context context, HistoryAdapter historyAdapter) {
        this.context = context;
        this.historyAdapter = historyAdapter;
        this.db = TrackingDatabase.getInstance(context);
    }

    public void loadTrackingData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<TrackingData> trackingDataList = db.trackingDataDao().getAllTrackingData();
                ((HistoryActivity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        historyAdapter.updateList(trackingDataList);
                    }
                });
            }
        }).start();
    }

    public void deleteTrackingData(TrackingData trackingData) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.trackingDataDao().delete(trackingData);
            }
        }).start();
    }
}
