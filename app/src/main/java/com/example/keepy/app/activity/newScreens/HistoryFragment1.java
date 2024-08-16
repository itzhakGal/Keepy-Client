package com.example.keepy.app.activity.newScreens;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class HistoryFragment1 extends Fragment {

    private static final String ARG_PHONE_NUMBER = "currentUserPhoneNumber";
    private static final String ARG_KIND_NAME = "kindergartenName";

    private String currentUserPhoneNumber;
    private String kindergartenName;

    private RecyclerView recyclerView;
    private EventsAdapter eventsAdapter;
    private List<Event> eventsList;

    public HistoryFragment1() {
        // Required empty public constructor
    }

    public static HistoryFragment1 newInstance(String currentUserPhoneNumber, String kindergartenName) {
        HistoryFragment1 fragment = new HistoryFragment1();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history1, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize event list and adapter
        eventsList = new ArrayList<>();
        eventsAdapter = new EventsAdapter(eventsList);
        recyclerView.setAdapter(eventsAdapter);

        // Fetch events from Firebase
        fetchEventsFromFirebase();

        return view;
    }

    private void fetchEventsFromFirebase() {
        DatabaseReference eventsRef = FirebaseDatabase.getInstance("https://keepyapp-e4d50-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("kindergartens")
                .child(kindergartenName)
                .child("events");

        eventsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventsList.clear(); // Clear the list before adding new data
                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    String eventType = eventSnapshot.child("event").getValue(String.class);
                    String description = eventSnapshot.child("sentence").exists()
                            ? eventSnapshot.child("sentence").getValue(String.class)
                            : eventSnapshot.child("word").getValue(String.class);
                    String dateTime = eventSnapshot.child("timestamp").getValue(String.class);
                    String id = eventSnapshot.child("id").getValue(String.class);

                    Event event = new Event(eventType, description, dateTime, id);
                    eventsList.add(event);
                }
                eventsAdapter.notifyDataSetChanged(); // Notify adapter of data changes
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }
}
