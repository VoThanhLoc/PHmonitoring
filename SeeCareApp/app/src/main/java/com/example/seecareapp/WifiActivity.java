package com.example.seecareapp;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class WifiActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;

    private WifiManager wifiManager;
    ImageButton btn_Back;
    Button btn_Connect;
    Spinner wifi_Spinner;
    EditText txt_Password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_wifi);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btn_Back = findViewById(R.id.btn_Back);
        wifi_Spinner = findViewById(R.id.wifiSpinner);
        txt_Password = findViewById(R.id.txt_password_input);
        btn_Connect = findViewById(R.id.btn_connect);

        btn_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_Connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputText = txt_Password.getText().toString().trim();
                if(inputText.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Vui lòng nhập password", Toast.LENGTH_SHORT).show();
                    txt_Password.requestFocus();
                    return;
                }
                Toast.makeText(getApplicationContext(), "kết nối wifi", Toast.LENGTH_SHORT).show();
            }
        });

        //scan wifi
        // Khởi tạo WifiManager và TextView
        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);

        // Kiểm tra xem Wi-Fi có được bật hay không
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true); // Bật Wi-Fi nếu chưa bật
        }

        // Quét Wi-Fi
        //scanWifiNetworks();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Kiểm tra quyền truy cập vị trí
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQUEST_CODE);
            } else {
                scanWifiNetworks(); // Quét mạng Wi-Fi nếu đã có quyền
            }
        } else {
            scanWifiNetworks(); // Trực tiếp quét Wi-Fi nếu hệ điều hành cũ hơn
        }    }

    private void scanWifiNetworks() {
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }

        boolean success = wifiManager.startScan();
        if (success) {
            @SuppressLint("MissingPermission") List<ScanResult> scanResults = wifiManager.getScanResults();
            updateWifiSpinner(scanResults);
        } else {
            Toast.makeText(this, "Quét Wi-Fi thất bại", Toast.LENGTH_SHORT).show();
        }
    }
    // Cập nhật danh sách các SSID vào Spinner
    private void updateWifiSpinner(List<ScanResult> scanResults) {
        String[] wifiList = new String[scanResults.size()];
        for (int i = 0; i < scanResults.size(); i++) {
            wifiList[i] = scanResults.get(i).SSID;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, wifiList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        wifi_Spinner.setAdapter(adapter);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                scanWifiNetworks(); // Quét Wi-Fi nếu quyền được cấp
            } else {
                Toast.makeText(this, "Cần quyền truy cập vị trí để quét Wi-Fi", Toast.LENGTH_SHORT).show();
            }
        }
    }
}