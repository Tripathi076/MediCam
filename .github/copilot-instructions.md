# MediCam - AI Coding Instructions

## Project Overview
MediCam is an Android medical records management app (Java, Gradle) with Firebase backend. It manages pathology/radiology reports, BMI tracking, ABHA (Ayushman Bharat Health Account) registration, and device connectivity.

## Architecture

### Module Structure
The app follows an **Activity-based architecture** with 5 feature modules sharing common utilities:
- **Authentication**: `PhoneLoginActivity` → `OTPVerificationActivity` → `DashboardActivity` (Firebase Phone Auth)
- **Pathology**: `PathologyActivity` → `UploadPathologyReportActivity` → `SelectReportSourceActivity` → `ReportPreviewActivity`
- **Radiology**: Mirrors pathology flow with `Radiology*` activities
- **BMI**: `BMIActivity` → `BMIGenderActivity` → `BMIInputActivity` → `BMIResultActivity`
- **ABHA**: Multi-step Aadhaar-based registration flow (`Abha*Activity` classes)

### Key Patterns
- **Singleton SessionManager** (`utils/SessionManager.java`): Uses `EncryptedSharedPreferences` for secure credential storage
- **Firebase Error Handling** (`utils/FirebaseErrorHandler.java`): Centralized user-friendly error messages
- **Local Storage**: Reports stored in SharedPreferences with Gson serialization (see `PathologyActivity.loadReports()`)
- **Bottom Navigation**: Implemented in each activity via `setupBottomNavigation()` method

## Code Conventions

### Activity Structure
Every activity follows this pattern:
```java
public class FeatureActivity extends AppCompatActivity {
    private static final String TAG = "FeatureActivity";  // For logging
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feature);
        // 1. Initialize Firebase/SessionManager
        // 2. Verify auth state
        // 3. Setup views and listeners
        // 4. Setup bottom navigation
    }
    
    private void setupBottomNavigation() { /* Standard nav to 5 modules */ }
}
```

### Error Handling
Always use `FirebaseErrorHandler` for Firebase operations:
```java
} catch (Exception e) {
    FirebaseErrorHandler.logException("methodName", e);
    Toast.makeText(this, FirebaseErrorHandler.getAuthErrorMessage(e), Toast.LENGTH_SHORT).show();
}
```

### Intent Navigation
Use explicit flags when navigating to prevent back-stack issues:
```java
Intent intent = new Intent(CurrentActivity.this, TargetActivity.class);
intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
startActivity(intent);
finish();
```

## Build & Development

### Requirements
- Android SDK 34 (target), min SDK 24
- Java 11
- Firebase project with `google-services.json` in `app/`

### Commands
```bash
# Build debug APK
./gradlew assembleDebug

# Run tests
./gradlew test

# Clean build
./gradlew clean assembleDebug
```

### Dependencies (from `gradle/libs.versions.toml`)
- Firebase BOM 32.7.0 (Auth, Firestore, Storage)
- Material Design 1.10.0
- AndroidX Security Crypto 1.1.0-alpha06 (EncryptedSharedPreferences)
- Gson 2.10.1

## File Organization
```
app/src/main/java/com/example/medicam/
├── *Activity.java          # 41 Activity classes (flat structure)
├── PathologyReport.java    # Data model
├── PathologyReportAdapter.java  # RecyclerView adapter
└── utils/
    ├── SessionManager.java      # Secure session storage (Singleton)
    └── FirebaseErrorHandler.java # Error message utility
```

## Important Notes
- All activities are in a **flat package** structure (`com.example.medicam`)
- Reports use **Gson + SharedPreferences** (not Firestore) for local storage
- Camera/file permissions handled via `ActivityResultContracts`
- `FileProvider` configured for secure file sharing (`com.example.medicam.fileprovider`)
- UML documentation available in `docs/UML_Design.md`
