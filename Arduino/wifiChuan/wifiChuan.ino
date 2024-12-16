#include <WiFi.h>
#include <WebServer.h>

const char* ssid = "THANH LOC_vnpt";
const char* password = "244466666";
String phValue = ""; 
WebServer server(80); // HTTP server trên cổng 80

void handlePHData() {
    server.send(200, "text/plain", phValue); // Dữ liệu trả về
}

void setup() {
    Serial.begin(115200);
     Serial2.begin(9600);
    WiFi.begin(ssid, password);

    while (WiFi.status() != WL_CONNECTED) {
        delay(1000);
        Serial.println("Connecting to WiFi...");
    }
    Serial.println("Connected to WiFi");
    Serial.print("IP Address: ");
    Serial.println(WiFi.localIP());
if (Serial2.available()) {
        phValue = Serial2.readStringUntil('\n');  // Đọc giá trị từ Arduino nếu có
        Serial.println("Received PH Value: " + phValue); // Debug giá trị nhận được
    }
    server.on("/phdata", handlePHData); // Định nghĩa endpoint
    server.begin();
}

void loop() {
    server.handleClient(); // Xử lý các request
}