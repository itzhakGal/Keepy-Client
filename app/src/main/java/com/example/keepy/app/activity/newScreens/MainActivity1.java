package com.example.keepy.app.activity.newScreens;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.keepy.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity1 extends AppCompatActivity {

    private String kindergartenName;
    private String currentUserPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);

        // Retrieve the data passed from the previous activity
        Intent intent = getIntent();
        currentUserPhoneNumber = intent.getStringExtra("currentUserPhoneNumber");
        kindergartenName = intent.getStringExtra("kindergartenName");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                Bundle bundle = new Bundle();
                bundle.putString("currentUserPhoneNumber", currentUserPhoneNumber);
                bundle.putString("kindergartenName", kindergartenName);

                int itemId = item.getItemId();

                if (itemId == R.id.navigation_home) {
                    selectedFragment = new HomeFragment1();
                } else if (itemId == R.id.navigation_sound) {
                    selectedFragment = new SoundFragment();
                } else if (itemId == R.id.navigation_details) {
                    selectedFragment = new DetailsFragment1();
                } else if (itemId == R.id.navigation_settings) {
                    selectedFragment = new CameraFragment1();
                }

                // Pass the bundle to the fragment
                if (selectedFragment != null) {
                    selectedFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                }

                return true;
            }
        });

        // Set default fragment
        if (savedInstanceState == null) {
            Fragment defaultFragment = new HomeFragment1();
            Bundle bundle = new Bundle();
            bundle.putString("currentUserPhoneNumber", currentUserPhoneNumber);
            bundle.putString("kindergartenName", kindergartenName);
            defaultFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    defaultFragment).commit();
        }
    }
}
