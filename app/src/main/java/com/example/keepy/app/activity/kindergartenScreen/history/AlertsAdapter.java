package com.example.keepy.app.activity.kindergartenScreen.history;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keepy.R;

import java.util.List;

public class AlertsAdapter extends RecyclerView.Adapter<AlertsAdapter.ViewHolder> {

    private List<Alert> alertList;

    public AlertsAdapter(List<Alert> alertList) {
        this.alertList = alertList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alert_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Alert alert = alertList.get(position);
        holder.tvEvent.setText(holder.tvEvent.getText() + alert.getEvent());
        holder.tvTimestamp.setText(holder.tvTimestamp.getText() + alert.getTimestamp());
        holder.tvDetectedWord.setText(holder.tvDetectedWord.getText() + alert.getWord());
    }

    @Override
    public int getItemCount() {
        return alertList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvEvent, tvTimestamp, tvDetectedWord;

        public ViewHolder(View itemView) {
            super(itemView);
            tvEvent = itemView.findViewById(R.id.tvEvent);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvDetectedWord = itemView.findViewById(R.id.tvDetectedWord);
        }
    }
}
