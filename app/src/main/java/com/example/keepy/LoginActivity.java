package com.example.keepy;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity {

    EditText phoneNumberET, passwordET;
    Button loginBtn;
    TextView textViewSignUp;

    Button buttonNotification;

    @SuppressLint({"MissingInflatedId", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        phoneNumberET = findViewById(R.id.loginPhoneNumber);
        passwordET = findViewById(R.id.loginPassword);
        loginBtn = findViewById(R.id.loginButton);
        textViewSignUp = findViewById(R.id.textViewSignUp);

        textViewSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        buttonNotification = findViewById(R.id.buttonNotification);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(LoginActivity.this,
                    android.Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(LoginActivity.this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
        buttonNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeNotification();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = phoneNumberET.getText().toString();
                String password = passwordET.getText().toString();

                boolean check = validationInfo(phoneNumber, password);
                if (check) {
                    checkUser();
                } else {
                    Toast.makeText(getApplicationContext(), "Sorry check information again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void checkUser() {
        String userPhoneNumber = phoneNumberET.getText().toString().trim();
        String userPassword = passwordET.getText().toString().trim();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("phoneNumber").equalTo(userPhoneNumber);
        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    phoneNumberET.setError(null);
                    String passwordFromDB = snapshot.child(userPhoneNumber).child("password").getValue(String.class);
                    if (passwordFromDB.equals(userPassword)) {
                        passwordET.setError(null);
                        String nameFromDB = snapshot.child(userPhoneNumber).child("full name").getValue(String.class);
                        String gardenNameDB = snapshot.child(userPhoneNumber).child("name of garden").getValue(String.class);
                        String emailFromDB = snapshot.child(userPhoneNumber).child("phoneNumber").getValue(String.class);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        Toast.makeText(getApplicationContext(), "Data is valid", Toast.LENGTH_SHORT).show();
                        intent.putExtra("full name", nameFromDB);
                        intent.putExtra("name of garden", gardenNameDB);
                        intent.putExtra("phoneNumber", emailFromDB);
                        intent.putExtra("password", passwordFromDB);
                        startActivity(intent);
                    } else {
                        passwordET.setError("Invalid Credentials");
                        passwordET.requestFocus();
                    }
                } else {
                    phoneNumberET.setError("User does not exist");
                    phoneNumberET.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    public void makeNotification() {
        String chanelID = "CHANEL_ID_NOTIFICATION";
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext(), chanelID);
        builder.setSmallIcon(R.drawable.ic_notification);
        builder.setContentTitle("Notification Title");
        builder.setContentText("Some text for notification here")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("data", "some value to be passed here");

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0, intent, PendingIntent.FLAG_MUTABLE);
        builder.setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationchannel =
                    notificationManager.getNotificationChannel(chanelID);
            if (notificationchannel == null) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                notificationchannel = new NotificationChannel(chanelID,
                        "some description", importance);
                notificationchannel.setLightColor(Color.GREEN);
                notificationchannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationchannel);
            }
        }

        notificationManager.notify(0, builder.build());

    }

    private boolean validationInfo(String phoneNumber, String password) {

        if (isFieldEmpty(phoneNumber, phoneNumberET) && isValidPhoneNumber(phoneNumber)) {
            return false;
        } else return isPasswordValid(password, passwordET);
    }

    private boolean isFieldEmpty(String field, EditText editText) {
        if (field.isEmpty()) {
            editText.requestFocus();
            editText.setError("FIELD CANNOT BE EMPTY");
            return true;
        }
        return false;
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        String regex = "^[0-9]{10}$"; // Assumes a 10-digit phone number
        return phoneNumber.matches(regex);
    }

    private boolean isPasswordValid(String password, EditText editText) {
        if (password.length() < 5) {
            editText.requestFocus();
            editText.setError("MINIMUM 5 CHARACTER REQUIRED");
            return false;
        }
        return true;
    }

}