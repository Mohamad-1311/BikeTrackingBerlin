package de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_Views;

import static android.app.PendingIntent.getActivity;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

import de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_model.Fahrdaten;
import de.htw_berlin.mob_sys.biketrackingberlin.R;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;
    private ArrayList<Fahrdaten> fahrdatenList;
    private ColorDrawable background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Dummy-Fahrdaten erstellen (hier sollte die Datenbankabfrage erfolgen)
        fahrdatenList = createDummyData();

        // RecyclerView initialisieren und konfigurieren
        recyclerView = findViewById(R.id.recycler_view_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        historyAdapter = new HistoryAdapter(fahrdatenList);
        recyclerView.setAdapter(historyAdapter);

        // ItemTouchHelper für Swipe-to-Dismiss hinzufügen
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                fahrdatenList.remove(position);
                historyAdapter.notifyItemRemoved(position);
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                View itemView = viewHolder.itemView;
                int backgroundCornerOffset = 20;
                if (dX > 0) { // Swiping to the right
                    background.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + ((int) dX) + backgroundCornerOffset, itemView.getBottom());
                } else if (dX < 0) { // Swiping to the left
                    background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                } else { // View is unSwiped
                    background.setBounds(0, 0, 0, 0);
                }
                background.draw(c);
            }
        };
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        background = new ColorDrawable(Color.RED);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // In the onCreate method of HistoryActivity
        historyAdapter.setOnItemClickListener(new HistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Fahrdaten selectedFahrdaten = fahrdatenList.get(position);

                Intent intent = new Intent(HistoryActivity.this, FahrdatenDetailActivity.class);
                intent.putExtra("FAHRT_ID", selectedFahrdaten.getFahrtID());
                intent.putExtra("DATUM", selectedFahrdaten.getDatum());
                intent.putExtra("STRECKE", selectedFahrdaten.getStrecke());
                intent.putExtra("GESCHWINDIGKEIT", selectedFahrdaten.getGeschwindigkeit());
                startActivity(intent);
            }
        });


    }

    // Dummy-Fahrdaten erstellen (zum Testen)
    private ArrayList<Fahrdaten> createDummyData() {
        ArrayList<Fahrdaten> data = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            Fahrdaten fahrdaten = new Fahrdaten();
            fahrdaten.setFahrtID(i);
            fahrdaten.setDatum("Datum " + i);
            fahrdaten.setStrecke("Strecke " + i);
            fahrdaten.setGeschwindigkeit("Geschwindigkeit " + 5 * i + " m/h");
            data.add(fahrdaten);
        }
        return data;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Zum vorherigen Bildschirm zurückkehren, wenn die Start-Schaltfläche der Toolbar gedrückt wird
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
