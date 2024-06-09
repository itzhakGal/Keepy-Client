package com.example.keepy.app.activity.registerScreen;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.keepy.R;
import com.example.keepy.app.activity.homePageScreen.HomePageActivity;
import com.example.keepy.app.domain.UserDetailsHelperClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {
    EditText fullNameET, phoneNumberET;
    Button registerBtn;
    CheckBox rememberMeCB;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;

    String currentUserPhoneNumber;

    @SuppressLint({"CutPasteId", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fullNameET = findViewById(R.id.fullName);
        phoneNumberET = findViewById(R.id.registerPhoneNumber);
        registerBtn = findViewById(R.id.registerButton);
        rememberMeCB = findViewById(R.id.rememberMe);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = fullNameET.getText().toString();
                String phoneNumber = phoneNumberET.getText().toString();

                boolean check = validationInfo(fullName, phoneNumber);
                if (check) {
                    mAuth.signInAnonymously().addOnCompleteListener(RegisterActivity.this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            writeNewUser(phoneNumber, fullName, phoneNumber);
                        } else {
                            Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Sorry, check information again", Toast.LENGTH_SHORT).show();
                }
            }
        });

        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        String checkbox = preferences.getString("remember", "");
        if (checkbox.equals("true")) {
            currentUserPhoneNumber = preferences.getString("phoneNumber", "");
            Intent intent = new Intent(RegisterActivity.this, HomePageActivity.class);
            intent.putExtra("currentUserPhoneNumber", currentUserPhoneNumber);
            startActivity(intent);
        } else if (checkbox.equals("false")) {
            Toast.makeText(this, "Please Sign in", Toast.LENGTH_SHORT).show();
        }

        rememberMeCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rememberMeCB.isChecked()) {
                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "true");
                    editor.putString("phoneNumber", phoneNumberET.getText().toString());
                    editor.apply();
                    Toast.makeText(RegisterActivity.this, "Checked", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "false");
                    editor.apply();
                    Toast.makeText(RegisterActivity.this, "Unchecked", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void writeNewUser(String userId, String fullName, String phoneNumber) {
        database = FirebaseDatabase.getInstance("https://keppy-5ed11.firebaseio.com/");
        reference = database.getReference("users");

        UserDetailsHelperClass userDetailsHelperClass = new UserDetailsHelperClass(fullName, phoneNumber);
        reference.child(userId).setValue(userDetailsHelperClass, (databaseError, databaseReference) -> {
            if (databaseError != null) {
                Toast.makeText(getApplicationContext(), "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Data is valid", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, HomePageActivity.class);
                intent.putExtra("currentUserPhoneNumber", phoneNumber);
                startActivity(intent);
            }
        });
    }

    private boolean isAlphabeticalString(String str) {
        if (str.matches("[a-zA-Z\u0590-\u05FF\\s]+")) {
            return true;
        } else {
            fullNameET.requestFocus();
            fullNameET.setError("Enter only alphabetical characters ");
            return false;
        }
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

    private boolean validationInfo(String fullName, String phoneNumber) {
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
}
