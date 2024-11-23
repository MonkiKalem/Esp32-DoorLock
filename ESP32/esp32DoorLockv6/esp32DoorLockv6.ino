#include <WiFi.h>
#include <Firebase_ESP_Client.h>

// Firebase project credentials
#define FIREBASE_URL "https://esp32doorlock-7877f-default-rtdb.asia-southeast1.firebasedatabase.app/"
#define FIREBASE_API "AIzaSyBszlzpFdY-nLkJbjtczW0gZeyh_2VKF5M"

#define RELAY_PIN 26

String ssid;
String password;

// Firebase objects
FirebaseData firebaseData;
FirebaseAuth firebaseAuth;
FirebaseConfig firebaseConfig;
bool signUpOK = false;

void setup() {
  Serial.begin(115200);
  pinMode(RELAY_PIN, OUTPUT);
  digitalWrite(RELAY_PIN, HIGH); // Initial state: Relay off

  // Get Wi-Fi credentials from user
  while (ssid == "" || password == "") {
    Serial.println("Enter Wi-Fi SSID:");
    while (ssid == "") {
      if (Serial.available()) {
        ssid = Serial.readStringUntil('\n');
        ssid.trim();
      }
    }
    Serial.println("Enter Wi-Fi Password:");
    while (password == "") {
      if (Serial.available()) {
        password = Serial.readStringUntil('\n');
        password.trim();
      }
    }
    Serial.println("Connecting to Wi-Fi...");
    WiFi.begin(ssid.c_str(), password.c_str());

    unsigned long startAttemptTime = millis();
    while (WiFi.status() != WL_CONNECTED && millis() - startAttemptTime < 20000) {
      delay(500);
      Serial.print(".");
    }

    if (WiFi.status() == WL_CONNECTED) {
      Serial.println("\nConnected to Wi-Fi!");
      Serial.println("IP Address: " + WiFi.localIP().toString());
    } else {
      Serial.println("\nFailed to connect. Please re-enter Wi-Fi credentials.");
      ssid = "";
      password = "";
    }
  }

  // Configure Firebase
  firebaseConfig.database_url = FIREBASE_URL;
  firebaseConfig.api_key = FIREBASE_API;

  if(Firebase.signUp(&firebaseConfig, &firebaseAuth, "", "")){
    Serial.println("signUp OK");
    signUpOK = true;
  }else{
    Serial.printf("%s\n", firebaseConfig.signer.signupError.message.c_str());
  }

  Firebase.begin(&firebaseConfig, &firebaseAuth);
  Firebase.reconnectWiFi(true);

  // Initialize the relay state in Firebase
  if (Firebase.RTDB.setString(&firebaseData, "/relay", "close")) {
    Serial.println("Firebase initialized. Relay state set to 'close'.");
  } else {
    Serial.println("Failed to initialize Firebase: " + firebaseData.errorReason());
  }
}

void loop() {
  // Listen for relay commands in Firebase
  if (Firebase.RTDB.getString(&firebaseData, "/relay")) {
    String command = firebaseData.stringData();
    if (command == "open") {
      digitalWrite(RELAY_PIN, LOW); // Open the relay
      Serial.println("Relay opened");
    } else if (command == "close") {
      digitalWrite(RELAY_PIN, HIGH); // Close the relay
      Serial.println("Relay closed");
    }
  } else {
    Serial.println("Failed to read from Firebase: " + firebaseData.errorReason());
  }

  delay(1000); // Add a delay to prevent spamming Firebase
}
