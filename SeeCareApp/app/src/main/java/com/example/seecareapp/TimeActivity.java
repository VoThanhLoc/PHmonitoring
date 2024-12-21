package com.example.seecareapp;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.OutputStream;

public class TimeActivity extends AppCompatActivity {
    ImageButton btn_Back;
    private TimePicker timePicker;
    private EditText timeDisplay;
    private Button btnSetTimers;
    private EditText txt_timerun;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_time);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //nut back về trang trước
        btn_Back = findViewById(R.id.btn_Back);
        btn_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        txt_timerun = findViewById(R.id.timerun);

        // Ánh xạ TimePicker và EditText
        timePicker = findViewById(R.id.oclock);
        timeDisplay = findViewById(R.id.timeDisplay);

        // Đặt chế độ 24h cho TimePicker
        timePicker.setIs24HourView(true);

        // Lắng nghe sự kiện thay đổi thời gian
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                // Tạo chuỗi thời gian từ giờ và phút
                String time = String.format("%02d:%02d", hourOfDay, minute);

                // Hiển thị thời gian vào EditText
                timeDisplay.setText(time);
            }
        });

        btnSetTimers = findViewById(R.id.btnSetTimers);
        btnSetTimers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendTimeToFirebase();
            }
        });
    }

    private void sendTimeToFirebase() {
        // Lấy giờ và phút từ EditText
        if(timeDisplay.getText().toString().isEmpty())
        {
            Toast.makeText(TimeActivity.this, "Vui lòng cài đặt thời gian bắt đầu", Toast.LENGTH_SHORT).show();
            return;
        }
        String time = timeDisplay.getText().toString();
        String[] timeParts = time.split(":");  // Tách giờ và phút từ chuỗi "HH:mm"
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        // Lấy thời gian chạy từ EditText

        // Kiểm tra nếu trường timerun trống
        if (txt_timerun.getText().toString().isEmpty()) {
            Toast.makeText(TimeActivity.this, "Vui lòng nhập thời gian chạy", Toast.LENGTH_SHORT).show();
            return;  // Dừng việc gửi dữ liệu nếu timerun trống
        }
        int runTime = Integer.parseInt(txt_timerun.getText().toString());
        // Tạo đối tượng PumpSchedule
        PumpSchedule pumpSchedule = new PumpSchedule(hour, minute, runTime);
        // Khởi tạo Firebase Database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("pumpSchedule");
        // Gửi dữ liệu lên Firebase
        myRef.setValue(pumpSchedule)
                .addOnSuccessListener(aVoid -> {
                    // Thông báo thành công
                    Toast.makeText(TimeActivity.this, "Dữ liệu đã được gửi lên Firebase!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Thông báo thất bại
                    Toast.makeText(TimeActivity.this, "Gửi dữ liệu thất bại!", Toast.LENGTH_SHORT).show();
                });
    }
}
