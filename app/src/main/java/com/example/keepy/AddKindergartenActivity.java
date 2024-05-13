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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


public class AddKindergartenActivity extends AppCompatActivity {

    EditText kindergartenNameET, kindergartenPasswordET;
    Button addKindergartenButton;
    TextView textGoToHomePage;

    Button buttonNotification;

    @SuppressLint({"MissingInflatedId", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_kindergarten);

        kindergartenNameET = findViewById(R.id.kindergartenName);
        kindergartenPasswordET = findViewById(R.id.Password);
        addKindergartenButton = findViewById(R.id.addKindergartenButton);
        textGoToHomePage = findViewById(R.id.textGoToHomePage);

        textGoToHomePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddKindergartenActivity.this, HomePageActivity.class));
            }
        });

        buttonNotification = findViewById(R.id.buttonNotification);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(AddKindergartenActivity.this,
                    android.Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(AddKindergartenActivity.this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
        buttonNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeNotification();
            }
        });

        addKindergartenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = kindergartenNameET.getText().toString();
                String password = kindergartenPasswordET.getText().toString();

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
        String kindergartenName = kindergartenNameET.getText().toString().trim();
        String userPassword = kindergartenPasswordET.getText().toString().trim();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("phoneNumber").equalTo(kindergartenName);
         /*checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    kindergartenNameET.setError(null);
                    String passwordFromDB = snapshot.child(kindergartenName).child("password").getValue(String.class);
                    if (passwordFromDB.equals(userPassword)) {
                        kindergartenPasswordET.setError(null);
                        String kindergartenNameDB = snapshot.child(kindergartenName).child("name of kindergarten").getValue(String.class);
                        Intent intent = new Intent(AddKindergartenActivity.this, MainActivity.class);
                        Toast.makeText(getApplicationContext(), "Data is valid", Toast.LENGTH_SHORT).show();
                        intent.putExtra("kindergartenName", kindergartenNameDB);
                        intent.putExtra("password", passwordFromDB);
                        startActivity(intent);
                    } else {
                        kindergartenPasswordET.setError("Invalid Credentials");
                        kindergartenPasswordET.requestFocus();
                    }
                } else {
                    kindergartenNameET.setError("User does not exist");
                    kindergartenNameET.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });*/
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

    private boolean validationInfo(String kindergartenName, String password) {

        if (isFieldEmpty(kindergartenName, kindergartenNameET)) {
            return false;
        } else return isPasswordValid(password, kindergartenPasswordET);
    }

    private boolean isFieldEmpty(String field, EditText editText) {
        if (field.isEmpty()) {
            editText.requestFocus();
            editText.setError("Field cannot be empty");
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
            editText.setError("Minimum 5 character required");
            return false;
        }
        return true;
    }

}