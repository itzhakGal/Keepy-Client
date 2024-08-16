package com.example.keepy.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keepy.R;
import com.example.keepy.app.domain.Event;

import java.util.List;


public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {

    private List<Event> eventsList;

    public EventsAdapter(List<Event> eventsList) {
        this.eventsList = eventsList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventsList.get(position);

        switch (event.getEventType()) {
            case "curse word detected":
                holder.ivEventIcon.setImageResource(R.drawable.ic_curse); // Replace with your curse icon
                break;
            case "inappropriate sentence detected":
                holder.ivEventIcon.setImageResource(R.drawable.ic_inappropriate_sentence); // Replace with your inappropriate icon
                break;
            case "crying detected":
                holder.ivEventIcon.setImageResource(R.drawable.ic_cry); // Replace with your cry icon
                break;
        }

        holder.tvEventDescription.setText(event.getEventType());
        holder.tvEventDateTime.setText(event.getDateTime());

        // Set the word or sentence if available
        if (event.getDescription() != null) {
            holder.tvWordOrSentence.setVisibility(View.VISIBLE);
            holder.tvWordOrSentence.setText("Details: " + event.getDescription());
        } else {
            holder.tvWordOrSentence.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView ivEventIcon;
        TextView tvEventDescription, tvEventDateTime, tvWordOrSentence;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            ivEventIcon = itemView.findViewById(R.id.ivEventIcon);
            tvEventDescription = itemView.findViewById(R.id.tvEventDescription);
            tvEventDateTime = itemView.findViewById(R.id.tvEventDateTime);
            tvWordOrSentence = itemView.findViewById(R.id.tvWordOrSentence);
        }
    }
}

