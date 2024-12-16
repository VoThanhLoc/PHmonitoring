int phPin = A0;  // Chân analog kết nối với cảm biến pH
int phValue = 0;  // Biến lưu giá trị đọc từ cảm biến

void setup() {
  Serial.begin(9600);  // Khởi tạo Serial Monitor để debug
}

void loop() {
  phValue = analogRead(phPin);  // Đọc giá trị analog từ cảm biến pH
  Serial.println(phValue);  // In giá trị ra màn hình Serial Monitor
  delay(1000);  // Chờ 1 giây trước khi đọc lại
}