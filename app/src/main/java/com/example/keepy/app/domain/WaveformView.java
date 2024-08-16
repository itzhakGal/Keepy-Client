package com.example.keepy.app.domain;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class WaveformView extends View {

    private Paint paint;
    private float[] waveform;

    public WaveformView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(2f);
    }

    public void setWaveform(float[] waveform) {
        this.waveform = waveform;
        invalidate();  // Re-draw the view
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (waveform != null) {
            int width = getWidth();
            int height = getHeight();
            int length = waveform.length;

            for (int i = 0; i < length - 1; i++) {
                float x1 = width * i / (float) length;
                float x2 = width * (i + 1) / (float) length;
                float y1 = height / 2 - waveform[i] * height / 2;
                float y2 = height / 2 - waveform[i + 1] * height / 2;
                canvas.drawLine(x1, y1, x2, y2, paint);
            }
        }
    }
}
