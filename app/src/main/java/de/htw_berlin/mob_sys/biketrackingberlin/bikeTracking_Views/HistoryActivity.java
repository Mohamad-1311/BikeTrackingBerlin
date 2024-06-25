package de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_Views;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
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

        // Swipe zum Löschen mit rotem Hintergrund
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            private Drawable background;
            private boolean initiated;

            private void init() {
                background = ContextCompat.getDrawable(getApplicationContext(), R.drawable.drawable);
                initiated = true;
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                TrackingData deletedItem = historyAdapter.getItem(position);
                historyAdapter.removeItem(position);
                deleteTrackingData(deletedItem);
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;
                if (!initiated) {
                    init();
                }
                // Zeichnet den roten Hintergrund
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
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

    private void deleteTrackingData(TrackingData trackingData) {
        // Datenbankzugriff in einem separaten Thread ausführen
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.trackingDataDao().delete(trackingData);
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
