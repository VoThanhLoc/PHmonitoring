#include <WiFi.h>
#include <FirebaseESP32.h>
#include <NTPClient.h>
#include <WiFiUdp.h>
#include <ArduinoJson.h>
// Thông tin WiFi
const char* ssid = "THANH LOC_vnpt";
const char* password = "244466666";

// Firebase thông tin
FirebaseConfig config;
FirebaseAuth auth;

// Định nghĩa UART
#define RXD2 16
#define TXD2 17

FirebaseData firebaseData;

// NTP Client để lấy thời gian
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "pool.ntp.org", 7 * 3600, 60000);  // GMT+7 cho Việt Nam

void setup() {
  Serial.begin(9600);                           // Kết nối với Serial Monitor
  Serial2.begin(9600, SERIAL_8N1, RXD2, TXD2);  // Kết nối UART với Arduino

  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.println("Đang kết nối WiFi...");
  }
  Serial.println("Kết nối WiFi thành công!");

  // Cấu hình Firebase
  config.host = "phmonitoring-14d16-default-rtdb.asia-southeast1.firebasedatabase.app";
  config.signer.tokens.legacy_token = "TI5VNXddsNOe2SKiaWdLBNKpLN8CKy7FI3LxP0JG";  // Thay bằng Database Secret của bạn

  // Khởi tạo Firebase
  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);

  timeClient.begin();  // Bắt đầu NTP Client
}

void loop() {
  // Cập nhật thời gian
  timeClient.update();
  String currentTime = timeClient.getFormattedTime();

  // Đọc giá trị pH từ cảm biến (giả sử giá trị pH là một số float)
  float pHValue = analogRead(A0);          // Đọc dữ liệu từ cảm biến pH (sử dụng chân analog A0)
  //pHValue = map(pHValue, 0, 1023, 0, 14);  // Chuyển đổi giá trị analog sang pH (giả sử khoảng pH là 0-14)

  // Đường dẫn Firebase, bao gồm thời gian làm key
  String firebasePath = "/ph001/" + currentTime;
  Firebase.setString(firebaseData, firebasePath, pHValue);
  // Gửi dữ liệu lên Firebase
  if (Firebase.setString(firebaseData, firebasePath, pHValue)) {
    Serial.println("Dữ liệu đã gửi thành công: " + String(pHValue));
  } else {
    Serial.println("Lỗi gửi dữ liệu: " + firebaseData.errorReason());
  }

  // Đợi một chút trước khi gửi dữ liệu tiếp theo
  delay(1000);  // Gửi mỗi phút (60 giây)
}
