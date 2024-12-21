package com.example.seecareapp;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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

import java.io.IOException;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WifiActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 5;
    private WifiManager wifiManager;
    private ImageButton btn_Back;
    private Button btn_Connect;
    private Spinner wifi_Spinner;
    private EditText txt_Password;

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

        btn_Back.setOnClickListener(view -> finish());

        btn_Connect.setOnClickListener(view -> {
            String inputText = txt_Password.getText().toString().trim();
            if (inputText.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Vui lòng nhập password", Toast.LENGTH_SHORT).show();
                txt_Password.requestFocus();
                return;
            }

            String ssid = wifi_Spinner.getSelectedItem().toString();
            sendWifiConfigToESP32(ssid, inputText);
        });

        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);

        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQUEST_CODE);
            } else {
                scanWifiNetworks();
            }
        } else {
            scanWifiNetworks();
        }
    }

    private void scanWifiNetworks() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 trở lên
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_WIFI_STATE,
                            android.Manifest.permission.NEARBY_WIFI_DEVICES
                    }, PERMISSION_REQUEST_CODE);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 - 12
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_WIFI_STATE
                    }, PERMISSION_REQUEST_CODE);
        } else {
            // Dưới Android 10
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_WIFI_STATE
                    }, PERMISSION_REQUEST_CODE);
        }

        // Logic quét Wi-Fi
        try {
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }

            boolean success = wifiManager.startScan();
            if (success) {
                List<ScanResult> scanResults = wifiManager.getScanResults();
                if (scanResults != null && !scanResults.isEmpty()) {
                    updateWifiSpinner(scanResults);
                } else {
                    Log.e("WifiScanError", "Không tìm thấy mạng Wi-Fi nào");
                    Toast.makeText(this, "Không tìm thấy mạng Wi-Fi nào", Toast.LENGTH_SHORT).show();
                }
            } else {
//                Log.e("WifiScanError", "Quét Wi-Fi thất bại");
//                Toast.makeText(this, "Quét Wi-Fi thất bại", Toast.LENGTH_SHORT).show();
            }
        } catch (SecurityException e) {
            Log.e("PermissionError", "Yêu cầu quyền để truy cập kết quả quét Wi-Fi", e);
            Toast.makeText(this, "Không có quyền truy cập kết quả quét Wi-Fi", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateWifiSpinner(List<ScanResult> scanResults) {
        String[] wifiList = new String[scanResults.size()];
        for (int i = 0; i < scanResults.size(); i++) {
            wifiList[i] = scanResults.get(i).SSID;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, wifiList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        wifi_Spinner.setAdapter(adapter);
    }

    private void sendWifiConfigToESP32(String ssid, String password) {
        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("ssid", ssid)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url("http://<ESP32_IP_ADDRESS>/set_wifi") // Thay <ESP32_IP_ADDRESS> bằng IP của ESP32
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(WifiActivity.this, "Lỗi kết nối đến ESP32", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(WifiActivity.this, "Kết nối Wi-Fi thành công", Toast.LENGTH_SHORT).show());
                } else {
                    runOnUiThread(() -> Toast.makeText(WifiActivity.this, "Lỗi khi gửi thông tin Wi-Fi", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == PERMISSION_REQUEST_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                scanWifiNetworks();
//            } else {
//                Toast.makeText(this, "Cần quyền truy cập vị trí để quét Wi-Fi", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
}