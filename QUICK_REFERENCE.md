# MediCam - Quick Reference Card

## 🔐 Authentication Flows

### Phone Login Flow
```
PhoneLoginActivity
  ↓ (Firebase sends SMS OTP)
OTPVerificationActivity
  ↓ (Verify OTP)
LoginSuccessActivity → DashboardActivity
```

### Admin Login Flow
```
AdminLoginActivity
  ↓ (Firebase Email/Password)
LoginSuccessActivity → DashboardActivity
```

### Password Reset Flow
```
ForgotPasswordActivity
  ↓ (Firebase sends reset email)
ResetPasswordOTPActivity
  ↓ (Verify OTP)
CreateNewPasswordActivity
  ↓ (Update in Firebase)
PasswordResetSuccessActivity
```

---

## 💾 Session Management

```java
// Get instance
SessionManager sessionManager = SessionManager.getInstance(context);

// Save session
sessionManager.saveUserSession(userId, email, phone, name);

// Save tokens
sessionManager.saveAuthTokens(authToken, refreshToken);

// Check if logged in
if (sessionManager.isUserLoggedIn()) { }

// Check if session valid (24h)
if (sessionManager.isSessionValid()) { }

// Logout
sessionManager.clearSession();
```

---

## ⚠️ Error Handling

```java
// Get user-friendly error message
String errorMsg = FirebaseErrorHandler.getAuthErrorMessage(exception);
Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();

// Log error with context
FirebaseErrorHandler.logException("methodName", exception);

// Check if network error
if (FirebaseErrorHandler.isNetworkError(exception)) { }
```

---

## 🧪 Testing

### Run All Tests
```bash
./gradlew test                    # Unit tests
./gradlew connectedAndroidTest    # Instrumented tests
./gradlew test --tests AuthenticationTest  # Specific test
```

### Test Files
- **Unit Tests:** `app/src/test/java/com/example/medicam/AuthenticationTest.java`
- **UI Tests:** `app/src/androidTest/java/com/example/medicam/AuthenticationUITest.java`

---

## 📚 Documentation

| Document | Purpose |
|----------|---------|
| [FIREBASE_INTEGRATION_GUIDE.md](FIREBASE_INTEGRATION_GUIDE.md) | Firebase setup & usage |
| [ENHANCEMENT_SUMMARY.md](ENHANCEMENT_SUMMARY.md) | Detailed feature descriptions |
| [IMPLEMENTATION_STATUS.md](IMPLEMENTATION_STATUS.md) | Status report & quick start |

---

## 🎯 Key Classes

| Class | Location | Purpose |
|-------|----------|---------|
| SessionManager | `utils/SessionManager.java` | Secure session storage |
| FirebaseErrorHandler | `utils/FirebaseErrorHandler.java` | Error handling utility |
| PhoneLoginActivity | Activity | Phone authentication |
| AdminLoginActivity | Activity | Email/password authentication |
| OTPVerificationActivity | Activity | OTP verification |
| ForgotPasswordActivity | Activity | Password reset request |
| ResetPasswordOTPActivity | Activity | Reset OTP verification |
| CreateNewPasswordActivity | Activity | New password creation |
| DashboardActivity | Activity | Main user dashboard |

---

## 🔧 Configuration

### Firebase Setup Checklist
- [ ] Update `app/google-services.json`
- [ ] Enable Phone Authentication in Firebase
- [ ] Enable Email/Password Authentication
- [ ] Configure password reset email template
- [ ] Set up Firestore security rules
- [ ] Enable Firebase Storage (if needed)

### Dependencies Added
```gradle
implementation 'androidx.security:security-crypto:1.1.0-alpha06'
testImplementation 'org.mockito:mockito-core:5.3.1'
testImplementation 'org.mockito:mockito-inline:5.3.1'
```

---

## 📱 Input Validation

### Phone Number
- Length: 10 digits
- Format: Numbers only
- Example: `9876543210`

### Email
- Format: Standard email pattern
- Example: `user@example.com`

### Password
- Min Length: 6 characters
- Required: Letters and numbers
- Example: `SecurePass123`

### OTP
- Length: 4 digits
- Format: Numbers only
- Example: `1234`

---

## 🚀 Build & Run

```bash
# Build
./gradlew build

# Run tests
./gradlew test

# Instrumented tests
./gradlew connectedAndroidTest

# Clean build
./gradlew clean build

# Run on device
./gradlew installDebug
```

---

## 📊 Security Features

✅ Encrypted SharedPreferences (AES256-GCM)
✅ Secure token storage
✅ Session expiry (24 hours)
✅ Input validation & sanitization
✅ Safe error messages
✅ Structured logging
✅ Firebase authentication

---

## 💡 Tips & Tricks

### Check if user is logged in
```java
if (sessionManager.isUserLoggedIn()) {
    // User is authenticated
    navigateToDashboard();
} else {
    // Show login screen
    navigateToLogin();
}
```

### Verify session before operations
```java
if (sessionManager.isSessionValid()) {
    // Session is still active
    proceedWithOperation();
} else {
    // Session expired, re-authenticate
    navigateToLogin();
}
```

### Clear all data on logout
```java
// Firebase logout
mAuth.signOut();

// Clear session
sessionManager.clearSession();

// Navigate to login
navigateToLogin();
```

---

## 🐛 Troubleshooting

| Issue | Solution |
|-------|----------|
| Firebase not initializing | Check google-services.json |
| OTP not sending | Enable Phone Auth in Firebase |
| Session cleared unexpectedly | Check session expiry (24h) |
| Build fails (Java version) | Java 11 compatible (AGP 7.4.2) |
| Tests failing | Run `./gradlew clean test` |

---

## 📞 Support Resources

- [Firebase Documentation](https://firebase.google.com/docs)
- [Android Security Guide](https://developer.android.com/training/articles/security-tips)
- [Espresso Testing](https://developer.android.com/training/testing/espresso)
- [EncryptedSharedPreferences](https://developer.android.com/training/articles/security-tips#preferences)

---

**Last Updated:** December 21, 2025
**Status:** ✅ Complete
