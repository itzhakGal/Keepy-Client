package com.example.keepy.app.adapter;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keepy.R;
import com.example.keepy.app.domain.Event;

import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {

    private List<Event> eventsList;
    private int[] backgroundColors;

    public EventsAdapter(List<Event> eventsList) {
        this.eventsList = eventsList;

        // Define background colors for each item
        backgroundColors = new int[]{
                R.color.pale_sunshine,
                R.color.gentle_lavender,
                R.color.mint_green,
                R.color.peachy_pink
        };
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

        // Set the background color dynamically
        int colorIndex = position % backgroundColors.length;
        int backgroundColor = ContextCompat.getColor(holder.itemView.getContext(), backgroundColors[colorIndex]);

        // Apply the background with rounded corners
        GradientDrawable backgroundDrawable = (GradientDrawable) ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.rounded_background_eventdetails);
        backgroundDrawable.setColor(backgroundColor);
        holder.itemView.setBackground(backgroundDrawable);

        // Set the appropriate icon based on the event type
        if (event.getEventType() != null) {
            switch (event.getEventType()) {
                case "curse word detected":
                    holder.ivEventIcon.setImageResource(R.drawable.ic_curse); // Set icon for curse word
                    break;
                case "inappropriate sentence detected":
                    holder.ivEventIcon.setImageResource(R.drawable.ic_inappropriate_sentence); // Set icon for inappropriate sentence
                    break;
                case "crying detected":
                    holder.ivEventIcon.setImageResource(R.drawable.ic_cry); // Set icon for crying detected
                    break;
            }
        }

        // Set the event description, time, and additional details
        String eventType = event.getEventType();
        String capitalizedEventType = eventType.substring(0, 1).toUpperCase() + eventType.substring(1);
        holder.tvEventDescription.setText(capitalizedEventType);
        holder.tvEventDateTime.setText(event.getDateTime());

        if (event.getDescription() != null && !event.getDescription().isEmpty()) {
            // Capitalize the first letter of the description
            String description = event.getDescription();
            String capitalizedDescription = description.substring(0, 1).toUpperCase() + description.substring(1);

            holder.tvWordOrSentence.setText("Details: " + capitalizedDescription);
        }else {
            holder.tvWordOrSentence.setText("Details: Baby is crying");
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
