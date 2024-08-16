package com.example.keepy.app.activity.newScreens;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.VideoView;

import androidx.fragment.app.Fragment;

import com.example.keepy.R;

public class CameraFragment1 extends Fragment {

    private String currentUserPhoneNumber;
    private String kindergartenName;
    private VideoView videoView;
    private ImageButton btnPlay, btnPause, btnRestart;

    public CameraFragment1() {
        // Required empty public constructor
    }

    public static CameraFragment1 newInstance(String currentUserPhoneNumber, String kindergartenName) {
        CameraFragment1 fragment = new CameraFragment1();
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
        View view = inflater.inflate(R.layout.fragment_camera1, container, false);

        // Initialize VideoView and ImageButtons
        videoView = view.findViewById(R.id.videoView);
        btnPlay = view.findViewById(R.id.btnPlay);
        btnPause = view.findViewById(R.id.btnPause);
        btnRestart = view.findViewById(R.id.btnRestart);

        // Set the video URI to the VideoView
        Uri videoUri = Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.kindergarten);
        videoView.setVideoURI(videoUri);

        // Play the video when the play button is clicked
        btnPlay.setOnClickListener(v -> videoView.start());

        // Pause the video when the pause button is clicked
        btnPause.setOnClickListener(v -> videoView.pause());

        // Restart the video when the restart button is clicked
        btnRestart.setOnClickListener(v -> {
            videoView.seekTo(0);
            videoView.start();
        });

        return view;
    }
}
