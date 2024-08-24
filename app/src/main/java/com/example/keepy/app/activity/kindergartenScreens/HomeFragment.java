package com.example.keepy.app.activity.kindergartenScreens;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.keepy.R;
import com.example.keepy.app.activity.homePageScreen.AddKindergartenActivity;
import com.example.keepy.app.activity.homePageScreen.HomePageActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeFragment extends Fragment {

    private String currentUserPhoneNumber;
    private String kindergartenName;
    private DatabaseReference databaseReference;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String currentUserPhoneNumber, String kindergartenName) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString("currentUserPhoneNumber", currentUserPhoneNumber);
        args.putString("kindergartenName", kindergartenName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentUserPhoneNumber = getArguments().getString("currentUserPhoneNumber");
            kindergartenName = getArguments().getString("kindergartenName");
        }

        // Initialize Firebase reference
        databaseReference = FirebaseDatabase.getInstance("https://keepyapp-e4d50-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users")
                .child(currentUserPhoneNumber);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize UI components
        TextView tvUsername = view.findViewById(R.id.username);
        TextView tvKindergartenName = view.findViewById(R.id.tvKindergartenNameHomeScreen);
        ImageView ivLiveSound = view.findViewById(R.id.ivLiveSound);
        ImageView ivHistory = view.findViewById(R.id.ivHistory);
        ImageView ivChildren = view.findViewById(R.id.ivChildren);
        ImageView ivCameraPic = view.findViewById(R.id.ivCameraPic);
       // ImageView ivFlower = view.findViewById(R.id.ivFlower);
        CardView tvGoToAllKindergartenScreen = view.findViewById(R.id.tvGoToAllKindergartenScreen);

        // Set the dynamic values for the TextViews
        String welcomeMessage = getString(R.string.welcome_kindergarten, kindergartenName);
        tvKindergartenName.setText(welcomeMessage);

        // Fetch the full name from Firebase
        fetchFullName(tvUsername);

        // Set click listeners to open the corresponding fragments
        ivLiveSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundFragment soundFragment = SoundFragment.newInstance(currentUserPhoneNumber, kindergartenName);
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, soundFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        ivHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HistoryFragment historyFragment = HistoryFragment.newInstance(currentUserPhoneNumber, kindergartenName);
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, historyFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        ivChildren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailsFragment detailsFragment = DetailsFragment.newInstance(currentUserPhoneNumber, kindergartenName);
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, detailsFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        ivCameraPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraFragment cameraFragment = CameraFragment.newInstance(currentUserPhoneNumber, kindergartenName);
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, cameraFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        tvGoToAllKindergartenScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HomePageActivity.class);
                intent.putExtra("currentUserPhoneNumber", currentUserPhoneNumber);
                startActivity(intent);
            }
        });


        return view;
    }

    private void fetchFullName(final TextView tvUsername) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String fullName = dataSnapshot.child("fullName").getValue(String.class);
                    tvUsername.setText(fullName + ","); // Set the full name in the TextView
                } else {
                    tvUsername.setText("User not found"); // Handle case where user is not found
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
                tvUsername.setText("Error loading user");
            }
        });
    }
}
