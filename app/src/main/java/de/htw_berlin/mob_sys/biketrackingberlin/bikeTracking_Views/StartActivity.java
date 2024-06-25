package de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_Views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import de.htw_berlin.mob_sys.biketrackingberlin.R;


public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Button trackingButton = findViewById(R.id.show_tracking);
        trackingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Erstelle einen Intent, um zur TrackingActivity zu wechseln
                Intent intent = new Intent(StartActivity.this, TrackingActivity.class);
                startActivity(intent); // Starte die TrackingActivity
            }
        });

        Button historyButton = findViewById(R.id.show_history);
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Erstelle einen Intent, um zur HistoryActivity zu wechseln
                Intent intent = new Intent(StartActivity.this, HistoryActivity.class);
                startActivity(intent); // Starte die HistoryActivity
            }
        });
    }
}
