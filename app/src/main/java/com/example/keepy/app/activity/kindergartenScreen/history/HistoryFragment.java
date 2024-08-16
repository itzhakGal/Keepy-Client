package com.example.keepy.app.activity.kindergartenScreen.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keepy.databinding.FragmentHistoryBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;
    private RecyclerView recyclerView;
    private AlertsAdapter alertsAdapter;
    private List<Alert> alertList;
    private String kindergartenName;
    private  String currentUserPhoneNumber;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        if (getArguments() != null) {
            kindergartenName = getArguments().getString("kindergartenName");
            currentUserPhoneNumber = getArguments().getString("currentUserPhoneNumber");
        }

        // Initialize RecyclerView and its adapter
        recyclerView = binding.recyclerViewAlerts;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        alertList = new ArrayList<>();
        alertsAdapter = new AlertsAdapter(alertList);
        recyclerView.setAdapter(alertsAdapter);

        // Fetch data from Firebase
        fetchAlertHistory();

        return root;
    }


    private void fetchAlertHistory() {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://keepyapp-e4d50-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference ref = database.getReference("kindergartens").child(kindergartenName).child("events");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                alertList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Alert alert = snapshot.getValue(Alert.class);
                    alertList.add(alert);
                }
                alertsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle error
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
