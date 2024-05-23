package com.example.keepy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomePageActivity extends AppCompatActivity {
    TextView textAddKindergarten;
    String currentUserPhoneNumber;
    TextView kindergartenTextView;
    ListView kindergartenListView;
    Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        currentUserPhoneNumber = intent.getStringExtra("currentUserPhoneNumber");
        textAddKindergarten = findViewById(R.id.textAddKindergarten);
        kindergartenTextView = findViewById(R.id.textViewKindergartenHome);
        kindergartenListView = findViewById(R.id.listViewKindergartens);
        logoutButton = findViewById(R.id.logoutButton);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users")
                .child(currentUserPhoneNumber).child("MyKindergartens");

        // Initially hide both TextView and ListView
        kindergartenTextView.setVisibility(View.GONE);
        kindergartenListView.setVisibility(View.GONE);

        // Set an event listener to fetch the kindergarten names from the database
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> kindergartenNames = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String kindergartenName = snapshot.child("kindergartenName").getValue(String.class);
                    kindergartenNames.add(kindergartenName);
                }

                if (kindergartenNames.isEmpty()) {
                    kindergartenTextView.setVisibility(View.VISIBLE);
                } else {
                    // Populate ListView with kindergarten names
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(HomePageActivity.this,
                            android.R.layout.simple_list_item_1, kindergartenNames);
                    kindergartenListView.setAdapter(adapter);
                    kindergartenListView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        });



    // Set click listener for the ListView items
        kindergartenListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedKindergarten = (String) parent.getItemAtPosition(position);
                // Handle item click, you can navigate to a new activity or perform any action here
            }
        });

        textAddKindergarten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, AddKindergartenActivity.class);
                intent.putExtra("currentUserPhoneNumber", currentUserPhoneNumber);
                startActivity(intent);
            }
        });

        // Set click listener for the ListView items
        kindergartenListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedKindergarten = (String) parent.getItemAtPosition(position);

                // Start MainActivity and pass selected kindergarten name and user phone number
                Intent intent = new Intent(HomePageActivity.this, UserMainActivity.class);
                intent.putExtra("kindergartenName", selectedKindergarten);
                intent.putExtra("currentUserPhoneNumber", currentUserPhoneNumber);
                startActivity(intent);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("remember", "false");
                editor.apply();
                Intent intent = new Intent(HomePageActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }


    public String getCurrentUserPhoneNumber() {
        return currentUserPhoneNumber;
    }

    public void setCurrentUserPhoneNumber(String currentUserPhoneNumber) {
        this.currentUserPhoneNumber = currentUserPhoneNumber;
    }

}