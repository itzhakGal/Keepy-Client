package com.example.keepy.app.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.keepy.R;

public class AlertDetailsActivity extends AppCompatActivity {

    private TextView alertTitle;
    private TextView alertMessage;
    private TextView eventType;
    private TextView kindergartenName;
    private TextView timestamp;
    private TextView word;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_details);

        alertTitle = findViewById(R.id.alertTitle);
        alertMessage = findViewById(R.id.alertMessage);
        eventType = findViewById(R.id.eventType);
        kindergartenName = findViewById(R.id.kindergartenName);
        timestamp = findViewById(R.id.timestamp);
        word = findViewById(R.id.word);

        // Retrieve the data from the Intent
        String title = getIntent().getStringExtra("title");
        String message = getIntent().getStringExtra("body");
        String id = getIntent().getStringExtra("id");
        String event = getIntent().getStringExtra("event");
        String kgName = getIntent().getStringExtra("kindergarten_name");
        String time = getIntent().getStringExtra("timestamp");
        String detectedWord = getIntent().getStringExtra("word");
        String detectedSentence = getIntent().getStringExtra("sentence");

        // Set the data to the TextViews
        alertTitle.setText(event);
        alertMessage.setText(message);
        eventType.setText(event);
        kindergartenName.setText(kgName);
        timestamp.setText(time);

        // Display either word or sentence in the same TextView and manage visibility
        String textToShow = detectedWord != null ? detectedWord : detectedSentence;

        if (textToShow != null) {
            word.setText(textToShow);
            word.setVisibility(View.VISIBLE);
            findViewById(R.id.iconDetails).setVisibility(View.VISIBLE);
            findViewById(R.id.word_detected).setVisibility(View.VISIBLE);
        } else {
            word.setVisibility(View.GONE); // Hide the TextView if neither word nor sentence is available
        }

    }
}
