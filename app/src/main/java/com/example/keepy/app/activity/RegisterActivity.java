package com.example.keepy.app.activity;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.keepy.R;
import com.example.keepy.app.activity.homePageScreen.HomePageActivity;
import com.example.keepy.app.domain.UserDetailsHelperClass;
import com.example.keepy.app.network.ApiService;
import com.example.keepy.app.network.TokenRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {
    EditText fullNameET, phoneNumberET;
    Button registerBtn;
    CheckBox rememberMeCB;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String currentUserPhoneNumber;

    @SuppressLint({"CutPasteId", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Log.d(TAG, "onCreate: Initializing Firebase");
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        fullNameET = findViewById(R.id.fullName);
        phoneNumberET = findViewById(R.id.registerPhoneNumber);
        registerBtn = findViewById(R.id.registerButton);
        rememberMeCB = findViewById(R.id.rememberMe);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Log.d(TAG, "onCreate: Checking notification permission");
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = fullNameET.getText().toString();
                String phoneNumber = phoneNumberET.getText().toString();

                Log.d(TAG, "onClick: Validation check for fullName and phoneNumber");
                boolean check = validationInfo(fullName, phoneNumber);
                if (check) {
                    Log.d(TAG, "onClick: Signing in anonymously");
                    mAuth.signInAnonymously().addOnCompleteListener(RegisterActivity.this, task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onClick: Anonymous sign-in successful");
                            saveUserToRealtimeDatabase(phoneNumber, fullName, phoneNumber);
                            currentUserPhoneNumber = phoneNumber;  // Set the currentUserPhoneNumber
                            sendToken();
                        } else {
                            Log.e(TAG, "onClick: Authentication failed", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Log.d(TAG, "onClick: Validation failed");
                    Toast.makeText(getApplicationContext(), "Sorry, check information again", Toast.LENGTH_SHORT).show();
                }
            }
        });

        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        String checkbox = preferences.getString("remember", "");
        Log.d(TAG, "onCreate: Checking remember me status: " + checkbox);
        if (checkbox.equals("true")) {
            currentUserPhoneNumber = preferences.getString("phoneNumber", "");
            Log.d(TAG, "onCreate: Remembered phone number: " + currentUserPhoneNumber);
            Intent intent = new Intent(RegisterActivity.this, HomePageActivity.class);
            intent.putExtra("currentUserPhoneNumber", currentUserPhoneNumber);
            startActivity(intent);
        } else if (checkbox.equals("false")) {
            Log.d(TAG, "onCreate: User needs to sign in");
            Toast.makeText(this, "Please Sign in", Toast.LENGTH_SHORT).show();
        }

        rememberMeCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rememberMeCB.isChecked()) {
                    Log.d(TAG, "onClick: Remember me checked");
                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "true");
                    editor.putString("phoneNumber", phoneNumberET.getText().toString());
                    editor.apply();
                } else {
                    Log.d(TAG, "onClick: Remember me unchecked");
                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "false");
                    editor.apply();
                }
            }
        });
    }

    private void sendToken() {
        Log.d(TAG, "sendToken: Fetching FCM token");
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "sendToken: Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        String token = task.getResult();
                        Log.d(TAG, "sendToken: FCM Token received: " + token);

                        sendTokenToServer(token);
                    }
                });
    }

    private void saveUserToRealtimeDatabase(String userId, String fullName, String phoneNumber) {
        Log.d(TAG, "saveUserToRealtimeDatabase: Saving user to Realtime Database");
        database = FirebaseDatabase.getInstance("https://keepyapp-e4d50-default-rtdb.europe-west1.firebasedatabase.app/");
        reference = database.getReference("users");

        UserDetailsHelperClass userDetailsHelperClass = new UserDetailsHelperClass(fullName, phoneNumber);
        reference.child(userId).setValue(userDetailsHelperClass, (databaseError, databaseReference) -> {
            if (databaseError != null) {
                Log.e(TAG, "saveUserToRealtimeDatabase: Error saving user", databaseError.toException());
                Toast.makeText(getApplicationContext(), "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "saveUserToRealtimeDatabase: User saved successfully");
                Toast.makeText(getApplicationContext(), "Data is valid", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, HomePageActivity.class);
                intent.putExtra("currentUserPhoneNumber", phoneNumber);
                startActivity(intent);
            }
        });
    }

    private boolean isAlphabeticalString(String str) {
        Log.d(TAG, "isAlphabeticalString: Checking if string is alphabetical: " + str);
        if (str.matches("[a-zA-Z\u0590-\u05FF\\s]+")) {
            return true;
        } else {
            fullNameET.requestFocus();
            fullNameET.setError("Enter only alphabetical characters ");
            return false;
        }
    }

    private boolean isFieldEmpty(String field, EditText editText) {
        Log.d(TAG, "isFieldEmpty: Checking if field is empty");
        if (field.isEmpty()) {
            editText.requestFocus();
            editText.setError("Field cannot be empty");
            return true;
        }
        return false;
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        Log.d(TAG, "isValidPhoneNumber: Checking if phone number is valid: " + phoneNumber);
        String regex = "^[0-9]{10}$"; // Assumes a 10-digit phone number
        return phoneNumber.matches(regex);
    }

    private boolean validationInfo(String fullName, String phoneNumber) {
        Log.d(TAG, "validationInfo: Validating user info");
        if (isFieldEmpty(fullName, fullNameET) || !isAlphabeticalString(fullName)) {
            return false;
        }

        if (isFieldEmpty(phoneNumber, phoneNumberET) || !isValidPhoneNumber(phoneNumber)) {
            phoneNumberET.requestFocus();
            phoneNumberET.setError("Enter a valid phone number");
            return false;
        }

        return true;
    }

    private void sendTokenToServer(String token) {
        Log.d(TAG, "sendTokenToServer: Sending token to server");
        Retrofit retrofit = getRetrofitInstance();
        ApiService apiService = retrofit.create(ApiService.class);

        if (currentUserPhoneNumber != null && token != null) {
            TokenRequest tokenRequest = new TokenRequest(currentUserPhoneNumber, token);

            apiService.sendToken(tokenRequest).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "sendTokenToServer: Token sent successfully");
                    } else {
                        Log.e(TAG, "sendTokenToServer: Error sending token: " + response.message());
                        try {
                            Log.e(TAG, "sendTokenToServer: Error body: " + response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e(TAG, "sendTokenToServer: Failure sending token", t);
                }
            });
        } else {
            Log.e(TAG, "sendTokenToServer: currentUserPhoneNumber or token is null. Cannot send token to server.");
        }
    }

    private Retrofit getRetrofitInstance() {
        Log.d(TAG, "getRetrofitInstance: Creating Retrofit instance");

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
}
