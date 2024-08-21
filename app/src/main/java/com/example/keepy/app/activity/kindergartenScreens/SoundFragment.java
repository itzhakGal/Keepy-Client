package com.example.keepy.app.activity.kindergartenScreens;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.chibde.visualizer.LineBarVisualizer;
import com.example.keepy.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SoundFragment extends Fragment {

    private String currentUserPhoneNumber;
    private String kindergartenName;
    private MediaPlayer mediaPlayer;
    private ImageButton btnPlayStop;
    private SeekBar sbSound;
    private Spinner spinnerMoreEvents;
    private LineBarVisualizer lineBarVisualizer;
    private Handler handler = new Handler();
    private Runnable updateSeekBar;
    private List<String> eventKeysList = new ArrayList<>();
    private Map<String, DataSnapshot> eventsMap = new HashMap<>();

    public SoundFragment() {
        // Required empty public constructor
    }

    public static SoundFragment newInstance(String currentUserPhoneNumber, String kindergartenName) {
        SoundFragment fragment = new SoundFragment();
        Bundle args = new Bundle();
        args.putString("currentUserPhoneNumber", currentUserPhoneNumber);
        args.putString("kindergartenName", kindergartenName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentUserPhoneNumber = getArguments().getString("currentUserPhoneNumber");
            kindergartenName = getArguments().getString("kindergartenName");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sounds, container, false);

        // Initialize UI elements
        TextView tvEventTypeValue = view.findViewById(R.id.tvEventTypeValue);
        TextView tvEventTimeValue = view.findViewById(R.id.tvEventTimeValue);
        TextView tvEventDetail = view.findViewById(R.id.tvEventDetailSound);
        btnPlayStop = view.findViewById(R.id.btnPlayStop);
        sbSound = view.findViewById(R.id.sbSound);
        spinnerMoreEvents = view.findViewById(R.id.spinnerMoreEvents);
        lineBarVisualizer = view.findViewById(R.id.visualizerLineBar);

        // Fetch all events from Firebase
        fetchAllEvents(tvEventTypeValue, tvEventTimeValue, tvEventDetail);

        return view;
    }

    private void fetchAllEvents(TextView tvEventTypeValue, TextView tvEventTimeValue, TextView tvEventDetail) {
        DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference("kindergartens/" + kindergartenName + "/events");

        eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventKeysList.clear();
                eventsMap.clear();

                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    String key = eventSnapshot.getKey();
                    eventKeysList.add(key);
                    eventsMap.put(key, eventSnapshot);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, eventKeysList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerMoreEvents.setAdapter(adapter);

                spinnerMoreEvents.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedKey = eventKeysList.get(position);
                        // Update the UI and audio based on the selected event
                        displayEventDetails(selectedKey, tvEventTypeValue, tvEventTimeValue, tvEventDetail);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // Handle case where no item is selected if needed
                    }
                });

                // Display the details of the latest event by default
                if (!eventKeysList.isEmpty()) {
                    displayEventDetails(eventKeysList.get(eventKeysList.size() - 1), tvEventTypeValue, tvEventTimeValue, tvEventDetail);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void displayEventDetails(String key, TextView tvEventTypeValue, TextView tvEventTimeValue, TextView tvEventDetail) {
        DataSnapshot eventSnapshot = eventsMap.get(key);
        if (eventSnapshot != null) {
            String eventType = eventSnapshot.child("event").getValue(String.class);
            String eventTime = eventSnapshot.child("timestamp").getValue(String.class);
            String audioFilePath = key + ".wav"; // Adjusted to match the .wav extension

            // Set the basic event type and time
            tvEventTypeValue.setText(eventType);
            tvEventTimeValue.setText(eventTime);

            // Handle event details based on the type
            if (eventType != null) {
                switch (eventType) {
                    case "curse word detected":
                        String word = eventSnapshot.child("word").getValue(String.class);
                        tvEventDetail.setText("Word: " + word);
                        break;

                    case "inappropriate sentence detected":
                        String sentence = eventSnapshot.child("sentence").getValue(String.class);
                        tvEventDetail.setText("Sentence: " + sentence);
                        break;
                    case "crying detected":
                        tvEventDetail.setText("Baby is crying");
                        break;

                    // Add other cases here if there are other event types
                }
            }

            // Stop and release previous MediaPlayer if exists
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }

            // Initialize MediaPlayer with the audio file from Firebase Storage
            initializeMediaPlayer(audioFilePath);
        }
    }

    private void initializeMediaPlayer(String audioFilePath) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child(audioFilePath);

        try {
            // Start streaming the audio from Firebase Storage
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(uri.toString());
                    mediaPlayer.setOnPreparedListener(mp -> {
                        // Set SeekBar max to the duration of the media
                        sbSound.setMax(mediaPlayer.getDuration());

                        // Set up Play/Stop button action
                        btnPlayStop.setOnClickListener(v -> {
                            if (mediaPlayer.isPlaying()) {
                                stopAudio();
                            } else {
                                playAudio();
                            }
                        });

                        // SeekBar change listener
                        sbSound.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                if (fromUser && mediaPlayer != null) {
                                    mediaPlayer.seekTo(progress);
                                }
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {}

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {}
                        });

                        // Setting up the LineBarVisualizer
                        lineBarVisualizer.setColor(ContextCompat.getColor(getContext(), R.color.myColor));
                        lineBarVisualizer.setDensity(40);
                        lineBarVisualizer.setPlayer(mediaPlayer.getAudioSessionId());

                        // Start updating the SeekBar and visualizer
                        startUpdatingSeekBar();

                        // Start the media player
                        mediaPlayer.start();
                    });

                    // Prepare the MediaPlayer asynchronously (this will start loading the stream)
                    mediaPlayer.prepareAsync();

                } catch (IOException e) {
                    e.printStackTrace();
                    // Handle the error with an appropriate message
                    showError("Error initializing MediaPlayer");
                }
            }).addOnFailureListener(exception -> {
                // Handle any errors
                exception.printStackTrace();
                showError("Error fetching audio URL");
            });
        } catch (Exception e) {
            e.printStackTrace();
            // Handle the error with an appropriate message
            showError("Error streaming audio");
        }
    }


    private void startUpdatingSeekBar() {
        updateSeekBar = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    sbSound.setProgress(currentPosition);
                    handler.postDelayed(this, 100); // Update every 100 ms
                }
            }
        };
        handler.post(updateSeekBar);
    }

    // Error display helper method
    private void showError(String message) {
        // Show an error message to the user
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    private void playAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            startUpdatingSeekBar(); // Start updating SeekBar when audio starts playing
        }
    }

    private void stopAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            mediaPlayer.seekTo(0); // Reset to beginning
            sbSound.setProgress(0);
            handler.removeCallbacks(updateSeekBar);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacks(updateSeekBar);
        if (lineBarVisualizer != null) {
            lineBarVisualizer.release();
        }
    }
}
