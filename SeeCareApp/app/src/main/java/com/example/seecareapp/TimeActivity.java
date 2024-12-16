package com.example.seecareapp;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.OutputStream;

public class TimeActivity extends AppCompatActivity {
    ImageButton btn_Back;
    private LinearLayout timerLayout;
    private Button btnSetTimers;
    private AlarmManager alarmManager;

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

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        btnSetTimers = findViewById(R.id.btnSetTimers);
        timerLayout = findViewById(R.id.timerLayout);

        Button btnSendData = findViewById(R.id.btnSendData);
        Spinner spinnerHours1 = findViewById(R.id.spinnerHours1);
        Spinner spinnerMinutes1 = findViewById(R.id.spinnerMinutes1);
        Spinner spinnerSeconds1 = findViewById(R.id.spinnerSeconds1);
        Switch deviceToggle1 = findViewById(R.id.deviceToggle1);
        String[] hourValues = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
        String[] minuteValues = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09",
                "10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
                "20", "21", "22", "23", "24", "25", "26", "27", "28", "29",
                "30", "31", "32", "33", "34", "35", "36", "37", "38", "39",
                "40", "41", "42", "43", "44", "45", "46", "47", "48", "49",
                "50", "51", "52", "53", "54", "55", "56", "57", "58", "59",
                "60"};
        String[] secondValues = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09",
                "10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
                "20", "21", "22", "23", "24", "25", "26", "27", "28", "29",
                "30", "31", "32", "33", "34", "35", "36", "37", "38", "39",
                "40", "41", "42", "43", "44", "45", "46", "47", "48", "49",
                "50", "51", "52", "53", "54", "55", "56", "57", "58", "59",
                "60"};

// Tạo một ArrayAdapter và gán cho spinner
        ArrayAdapter<String> adapterHour = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, hourValues);
        adapterHour.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> minute = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, minuteValues);
        minute.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<String> seconds = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, secondValues);
        seconds.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

// Gán adapter cho các spinner.

        spinnerHours1.setAdapter(adapterHour);
        spinnerMinutes1.setAdapter(minute);
        spinnerSeconds1.setAdapter(seconds);
        btnSendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour1 = Integer.parseInt(spinnerHours1.getSelectedItem().toString());
                int minute1 = Integer.parseInt(spinnerMinutes1.getSelectedItem().toString());
                int second1 = Integer.parseInt(spinnerSeconds1.getSelectedItem().toString());

                boolean isDeviceOn = deviceToggle1.isChecked();
                String command = hour1 + ":" + minute1 + ":" + second1 + ":" + (isDeviceOn ? "ON" : "OFF");
            }
        });
    }
}
