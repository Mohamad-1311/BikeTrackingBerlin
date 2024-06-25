package de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_Views;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import de.htw_berlin.mob_sys.biketrackingberlin.R;

public class FahrdatenDetailActivity extends AppCompatActivity {

    private TextView textViewFahrtID;
    private TextView textViewDatum;
    private TextView textViewStrecke;
    private TextView textViewGeschwindigkeit;

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

        // Daten aus Intent erhalten
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int fahrtID = extras.getInt("FAHRT_ID");
            String datum = extras.getString("DATUM");
            String strecke = extras.getString("STRECKE");
            String geschwindigkeit = extras.getString("GESCHWINDIGKEIT");

            textViewFahrtID.setText(String.valueOf(fahrtID));
            textViewDatum.setText(datum);
            textViewStrecke.setText(strecke);
            textViewGeschwindigkeit.setText(geschwindigkeit);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            // Beenden Sie die Aktivität und kehren Sie zur vorherigen Aktivität zurück
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
