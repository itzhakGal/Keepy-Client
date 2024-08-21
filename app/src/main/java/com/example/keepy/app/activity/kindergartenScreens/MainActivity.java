package com.example.keepy.app.activity.kindergartenScreens;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.keepy.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private String kindergartenName;
    private String currentUserPhoneNumber;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                    selectedFragment = new HomeFragment();
                } else if (itemId == R.id.navigation_sound) {
                    selectedFragment = new SoundFragment();
                } else if (itemId == R.id.navigation_details) {
                    selectedFragment = new DetailsFragment();
                } else if (itemId == R.id.navigation_settings) {
                    selectedFragment = new CameraFragment();
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
            Fragment defaultFragment = new HomeFragment();
            Bundle bundle = new Bundle();
            bundle.putString("currentUserPhoneNumber", currentUserPhoneNumber);
            bundle.putString("kindergartenName", kindergartenName);
            defaultFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    defaultFragment).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_settings) {
            Fragment settingsFragment = new SettingsFragment();
            Bundle bundle = new Bundle();
            bundle.putString("currentUserPhoneNumber", currentUserPhoneNumber);
            bundle.putString("kindergartenName", kindergartenName);
            settingsFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, settingsFragment)
                    .commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
