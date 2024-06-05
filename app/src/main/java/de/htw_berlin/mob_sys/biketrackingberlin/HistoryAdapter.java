package de.htw_berlin.mob_sys.biketrackingberlin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.htw_berlin.mob_sys.biketrackingberlin.tracking_data.Fahrdaten;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private ArrayList<Fahrdaten> fahrdatenList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public HistoryAdapter(ArrayList<Fahrdaten> fahrdatenList) {
        this.fahrdatenList = fahrdatenList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Fahrdaten fahrdaten = fahrdatenList.get(position);
        holder.bind(fahrdaten);
    }

    @Override
    public int getItemCount() {
        return fahrdatenList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView textViewFahrtID;
        private TextView textViewDatum;
        private TextView textViewStrecke;
        private TextView textViewSpeed; // TextView für die Geschwindigkeit hinzugefügt

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewFahrtID = itemView.findViewById(R.id.textView_fahrtID);
            textViewDatum = itemView.findViewById(R.id.textView_datum);
            textViewStrecke = itemView.findViewById(R.id.textView_strecke);
            textViewSpeed = itemView.findViewById(R.id.textView_speed); // TextView-Referenz für die Geschwindigkeit initialisiert

            itemView.setOnClickListener(this);
        }

        public void bind(Fahrdaten fahrdaten) {
            textViewFahrtID.setText(String.valueOf(fahrdaten.getFahrtID()));
            textViewDatum.setText(fahrdaten.getDatum());
            textViewStrecke.setText(fahrdaten.getStrecke());
            textViewSpeed.setText(String.valueOf(fahrdaten.getGeschwindigkeit()));
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position);
                }
            }
        }
    }
}
