package com.example.keepy;

import android.Manifest;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.keepy.helperClass.KindergartenDetailsHelperClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class AddKindergartenActivity extends AppCompatActivity {

    EditText kindergartenNameET, kindergartenPasswordET;
    Button addKindergartenButton;
    TextView textGoToHomePage;
    Button buttonNotification;
    String currentUserPhoneNumber;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_kindergarten);

        kindergartenNameET = findViewById(R.id.kindergartenName);
        kindergartenPasswordET = findViewById(R.id.Password);
        addKindergartenButton = findViewById(R.id.addKindergartenButton);
        textGoToHomePage = findViewById(R.id.textGoToHomePage);
        Intent intent = getIntent();
        currentUserPhoneNumber = intent.getStringExtra("currentUserPhoneNumber");
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(currentUserPhoneNumber).child("MyKindergartens");


        imageAnimations();


        textGoToHomePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddKindergartenActivity.this, HomePageActivity.class);
                intent.putExtra("currentUserPhoneNumber", currentUserPhoneNumber);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        buttonNotification = findViewById(R.id.buttonNotification);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ContextCompat.checkSelfPermission(AddKindergartenActivity.this,
                    Manifest.permission.RECEIVE_BOOT_COMPLETED) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(AddKindergartenActivity.this,
                        new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED}, 101);
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
                String kindergartenName = kindergartenNameET.getText().toString();
                String password = kindergartenPasswordET.getText().toString();

                boolean check = validationInfo(kindergartenName, password);
                if (check) {
                    checkDetails();
                } else {
                    Toast.makeText(getApplicationContext(), "Sorry, check information again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void imageAnimations() {
        ImageView imageView = findViewById(R.id.imageView5);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        imageView.startAnimation(animation);
        ImageView imageView1 = findViewById(R.id.imageView6);
        Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        imageView1.startAnimation(animation1);
    }

    private void createTestData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("kindergartens");

        // Inserting the first kindergarten data
        String orenId = reference.push().getKey();
        KindergartenDetailsHelperClass orenKindergarten = new KindergartenDetailsHelperClass("oren", "55555");
        reference.child("oren").setValue(orenKindergarten);

        // Inserting the second kindergarten data
        String shakenId = reference.push().getKey();
        KindergartenDetailsHelperClass shakenKindergarten = new KindergartenDetailsHelperClass("shaked", "11111");
        reference.child("shaked").setValue(shakenKindergarten);

        // Inserting the third kindergarten data
        String taliId = reference.push().getKey();
        KindergartenDetailsHelperClass taliKindergarten = new KindergartenDetailsHelperClass("tali", "33333");
        reference.child("tali").setValue(taliKindergarten);
    }


    public void checkDetails() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("kindergartens");

        String kindergartenName = kindergartenNameET.getText().toString().trim();
        String password = kindergartenPasswordET.getText().toString().trim();

        Query checkUserDatabase = reference.orderByChild("kindergartenName").equalTo(kindergartenName);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        KindergartenDetailsHelperClass kindergarten = dataSnapshot.getValue(KindergartenDetailsHelperClass.class);
                        if (kindergarten.getPassword().equals(password)) {
                            kindergartenNameET.setError(null);
                            kindergartenPasswordET.setError(null);
                            saveKindergartenDetails(kindergartenName, password);
                            Toast.makeText(getApplicationContext(), "Kindergarten details are valid", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AddKindergartenActivity.this, HomePageActivity.class);
                            intent.putExtra("kindergartenName", kindergartenName);
                            intent.putExtra("password", password);
                            startActivity(intent);
                            return;
                        } else {
                            kindergartenPasswordET.setError("Invalid password, this password does not have permissions for Kindergarten " + kindergartenName);
                            kindergartenPasswordET.requestFocus();
                        }
                    }
                } else {
                    kindergartenNameET.setError("Kindergarten does not exist");
                    kindergartenNameET.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddKindergartenActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveKindergartenDetails(String kindergartenName, String password) {
        String id = databaseReference.push().getKey();
        KindergartenDetailsHelperClass kindergartenDetailsHelperClass = new KindergartenDetailsHelperClass(kindergartenName, password);
        // Check if the id already exists
        if (id != null) {
            databaseReference.child(id).setValue(kindergartenDetailsHelperClass);

            // Clear EditText fields
            kindergartenNameET.setText("");
            kindergartenPasswordET.setText("");
        } else {
            Toast.makeText(this, "Failed to save kindergarten details", Toast.LENGTH_SHORT).show();
        }
    }



    public void makeNotification() {
        String channelID = "CHANNEL_ID_NOTIFICATION";
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext(), channelID);
        builder.setSmallIcon(R.drawable.ic_notification);
        builder.setContentTitle("Notification Title");
        builder.setContentText("Some text for notification here")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("data", "some value to be passed here");

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE); // Add FLAG_IMMUTABLE
        builder.setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel =
                    notificationManager.getNotificationChannel(channelID);
            if (notificationChannel == null) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                notificationChannel = new NotificationChannel(channelID,
                        "Keepy Notifications", importance);
                notificationChannel.setDescription("Notifications for Keepy app");
                notificationChannel.setLightColor(Color.GREEN);
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
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

    private boolean isPasswordValid(String password, EditText editText) {
        if (password.length() < 5) {
            editText.requestFocus();
            editText.setError("Minimum 5 characters required");
            return false;
        }
        return true;
    }

    public String getCurrentUserPhoneNumber() {
        return currentUserPhoneNumber;
    }

    public void setCurrentUserPhoneNumber(String currentUserPhoneNumber) {
        this.currentUserPhoneNumber = currentUserPhoneNumber;
    }

}