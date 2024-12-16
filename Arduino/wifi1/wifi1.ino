#include <WiFi.h>
#include <HTTPClient.h>

// Thông tin kết nối Wi-Fi
const char* ssid = "THANH LOC_vnpt";
const char* password = "244466666";

// Địa chỉ server
const char* serverURL = "http://192.168.1.8/phdata";  // Địa chỉ của server API

// Hàm kết nối Wi-Fi
void connectToWiFi() {
  Serial.begin(115200);
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.println("Connecting to WiFi...");
  }
  Serial.println("Connected to WiFi");
}

void setup() {
  // Kết nối Wi-Fi khi bắt đầu
  connectToWiFi();
}

void loop() {
  // Đọc giá trị từ cảm biến (giả sử là giá trị pH)
  String phValue = "7.0";  // Giả sử chúng ta gửi giá trị pH là 7.0

  // Gửi dữ liệu lên server
  sendDataToServer(phValue);

  // Đợi một khoảng thời gian (ví dụ: 5 giây)
  delay(5000);  // Chờ 5 giây trước khi gửi dữ liệu lần tiếp theo
}

void sendDataToServer(String data) {
  if (WiFi.status() == WL_CONNECTED) {
    HTTPClient http;
    http.begin(serverURL);  // Bắt đầu kết nối HTTP đến server

    // Cấu hình HTTP request
    http.addHeader("Content-Type", "application/x-www-form-urlencoded");  // Định dạng gửi dữ liệu
    String postData = "phValue=" + data;  // Dữ liệu gửi lên

    // Gửi HTTP POST request
    int httpResponseCode = http.POST(postData);

    if (httpResponseCode > 0) {
      Serial.println("Data sent successfully");
      Serial.println("HTTP Response code: " + String(httpResponseCode));
    } else {
      Serial.println("Error sending data");
    }

    // Kết thúc kết nối
    http.end();
  } else {
    Serial.println("WiFi not connected");
  }
}