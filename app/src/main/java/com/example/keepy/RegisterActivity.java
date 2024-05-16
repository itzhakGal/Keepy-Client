package com.example.keepy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.keepy.helperClass.UserDetailsHelperClass;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    EditText fullNameET, phoneNumberET;
    Button registerBtn;
    FirebaseDatabase database;
    DatabaseReference reference;

    @SuppressLint({"CutPasteId", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fullNameET = findViewById(R.id.fullName);
        phoneNumberET = findViewById(R.id.registerPhoneNumber);
        registerBtn = findViewById(R.id.registerButton);


        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                database = FirebaseDatabase.getInstance();
                reference = database.getReference("users");

                String fullName = fullNameET.getText().toString();
                String phoneNumber = phoneNumberET.getText().toString();

                UserDetailsHelperClass userDetailsHelperClass = new UserDetailsHelperClass(fullName, phoneNumber);
                reference.child(phoneNumber).setValue(userDetailsHelperClass);

                boolean check = validationInfo(fullName, phoneNumber);
                if(check) {
                    Toast.makeText(getApplicationContext(), "Data is valid", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, HomePageActivity.class);
                    intent.putExtra("currentUserPhoneNumber", phoneNumber);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Sorry check information again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isAlphabeticalString(String str) {
        if (str.matches("[a-zA-Z\u0590-\u05FF\\s]+"))
        {
        return true;
        }
        else
        {
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
        if (isFieldEmpty(fullName,fullNameET) && isAlphabeticalString(fullName)) {
            return false;
        }

        if (isFieldEmpty(phoneNumber, phoneNumberET)) {
            return false;
        }
        if (!isValidPhoneNumber(phoneNumber)) {
            phoneNumberET.requestFocus();
            phoneNumberET.setError("Enter a valid phone number");
            return false;
        }

        return true;
    }



}