package com.example.keepy.app.domain;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class VisualizerView extends View {
    private byte[] waveform;
    private Paint paint;
    private Paint backgroundPaint;
    private int width;
    private int height;
    private static final int LINE_COLOR = 0xFFD2B48C; // Light brown color (tan)
    private static final int BACKGROUND_COLOR = 0xFFDDDDDD; // Light gray color

    public VisualizerView(Context context) {
        super(context);
        init();
    }

    public VisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        waveform = null;

        // Set up the paint for the waveform
        paint = new Paint();
        paint.setStrokeWidth(4f);
        paint.setAntiAlias(true);
        paint.setColor(LINE_COLOR);

        // Set up the paint for the background grid
        backgroundPaint = new Paint();
        backgroundPaint.setStrokeWidth(2f);
        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setColor(BACKGROUND_COLOR);
    }

    public void updateVisualizer(byte[] waveform) {
        this.waveform = waveform;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (waveform == null) {
            return;
        }

        float centerY = height / 2f;
        float scale = height / 128f;
        float step = width / (float) waveform.length;

        // Draw the background grid
        for (int i = 0; i < 10; i++) {
            float y = i * height / 10f;
            canvas.drawLine(0, y, width, y, backgroundPaint);
        }

        // Draw the waveform as a continuous line
        for (int i = 1; i < waveform.length; i++) {
            float x1 = (i - 1) * step;
            float y1 = centerY + ((byte) (waveform[i - 1] + 128)) * scale;
            float x2 = i * step;
            float y2 = centerY + ((byte) (waveform[i] + 128)) * scale;
            canvas.drawLine(x1, y1, x2, y2, paint);
        }
    }
}
