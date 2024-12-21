package com.example.seecareapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class PHCircleView extends View {

    private String phValue; // Giá trị pH mặc định
    private Paint circlePaint;
    private Paint textPaint;

    public PHCircleView(Context context) {
        super(context);
        init();
    }

    public PHCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        phValue = "0";
        float ph = Float.parseFloat(phValue);
        //Khởi tạo paint cho vòng trònif
        if (ph >= 7.5) {
            circlePaint = new Paint();
            circlePaint.setColor(Color.rgb(255, 0, 0)); // Màu vòng tròn
            circlePaint.setStyle(Paint.Style.FILL);
        } else if (ph <= 6.5) {
            circlePaint = new Paint();
            circlePaint.setColor(Color.rgb(0, 0, 255)); // Màu vòng tròn
            circlePaint.setStyle(Paint.Style.FILL);
        } else {
            circlePaint = new Paint();
            circlePaint.setColor(Color.rgb(0, 255, 0)); // Màu vòng tròn
            circlePaint.setStyle(Paint.Style.FILL);
        }
        // Khởi tạo paint cho văn bản
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE); // Màu chữ
        textPaint.setTextSize(300); // Kích thước chữ
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Vẽ vòng tròn
        float radius = Math.min(getWidth(), getHeight()) / 2f - 10; // Bán kính vòng tròn
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        canvas.drawCircle(centerX, centerY, radius, circlePaint);

        // Vẽ giá trị pH
        String phText = phValue; // Hiển thị độ pH với một chữ số thập phân
        canvas.drawText(phText, centerX, centerY + textPaint.getTextSize() / 4, textPaint);
    }

    // Phương thức để cập nhật giá trị pH
    public void setPhValue(String phValue) {
        this.phValue = phValue;
        invalidate(); // Yêu cầu vẽ lại view
    }
}
