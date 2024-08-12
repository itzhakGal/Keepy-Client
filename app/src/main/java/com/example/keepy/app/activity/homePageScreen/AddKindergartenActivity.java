package com.example.keepy.app.activity.homePageScreen;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
import androidx.core.app.NotificationCompat;

import com.example.keepy.app.firebase.NotificationActivity;
import com.example.keepy.R;
import com.example.keepy.app.domain.KindergartenDetailsHelperClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class AddKindergartenActivity extends AppCompatActivity {

    EditText kindergartenNameET, kindergartenPasswordET;
    Button addKindergartenButton;
    TextView textGoToHomePage;
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
        databaseReference = FirebaseDatabase.getInstance("https://keppy-5ed11.firebaseio.com/")
                .getReference("users").child(currentUserPhoneNumber).child("MyKindergartens");

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
    }

    private void createTestData() {
        DatabaseReference reference = FirebaseDatabase.getInstance("https://keppy-5ed11.firebaseio.com/").getReference("kindergartens");

        // Inserting the first kindergarten data
        KindergartenDetailsHelperClass orenKindergarten = new KindergartenDetailsHelperClass("oren", "55555");
        reference.child("oren").setValue(orenKindergarten);

        // Inserting the second kindergarten data
        KindergartenDetailsHelperClass shakenKindergarten = new KindergartenDetailsHelperClass("shaked", "11111");
        reference.child("shaked").setValue(shakenKindergarten);

        // Inserting the third kindergarten data
        KindergartenDetailsHelperClass taliKindergarten = new KindergartenDetailsHelperClass("tali", "33333");
        reference.child("tali").setValue(taliKindergarten);
    }

    public void checkDetails() {
        DatabaseReference reference = FirebaseDatabase.getInstance("https://keppy-5ed11.firebaseio.com/").getReference("kindergartens");

        String kindergartenName = kindergartenNameET.getText().toString().trim();
        String password = kindergartenPasswordET.getText().toString().trim();

        Query checkUserDatabase = reference.orderByChild("kindergartenName").equalTo(kindergartenName);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        KindergartenDetailsHelperClass kindergarten = dataSnapshot.getValue(KindergartenDetailsHelperClass.class);
                        assert kindergarten != null;
                        if (kindergarten.getPassword().equals(password)) {
                            kindergartenNameET.setError(null);
                            kindergartenPasswordET.setError(null);
                            saveKindergartenDetails(kindergartenName, password);
                            updateParentID(kindergartenName);
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

    private void updateParentID(String kindergartenName) {
        DatabaseReference kindergartenRef = FirebaseDatabase.getInstance("https://keppy-5ed11.firebaseio.com/").getReference("kindergartens").child(kindergartenName);

        Map<String, Object> updates = new HashMap<>();
        updates.put("parentID", currentUserPhoneNumber);

        kindergartenRef.child("parentID").setValue(currentUserPhoneNumber)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Parent ID updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to update Parent ID", Toast.LENGTH_SHORT).show();
                    }
                });

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

