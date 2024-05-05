package com.example.keepy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    EditText fullNameET, gardenNameET, phoneNumberET, passwordET;
    Button registerBtn;
    TextView alreadyHaveAccountBtn;
    FirebaseDatabase database;
    DatabaseReference reference;

    @SuppressLint({"CutPasteId", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fullNameET = findViewById(R.id.fullName);
        gardenNameET = findViewById(R.id.gardenName);
        phoneNumberET = findViewById(R.id.registerPhoneNumber);
        passwordET = findViewById(R.id.registerPassword);
        registerBtn = findViewById(R.id.registerButton);
        alreadyHaveAccountBtn=findViewById(R.id.alreadyHaveAccount);

        alreadyHaveAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,MainActivity.class));
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                database = FirebaseDatabase.getInstance();
                reference = database.getReference("users");

                String fullName = fullNameET.getText().toString();
                String gardenName = gardenNameET.getText().toString();
                String phoneNumber = phoneNumberET.getText().toString();
                String password = passwordET.getText().toString();

                HelperClass helperClass = new HelperClass(fullName, gardenName, phoneNumber, password);
                reference.child(phoneNumber).setValue(helperClass);

                boolean check = validationInfo(fullName, gardenName, phoneNumber, password);
                if(check) {
                    Toast.makeText(getApplicationContext(), "Data is valid", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                }
                else{
                    Toast.makeText(getApplicationContext(), "Sorry check information again", Toast.LENGTH_SHORT).show();
                }
            }
        });




    }

    private boolean isAlphabeticalString(String str) {
        return !str.matches("[a-zA-Z\u0590-\u05FF\\s]+");
    }


    private boolean isFieldEmpty(String field, EditText editText) {
        if (field.isEmpty()) {
            editText.requestFocus();
            editText.setError("FIELD CANNOT BE EMPTY");
            return true;
        }
        return false;
    }

    private boolean isPasswordValid(String password, EditText editText) {
        if (password.length() < 5) {
            editText.requestFocus();
            editText.setError("MINIMUM 5 CHARACTERS REQUIRED");
            return false;
        }
        return true;
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        String regex = "^[0-9]{10}$"; // Assumes a 10-digit phone number
        return phoneNumber.matches(regex);
    }

    private boolean validationInfo(String fullName, String gardenName, String phoneNumber, String password) {
        if (isAlphabeticalString(fullName)) {
            fullNameET.requestFocus();
            fullNameET.setError("ENTER ONLY ALPHABETICAL CHARACTERS");
            return false;
        }
        if (isFieldEmpty(gardenName, gardenNameET)) {
            return false;
        }
        if (isFieldEmpty(phoneNumber, phoneNumberET)) {
            return false;
        }
        if (!isValidPhoneNumber(phoneNumber)) {
            phoneNumberET.requestFocus();
            phoneNumberET.setError("ENTER A VALID PHONE NUMBER");
            return false;
        }
        if (isAlphabeticalString(gardenName)) {
            gardenNameET.requestFocus();
            gardenNameET.setError("ENTER ONLY ALPHABETICAL CHARACTERS");
            return false;
        }
        return isPasswordValid(password, passwordET);
    }



}