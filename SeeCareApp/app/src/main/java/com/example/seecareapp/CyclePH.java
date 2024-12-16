package com.example.seecareapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.graphics.Color;

public class CyclePH extends View {
    private Paint circlePaint;
    private Paint textPaint;
    private int batteryPercentage = 0;

    public CyclePH(Context context) {
        super(context);
        init();
    }

    public CyclePH(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CyclePH(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // Khởi tạo Paint cho hình tròn
        circlePaint = new Paint();
        circlePaint.setColor(Color.GREEN); // Màu xanh lá
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(10);
        circlePaint.setAntiAlias(true);

        // Khởi tạo Paint cho chữ
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(80);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int radius = Math.min(width, height) / 3;

        // Vẽ hình tròn
        canvas.drawCircle(width / 2, height / 2, radius, circlePaint);

        // Vẽ phần trăm pin
        canvas.drawText(batteryPercentage + "%", width / 2, height / 2 + 30, textPaint);
    }

    public void setBatteryPercentage(int percentage) {
        batteryPercentage = percentage;
        invalidate(); // Vẽ lại view
    }
}
