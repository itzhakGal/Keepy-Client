package com.example.keepy.app.activity.kindergartenScreens;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
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
    private ImageButton btnPlayStop, btnRewind, btnPrevious, btnNext, btnFastForward;
    private SeekBar sbSound;
    private LineBarVisualizer lineBarVisualizer;
    private Handler handler = new Handler();
    private Runnable updateSeekBar;
    private List<String> eventKeysList = new ArrayList<>();
    private Map<String, DataSnapshot> eventsMap = new HashMap<>();
    private int currentEventIndex = -1;

    private int[] eventImages = {
            R.drawable.sound1,
            R.drawable.sound2,
            R.drawable.sound3,
            R.drawable.sound4
    };

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sounds, container, false);

        // Initialize UI elements
        TextView tvEventTypeValue = view.findViewById(R.id.tvEventTypeValue);
        TextView tvEventTimeValue = view.findViewById(R.id.tvEventTimeValue);
        TextView tvEventDetail = view.findViewById(R.id.tvEventDetailSound);
        btnPlayStop = view.findViewById(R.id.btnPlayStop);
        btnRewind = view.findViewById(R.id.btnRewind);
        btnPrevious = view.findViewById(R.id.btnPrevious);
        btnNext = view.findViewById(R.id.btnNext);
        btnFastForward = view.findViewById(R.id.btnFastForward);
        sbSound = view.findViewById(R.id.sbSound);
        lineBarVisualizer = view.findViewById(R.id.visualizerLineBar);

        // Set up the buttons for skipping/rewinding/fast-forwarding
        setupButtonListeners(tvEventTypeValue, tvEventTimeValue, tvEventDetail);

        // Fetch all events from Firebase
        fetchEventDetails(tvEventTypeValue, tvEventTimeValue, tvEventDetail);

        return view;
    }

    private void fetchEventDetails(TextView tvEventTypeValue, TextView tvEventTimeValue, TextView tvEventDetail) {
        DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference("kindergartens/" + kindergartenName + "/events");

        eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventsMap.clear();
                eventKeysList.clear();

                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    String key = eventSnapshot.getKey();
                    eventKeysList.add(key);
                    eventsMap.put(key, eventSnapshot);
                }

                // Display the details of the first event by default
                if (!eventsMap.isEmpty()) {
                    currentEventIndex = 0;  // Start with the first event
                    displayEventDetails(eventKeysList.get(currentEventIndex), tvEventTypeValue, tvEventTimeValue, tvEventDetail);
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
            tvEventTypeValue.setText(capitalize(eventType));
            tvEventTimeValue.setText(eventTime);

            // Handle event details based on the type
            if (eventType != null) {
                switch (eventType) {
                    case "curse word detected":
                        String word = eventSnapshot.child("word").getValue(String.class);
                        tvEventDetail.setText("Word: " + capitalize(word));
                        break;

                    case "inappropriate sentence detected":
                        String sentence = eventSnapshot.child("sentence").getValue(String.class);
                        tvEventDetail.setText("Sentence: " + capitalize(sentence));
                        break;

                    case "crying detected":
                        tvEventDetail.setText("Baby is crying");
                        break;

                    // Add other cases here if there are other event types
                }
            }

            // Set the image based on the current event index
            ImageView ivEventImage = getView().findViewById(R.id.ivEventImage);
            ivEventImage.setImageResource(eventImages[currentEventIndex % eventImages.length]);

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
                        lineBarVisualizer.setDensity(60);
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

    private void setupButtonListeners(TextView tvEventTypeValue, TextView tvEventTimeValue, TextView tvEventDetail) {
        btnRewind.setOnClickListener(v -> rewindAudio());
        btnFastForward.setOnClickListener(v -> fastForwardAudio());
        btnPrevious.setOnClickListener(v -> playPrevious(tvEventTypeValue, tvEventTimeValue, tvEventDetail));
        btnNext.setOnClickListener(v -> playNext(tvEventTypeValue, tvEventTimeValue, tvEventDetail));
    }

    private void rewindAudio() {
        if (mediaPlayer != null) {
            int currentPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.seekTo(Math.max(currentPosition - 5000, 0)); // Rewind 5 seconds
        }
    }

    private void fastForwardAudio() {
        if (mediaPlayer != null) {
            int currentPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.seekTo(Math.min(currentPosition + 5000, mediaPlayer.getDuration())); // Fast-forward 5 seconds
        }
    }

    private void playPrevious(TextView tvEventTypeValue, TextView tvEventTimeValue, TextView tvEventDetail) {
        if (currentEventIndex > 0) {
            currentEventIndex--;
            displayEventDetails(eventKeysList.get(currentEventIndex), tvEventTypeValue, tvEventTimeValue, tvEventDetail);
        } else {
            showError("No previous events available");
        }
    }

    private void playNext(TextView tvEventTypeValue, TextView tvEventTimeValue, TextView tvEventDetail) {
        if (currentEventIndex < eventKeysList.size() - 1) {
            currentEventIndex++;
            displayEventDetails(eventKeysList.get(currentEventIndex), tvEventTypeValue, tvEventTimeValue, tvEventDetail);
        } else {
            showError("No next events available");
        }
    }

    private void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    private String capitalize(String input) {
        if (input == null || input.isEmpty()) return input;
        return input.substring(0, 1).toUpperCase() + input.substring(1);
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

    @Override
    public void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            stopAudio();
        }
    }

}
