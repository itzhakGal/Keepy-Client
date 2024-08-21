package com.example.keepy.app.activity.kindergartenScreens;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.keepy.R;
import com.example.keepy.app.adapter.EventsAdapter;
import com.example.keepy.app.domain.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private static final String ARG_PHONE_NUMBER = "currentUserPhoneNumber";
    private static final String ARG_KIND_NAME = "kindergartenName";

    private String currentUserPhoneNumber;
    private String kindergartenName;

    private RecyclerView recyclerView;
    private EventsAdapter eventsAdapter;
    private List<Event> eventsList;

    private Spinner spinnerEventType;

    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance(String currentUserPhoneNumber, String kindergartenName) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PHONE_NUMBER, currentUserPhoneNumber);
        args.putString(ARG_KIND_NAME, kindergartenName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentUserPhoneNumber = getArguments().getString(ARG_PHONE_NUMBER);
            kindergartenName = getArguments().getString(ARG_KIND_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize event list and adapter
        eventsList = new ArrayList<>();
        eventsAdapter = new EventsAdapter(eventsList);
        recyclerView.setAdapter(eventsAdapter);

        // Initialize Spinner
        spinnerEventType = view.findViewById(R.id.spinnerEventType);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.event_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEventType.setAdapter(adapter);

        // Fetch all events initially
        fetchEventsFromFirebase("All Events");

        // Set listener for Spinner selection
        spinnerEventType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Filter events only if a specific type is selected
                String selectedEventType = spinnerEventType.getSelectedItem().toString();
                if (!selectedEventType.equals("All Events")) {
                    fetchEventsFromFirebase(selectedEventType);
                } else {
                    // If "All Events" is selected, fetch all events again
                    fetchEventsFromFirebase("All Events");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        return view;
    }

    private void fetchEventsFromFirebase(String eventTypeFilter) {
        DatabaseReference eventsRef = FirebaseDatabase.getInstance("https://keepyapp-e4d50-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("kindergartens")
                .child(kindergartenName)
                .child("events");

        eventsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventsList.clear();
                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    String eventType = eventSnapshot.child("event").getValue(String.class);
                    String description = eventSnapshot.child("sentence").exists()
                            ? eventSnapshot.child("sentence").getValue(String.class)
                            : eventSnapshot.child("word").getValue(String.class);
                    String dateTime = eventSnapshot.child("timestamp").getValue(String.class);
                    String id = eventSnapshot.child("id").getValue(String.class);

                    // If the filter is "All Events" or matches the selected event type, add the event to the list
                    if (eventTypeFilter.equals("All Events") || eventTypeFilter.equals(eventType)) {
                        Event event = new Event(eventType, description, dateTime, id);
                        eventsList.add(event);
                    }
                }
                eventsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }
}
