package com.example.keepy.app.activity.kindergartenScreen.details;

import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.keepy.R;
import com.example.keepy.app.domain.VisualizerView;
import com.example.keepy.databinding.FragmentDetailsBinding;

public class DetailsFragment extends Fragment {

    private FragmentDetailsBinding binding;
    private MediaPlayer mediaPlayer;
    private Visualizer visualizer;
    private VisualizerView visualizerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DetailsViewModel homeViewModel =
                new ViewModelProvider(this).get(DetailsViewModel.class);

        binding = FragmentDetailsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        visualizerView = root.findViewById(R.id.visualizer_view);

        initializeMediaPlayerAndVisualizer();

        return root;
    }

    private void initializeMediaPlayerAndVisualizer() {
        mediaPlayer = MediaPlayer.create(getContext(), R.raw.sample_audio);
        mediaPlayer.setLooping(true);

        setupVisualizer();

        mediaPlayer.start();
    }

    private void setupVisualizer() {
        visualizer = new Visualizer(mediaPlayer.getAudioSessionId());
        visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            @Override
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
                visualizerView.updateVisualizer(bytes);
            }

            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
                // Not used in this example
            }
        }, Visualizer.getMaxCaptureRate() / 8, true, false); // Changed capture rate to 1/8 of the maximum rate
        visualizer.setEnabled(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (visualizer != null) {
            visualizer.release();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        binding = null;
    }
}
