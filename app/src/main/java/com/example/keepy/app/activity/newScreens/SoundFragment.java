package com.example.keepy.app.activity.newScreens;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.keepy.R;
import com.example.keepy.app.domain.WaveformView;

public class SoundFragment extends Fragment {

    private String currentUserPhoneNumber;
    private String kindergartenName;
    private MediaPlayer mediaPlayer;
    private ImageButton btnPlayStop;
    private SeekBar sbSound;
    private WaveformView waveformView;
    private Handler handler = new Handler();
    private Runnable updateSeekBar;

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
        btnPlayStop = view.findViewById(R.id.btnPlayStop);
        sbSound = view.findViewById(R.id.sbSound);
        waveformView = view.findViewById(R.id.waveformView);

        // Set dynamic values
        tvEventTypeValue.setText("Some event type"); // Set your dynamic event type here
        tvEventTimeValue.setText("10:30 AM"); // Set your dynamic event time here

        // Initialize MediaPlayer
        mediaPlayer = MediaPlayer.create(getContext(), R.raw.your_audio_file); // Replace with your audio file resource
        sbSound.setMax(mediaPlayer.getDuration());

        // Set up Play/Stop button action
        btnPlayStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    stopAudio();
                } else {
                    playAudio();
                }
            }
        });

        // SeekBar change listener
        sbSound.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                    updateWaveform(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // Update waveform view with initial data
        updateWaveform(0);

        return view;
    }

    private void playAudio() {
        mediaPlayer.start();
        // You can change the icon here if you have another drawable, or leave it as is
        // btnPlayStop.setImageResource(R.drawable.ic_play); // No icon change needed
        updateSeekBar = new Runnable() {
            @Override
            public void run() {
                int currentPosition = mediaPlayer.getCurrentPosition();
                sbSound.setProgress(currentPosition);
                updateWaveform(currentPosition);
                handler.postDelayed(this, 100);
            }
        };
        handler.postDelayed(updateSeekBar, 0);
    }

    private void stopAudio() {
        mediaPlayer.pause();
        mediaPlayer.seekTo(0); // Reset to beginning
        sbSound.setProgress(0);
        // btnPlayStop.setImageResource(R.drawable.ic_play); // No icon change needed
        handler.removeCallbacks(updateSeekBar);
        updateWaveform(0); // Reset waveform
    }

    private void updateWaveform(int progress) {
        // Generate the waveform for the current position.
        // This is a placeholder; replace with actual waveform data extraction
        float[] waveform = getWaveformForAudioSegment(progress);
        waveformView.setWaveform(waveform);
    }

    private float[] getWaveformForAudioSegment(int position) {
        // Placeholder method for generating waveform data
        // Replace with actual data extraction method
        int length = 100; // Replace with actual waveform length
        float[] waveform = new float[length];
        for (int i = 0; i < length; i++) {
            waveform[i] = (float) Math.sin(2 * Math.PI * i / length);
        }
        return waveform;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacks(updateSeekBar);
    }
}