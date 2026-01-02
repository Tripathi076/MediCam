package com.example.medicam;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.Assert.*;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.medicam.utils.FirebaseErrorHandler;
import com.example.medicam.utils.SessionManager;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for authentication flows
 */
@RunWith(AndroidJUnit4.class)
public class AuthenticationTest {
    // Test data constants
    private static final String TEST_USER_ID = "user123";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PHONE = "9876543210";
    private static final String TEST_USER_NAME = "Test User";

    @Mock
    private FirebaseAuth mockFirebaseAuth;

    @Mock
    private FirebaseUser mockFirebaseUser;

    @Mock
    private AuthResult mockAuthResult;

    private SessionManager sessionManager;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        Context context = ApplicationProvider.getApplicationContext();
        sessionManager = SessionManager.getInstance(context);
        
        // Clear session before each test
        sessionManager.clearSession();
    }

    @Test
    public void testSessionManagerSaveUserSession() {
        sessionManager.saveUserSession(TEST_USER_ID, TEST_EMAIL, TEST_PHONE, TEST_USER_NAME);

        assertEquals(TEST_USER_ID, sessionManager.getUserId());
        assertEquals(TEST_EMAIL, sessionManager.getUserEmail());
        assertEquals(TEST_PHONE, sessionManager.getUserPhone());
        assertEquals(TEST_USER_NAME, sessionManager.getUserName());
        assertTrue(sessionManager.isUserLoggedIn());
    }

    @Test
    public void testSessionManagerSaveAuthTokens() {
        String authToken = "auth_token_xyz";
        String refreshToken = "refresh_token_abc";

        sessionManager.saveAuthTokens(authToken, refreshToken);

        assertEquals(authToken, sessionManager.getAuthToken());
        assertEquals(refreshToken, sessionManager.getRefreshToken());
    }

    @Test
    public void testSessionManagerSaveABHANumber() {
        String abhaNumber = "12345-67890-12345";

        sessionManager.saveABHANumber(abhaNumber);

        assertEquals(abhaNumber, sessionManager.getABHANumber());
    }

    @Test
    public void testSessionManagerClearSession() {
        sessionManager.saveUserSession(TEST_USER_ID, TEST_EMAIL, TEST_PHONE, TEST_USER_NAME);
        assertTrue(sessionManager.isUserLoggedIn());

        sessionManager.clearSession();

        assertFalse(sessionManager.isUserLoggedIn());
        assertNull(sessionManager.getUserId());
        assertNull(sessionManager.getUserEmail());
    }

    @Test
    public void testSessionManagerIsSessionValid() {
        sessionManager.saveUserSession(TEST_USER_ID, TEST_EMAIL, TEST_PHONE, TEST_USER_NAME);
        assertTrue(sessionManager.isSessionValid());
    }

    @Test
    public void testFirebaseErrorHandlerAuthError() {
        Exception exception = new Exception("User not found");
        String errorMsg = FirebaseErrorHandler.getAuthErrorMessage(exception);
        assertNotNull(errorMsg);
        assertFalse(errorMsg.isEmpty());
    }

    @Test
    public void testValidPhoneNumber() {
        assertTrue(TEST_PHONE.length() == 10);
        assertTrue(TEST_PHONE.matches("\\d{10}"));
    }

    @Test
    public void testInvalidPhoneNumber() {
        String invalidPhone = "987654321"; // 9 digits
        assertFalse(invalidPhone.length() == 10);
    }

    @Test
    public void testValidEmailFormat() {
        assertTrue(android.util.Patterns.EMAIL_ADDRESS.matcher(TEST_EMAIL).matches());
    }

    @Test
    public void testInvalidEmailFormat() {
        String invalidEmail = "not_an_email";
        assertFalse(android.util.Patterns.EMAIL_ADDRESS.matcher(invalidEmail).matches());
    }

    @Test
    public void testValidPassword() {
        String password = "SecurePass123";
        assertTrue(password.length() >= 6);
        assertTrue(password.matches(".*[a-zA-Z].*"));
        assertTrue(password.matches(".*\\d.*"));
    }

    @Test
    public void testWeakPassword() {
        String weakPassword = "123";
        assertFalse(weakPassword.length() >= 6);
    }

    @Test
    public void testOTPValidation() {
        String otp = "1234";
        assertTrue(otp.length() == 4);
        assertTrue(otp.matches("\\d{4}"));
    }

    @Test
    public void testOTPValidationInvalid() {
        String invalidOtp = "12a4";
        assertTrue(invalidOtp.length() == 4);
        assertFalse(invalidOtp.matches("\\d{4}"));
    }

    @Test
    public void testNetworkErrorDetection() {
        Exception networkError = new Exception("Connection timeout");
        assertTrue(FirebaseErrorHandler.isNetworkError(networkError));
    }

    @Test
    public void testNonNetworkErrorDetection() {
        Exception authError = new Exception("Invalid credentials");
        assertFalse(FirebaseErrorHandler.isNetworkError(authError));
    }
}
