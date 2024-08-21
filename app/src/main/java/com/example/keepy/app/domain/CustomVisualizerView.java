package com.example.keepy.app.domain;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.util.AttributeSet;
import android.view.View;

public class CustomVisualizerView extends View {
    private byte[] waveform;
    private Paint paint;
    private Visualizer visualizer;

    public CustomVisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setStrokeWidth(5f);
        paint.setColor(0xFF00FF00);  // Green color for waveform
    }

    public void linkTo(MediaPlayer mediaPlayer) {
        visualizer = new Visualizer(mediaPlayer.getAudioSessionId());
        visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            @Override
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
                CustomVisualizerView.this.waveform = waveform;
                invalidate();  // Redraw the view with the new waveform data
            }

            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
                // Optional: Handle FFT data if needed
            }
        }, Visualizer.getMaxCaptureRate() / 2, true, false);

        visualizer.setEnabled(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (waveform != null) {
            float width = getWidth();
            float height = getHeight();
            float centerY = height / 2;

            for (int i = 0; i < waveform.length - 1; i++) {
                float startX = i * (width / (float) waveform.length);
                float startY = centerY + ((byte) (waveform[i] + 128)) * (centerY / 128);
                float stopX = (i + 1) * (width / (float) waveform.length);
                float stopY = centerY + ((byte) (waveform[i + 1] + 128)) * (centerY / 128);

                canvas.drawLine(startX, startY, stopX, stopY, paint);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (visualizer != null) {
            visualizer.release();
        }
    }
}

