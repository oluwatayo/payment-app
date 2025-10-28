# Cashi

A Kotlin Multiplatform Mobile (KMM) sample implementing a Payments flow with API calls via **Ktor** and JSON parsing via **kotlinx.serialization**. Includes JMeter load testing for **5 concurrent users** on `/payments` and unit tests verifying **API response parsing in the shared module**.

## App Demo Recording
https://github.com/oluwatayo/payment-app/blob/main/Screen_recording_20251029_000739.mp4?raw=true

## Tech Stack
- **KMM**: shared code in Kotlin across Android/iOS
- **Ktor Client**: HTTP, with platform engines (OkHttp on Android, Darwin on iOS)
- **kotlinx.serialization**: JSON (`ignoreUnknownKeys = true`)
- **Ktor MockEngine** in tests
- **JMeter 5.6.x** for load testing
- **JSON Server** as a mock backend

## App Architecture
- The **shared module** contains core logic including network calls, models, validation, and business rules using **Ktor** and **kotlinx.serialization**.
- The **shared module** uses **Jetpack Compose** for UI for both android and iOS.
- The **expect/actual** pattern is used for platform-specific logic, such as HTTP client engines (OkHttp for Android, Darwin for iOS).
- The **testing approach** uses **Ktor MockEngine** for unit tests in the shared module to validate parsing and network logic.

```
mermaid
graph LR
    Android[Android (Jetpack Compose)]
    iOS[iOS (SwiftUI / Native UI)]
    Shared[Shared Module (KMM)]
    Backend[Mock Backend (JSON Server)]
    Android --> Shared
    iOS --> Shared
    Shared --> Backend
```

## Project Layout (detected)
**Modules / Gradle files**
- `Cashi/build.gradle.kts`
- `Cashi/composeApp/build.gradle.kts`
- `Cashi/settings.gradle.kts`

**tests**
- `Cashi/composeApp/src/jvmTest/kotlin/com/example/cashi/appium/PaymentFlowTest.kt`
- `Cashi/composeApp/src/jvmTest/kotlin/com/example/cashi/data/network/KtorHttpClientTestConfig.kt`
- `Cashi/composeApp/src/jvmTest/kotlin/com/example/cashi/data/network/PaymentServiceTest.kt`
- `Cashi/composeApp/src/jvmTest/kotlin/com/example/cashi/test/TransactionRepositoryImplTest.kt`
- `Cashi/composeApp/src/jvmTest/kotlin/com/example/cashi/test/ValidatorTest.kt`
- `Cashi/composeApp/src/jvmTest/kotlin/com/example/cashi/bdd/Cucumber/CucumberLauncher.kt`
- `Cashi/composeApp/src/jvmTest/kotlin/com/example/cashi/appium/PaymentFlowTest.kt`

## Local Development

### 1) Start JSON Server (mock API)
In the `json-server/` package
```bash
npm install -g json-server
npm start
```
The API base URL will be `http://localhost:3000` and `POST /payments` creates a payment.

### 2) Run the app / shared tests (KMM)
```bash
./gradlew assembleDebug   
./gradlew :composeApp:jvmTest    # runs all jvm tests
```

### 3) Load Test with JMeter (5 concurrent users)
This repo includes a JMeter plan:
```bash
jmeter -n -t json-server/jmeter/payments_test.jmx -l results.jtl -e -o report
# then open ./report/index.html
```
Assertions expect HTTP **201** and record latency percentiles.


## API Contract (mock)
**POST** `/payments`  
**Request**:
```json
{
  "recipientEmail": "alice@example.com",
  "amount": 12.50,
  "currency": "USD"
}
```
**Success 201**
```json
{
  "success": true,
  "message": "Payment processed successfully",
  "payment": {
    "id": "pay_123",
    "transactionId": "txn_001",
    "recipientEmail": "alice@example.com",
    "amount": 12.5,
    "currency": "USD",
    "status": "SUCCESS",
    "timestamp": "2025-10-28T10:00:00Z"
  }
}
```
**Error 400**
```json
{
  "success": false,
  "error": "Amount must be a positive number",
  "code": "INVALID_AMOUNT"
}
```

## How to Run Parsing Tests Only
Parsing tests use Ktor **MockEngine**. Example:
```bash
./gradlew :composeApp:jvmTest --tests "com.example.cashi.data.network.PaymentServiceTest"
```

## Setup (Android Studio + Firebase)

### **Prerequisites**
- **Android Studio** (latest stable recommended)
- **JDK 17**
- **Gradle Wrapper** (bundled; use `./gradlew` in project root)

---

### **1) Open & run in Android Studio (Android app)**
1. **Clone this repository**  
   ```bash
   git clone <repo-url>
   cd payment-solution
   ```
2. **Open the project** in Android Studio (`File > Open...` and select the root folder).
3. **Sync Gradle** if prompted (or via `File > Sync Project with Gradle Files`).
4. **Select a device or emulator** in the toolbar.
5. **Run** the app (`Run > Run 'app'` or green triangle).

   *Optional: To use the mock API, start JSON Server as described above. If running on an emulator, use `10.0.2.2:3000` as the base URL instead of `localhost:3000`.*

---

### **2) Firebase setup (Analytics/Crashlytics/etc. on Android)**
To enable Firebase services (Analytics, Crashlytics, etc.):

1. **Create a Firebase project**  
   - Go to [Firebase Console](https://console.firebase.google.com/) and create a new project (or use an existing one).
2. **Register your Android app**  
   - Enter your app's package name (e.g., `com.example.cashi`).
   - Download the generated `google-services.json` file.
3. **Place `google-services.json`**  
   - Move the file to:  
     ```
     Cashi/composeApp/src/androidMain/google-services.json
     ```
4. **Enable the Google Services plugin**  
   - In `Cashi/composeApp/build.gradle.kts`, ensure you have:
     ```kotlin
     plugins {
         id("com.google.gms.google-services")
     }
     ```
   - And at the root `Cashi/build.gradle.kts`:
     ```kotlin
     dependencies {
         classpath("com.google.gms:google-services:4.3.15") // or latest
     }
     ```
5. **Add Firebase dependencies using the BOM**  
   - In your `composeApp/build.gradle.kts`:
     ```kotlin
     dependencies {
         implementation(platform("com.google.firebase:firebase-bom:32.7.4")) // or latest
         implementation("com.google.firebase:firebase-analytics-ktx")
         implementation("com.google.firebase:firebase-crashlytics-ktx")
         // Add other Firebase libraries as needed
     }
     ```
6. **Sync the project**  
   - Click "Sync Now" when prompted, or use `File > Sync Project with Gradle Files`.

---

