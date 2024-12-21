package com.example.seecareapp;



import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SettingActivity extends AppCompatActivity {
    ImageButton btn_Back;
    private WifiManager wifiManager;
    private ListView listView;
    private List<String> wifiList;
    private ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btn_Back = findViewById(R.id.btn_Back);
        btn_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        listView = findViewById(R.id.wifiListView);
        wifiList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, wifiList);
        listView.setAdapter(adapter);

        // Quét Wi-Fi
        scanWiFi();

        // Xử lý khi người dùng chọn một SSID
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedSSID = wifiList.get(position);
            EditText passwordInput = findViewById(R.id.passwordInput);
            passwordInput.setVisibility(EditText.VISIBLE); // Hiển thị trường nhập mật khẩu
            Button connectButton = findViewById(R.id.connectButton);
            connectButton.setOnClickListener(v -> {
                String password = passwordInput.getText().toString();
                sendWiFiInfoToESP32(selectedSSID, password);  // Gửi SSID và password đến ESP32
            });
        });
    }

    private void scanWiFi() {
        wifiManager.startScan();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        List<android.net.wifi.ScanResult> scanResults = wifiManager.getScanResults();
        wifiList.clear();
        for (android.net.wifi.ScanResult scanResult : scanResults) {
            wifiList.add(scanResult.SSID);
        }
        adapter.notifyDataSetChanged();
    }

    private void sendWiFiInfoToESP32(String ssid, String password) {
        // URL của ESP32 (giả sử ESP32 có IP là 192.168.4.1 và endpoint là /setwifi)
        String url = "http://192.168.4.1/setwifi?ssid=" + ssid + "&password=" + password;

        // Sử dụng thư viện OkHttp để gửi HTTP Request (có thể thay thế bằng thư viện khác nếu cần)
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(SettingActivity.this, "Thông tin Wi-Fi đã được gửi", Toast.LENGTH_SHORT).show());
                } else {
                    runOnUiThread(() -> Toast.makeText(SettingActivity.this, "Lỗi gửi thông tin Wi-Fi", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(SettingActivity.this, "Lỗi kết nối đến ESP32", Toast.LENGTH_SHORT).show());
            }
        });
    }
}