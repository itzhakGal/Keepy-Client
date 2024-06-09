package com.example.keepy.app.activity.homePageScreen;

import static android.content.ContentValues.TAG;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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

import com.example.keepy.ApiService;
import com.example.keepy.R;
import com.example.keepy.app.TokenRequest;
import com.example.keepy.app.activity.kindergartenScreen.MainActivity;
import com.example.keepy.app.activity.registerScreen.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
        FirebaseApp.initializeApp(this);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://keppy-5ed11.firebaseio.com/").getReference("users")
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

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        Log.d(TAG, "FCM Token: " + token);

                        // Send token to your server
                        sendTokenToServer(token);
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
                Intent intent = new Intent(HomePageActivity.this, MainActivity.class);
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

        createNotificationChannel();


    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = getString(R.string.default_notification_channel_id);
            String channelName = "Default Channel";
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private void sendTokenToServer(String token) {
        Retrofit retrofit = getRetrofitInstance();
        ApiService apiService = retrofit.create(ApiService.class);

        if (currentUserPhoneNumber != null && token != null) {
            TokenRequest tokenRequest = new TokenRequest(currentUserPhoneNumber, token);

            apiService.sendToken(tokenRequest).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Token sent successfully");
                    } else {
                        Log.e(TAG, "Error sending token: " + response.message());
                        try {
                            Log.e(TAG, "Error body: " + response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    t.printStackTrace();
                    Log.e(TAG, "Error sending token", t);
                }
            });
        } else {
            Log.e(TAG, "currentUserPhoneNumber or token is null. Cannot send token to server.");
        }
    }


    private Retrofit getRetrofitInstance() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        return new Retrofit.Builder()
                .baseUrl("http://192.168.1.32:8080")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public String getCurrentUserPhoneNumber() {
        return currentUserPhoneNumber;
    }

    public void setCurrentUserPhoneNumber(String currentUserPhoneNumber) {
        this.currentUserPhoneNumber = currentUserPhoneNumber;
    }

}
