package de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_Views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import de.htw_berlin.mob_sys.biketrackingberlin.R;
import de.htw_berlin.mob_sys.biketrackingberlin.bikeTracking_model.TrackingData;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<TrackingData> trackingDataList;
    private OnItemClickListener onItemClickListener;

    public HistoryAdapter() {
        this.trackingDataList = new ArrayList<>();
    }

    public void updateList(List<TrackingData> newList) {
        trackingDataList.clear();
        trackingDataList.addAll(newList);
        notifyDataSetChanged();
    }

    public TrackingData getItem(int position) {
        return trackingDataList.get(position);
    }

    public void removeItem(int position) {
        trackingDataList.remove(position);
        notifyItemRemoved(position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TrackingData trackingData = trackingDataList.get(position);
        holder.bind(trackingData);
    }

    @Override
    public int getItemCount() {
        return trackingDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView textViewFahrtID;
        private TextView textViewDatum;
        private TextView textViewStrecke;
        private TextView textViewSpeed;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewFahrtID = itemView.findViewById(R.id.textView_fahrtID);
            textViewDatum = itemView.findViewById(R.id.textView_datum);
            textViewStrecke = itemView.findViewById(R.id.textView_strecke);
            textViewSpeed = itemView.findViewById(R.id.textView_speed); // Falls benötigt
            itemView.setOnClickListener(this);
        }

        public void bind(TrackingData trackingData) {
            textViewFahrtID.setText(String.valueOf(trackingData.id));
            textViewStrecke.setText(formatDistance(trackingData.totalDistance)); // Hier anpassen
            textViewSpeed.setText(formatSpeed(trackingData.speed)); // Hier anpassen
        }

        private String formatDistance(double distance) {
            DecimalFormat decimalFormat = new DecimalFormat("#0.00");
            return decimalFormat.format(distance) + " km";
        }

        private String formatSpeed(double speed) {
            DecimalFormat decimalFormat = new DecimalFormat("#0.0");
            return decimalFormat.format(speed) + " km/h";
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && onItemClickListener != null) {
                onItemClickListener.onItemClick(trackingDataList.get(position));
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(TrackingData trackingData);
    }
}
