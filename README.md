# 🌧️ Rain Detector Cloth Collector

![Rain Detector Dashboard](https://img.shields.io/badge/Status-Active-brightgreen.svg)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.1-6DB33F?style=flat&logo=spring&logoColor=white)
![React](https://img.shields.io/badge/React-18-61DAFB?style=flat&logo=react&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/Supabase_PostgreSQL-336791?style=flat&logo=postgresql&logoColor=white)
![AWS EC2](https://img.shields.io/badge/AWS_EC2-Deployed-FF9900?style=flat&logo=amazon-aws&logoColor=white)

An automated, full-stack IoT Simulation system designed to monitor weather conditions and autonomously retract hanging clothes indoors when rain is detected.

## 🚀 Features
- **Real-Time Weather Response**: Uses an internal state machine simulator to instantly react to rain detection events.
- **WebSocket Dashboard**: A beautiful, responsive React frontend that streams live motor states and system events without page reloads.
- **Dual-Mode Operation**: 
  - `AUTO`: Relies on sensors to automatically trigger the retraction motor when raining.
  - `MANUAL`: Allows the user to remotely extend/retract the clothes rack overriding the sensors.
- **Persistent Audit Log**: Every motor action and weather event is permanently logged into a PostgreSQL database for historical tracking.

## 🔌 Hardware Integration (Building the Physical Device)
If you want to move beyond the software simulation and build the actual physical device, you will need the following components:

### Required Components
1. **Microcontroller**: ESP32 or ESP8266 (Recommended due to built-in Wi-Fi).
2. **Rain Sensor**: FC-37 or YL-83 Rain Sensor Module (with LM393 comparator board).
3. **Motor**: 12V DC Stepper Motor or Servo Motor (to drive the clothes rack).
4. **Motor Driver**: L298N Motor Driver module (to control the motor's forward/reverse direction).
5. **Limit Switches (Optional but recommended)**: Two micro-switches placed at the ends of the rack to tell the motor when it has fully extended or fully retracted.

### Wiring Guide
- Connect the **Rain Sensor Analog Pin (A0)** to the ESP32 ADC pin.
- Connect the **ESP32 Digital Output Pins** to the **IN1, IN2, IN3, IN4** pins on the L298N Motor Driver.
- Connect the **Motor** to the Output terminals of the L298N.

### Connecting to the Spring Boot Backend
To make your physical ESP32 talk to this Spring Boot application, you simply need to write a C++ script for your ESP32 (using the Arduino IDE) that makes standard HTTP POST requests to your EC2 server.

**Example ESP32 Logic:**
```cpp
// 1. Read the Rain Sensor
int rainValue = analogRead(RAIN_SENSOR_PIN);

// 2. If rain is detected, alert the backend via REST API
if (rainValue < THRESHOLD) {
   http.begin("http://[YOUR-EC2-IP]/api/events");
   http.addHeader("Content-Type", "application/json");
   // Send POST request indicating Rain
   http.POST("{\"eventType\":\"RAIN_STARTED\", \"deviceId\":1}"); 
}
```
*Note: Your Spring Boot backend is already built to receive these REST API calls! When the ESP32 sends the HTTP POST request, the backend will automatically process it, change the system state, and broadcast the WebSocket update to your React frontend!*

## 🏗️ Architecture Stack
- **Frontend**: React + Vite, Tailwind CSS, Lucide Icons, Axios, STOMP.js
- **Backend**: Java 17, Spring Boot 3, Spring Data JPA, Spring WebSockets
- **Database**: Supabase (PostgreSQL Connection Pooler)
- **Deployment**: AWS EC2 (Ubuntu 24.04), Nginx Reverse Proxy, Systemd Background Service

## ⚙️ Running Locally

### 1. Backend Setup
```bash
cd backend
# Edit src/main/resources/application.properties to point to your local MySQL/PostgreSQL
./mvnw spring-boot:run
```

### 2. Frontend Setup
```bash
cd frontend
npm install
npm run dev
```

## ☁️ Production Deployment (AWS EC2)
A fully automated deployment script is provided in the `aws-deployment` folder.
1. Provision an Ubuntu EC2 instance and open Ports 22 and 80.
2. Upload the `deploy.sh` script, your built `.jar` file, and the React `dist` folder to the server.
3. Run `./deploy.sh` to install Java 17 and Nginx automatically.
4. Move the custom `nginx.conf` into place to serve the React app and reverse-proxy the WebSocket traffic.

---
*Developed as a full-stack real-time IoT application.*
