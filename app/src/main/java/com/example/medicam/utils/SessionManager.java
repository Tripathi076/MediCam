package com.example.medicam.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * SessionManager handles secure user session and credential storage
 * using Android's EncryptedSharedPreferences for security
 */
public class SessionManager {
    private static final String PREFS_NAME = "medicam_session";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_PHONE = "user_phone";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_AUTH_TOKEN = "auth_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_ABHA_NUMBER = "abha_number";
    private static final String KEY_SESSION_TIMESTAMP = "session_timestamp";
    private static final String KEY_LAST_ACTIVE_TIME = "last_active_time";
    private static final String KEY_BIOMETRIC_ENABLED = "biometric_enabled";
    private static final String KEY_AUTO_LOCK_ENABLED = "auto_lock_enabled";
    
    // Session timeout durations
    private static final long SESSION_TIMEOUT_MS = 24 * 60 * 60 * 1000L; // 24 hours
    private static final long AUTO_LOCK_TIMEOUT_MS = 5 * 60 * 1000L; // 5 minutes of inactivity

    private SharedPreferences encryptedSharedPreferences;
    private static SessionManager instance;

    private SessionManager(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            encryptedSharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    PREFS_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            throw new SessionInitializationException("Failed to initialize encrypted shared preferences", e);
        }
    }

    /**
     * Custom exception for session initialization failures
     */
    public static class SessionInitializationException extends RuntimeException {
        public SessionInitializationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Save user session after successful login
     */
    public void saveUserSession(String userId, String email, String phone, String name) {
        SharedPreferences.Editor editor = encryptedSharedPreferences.edit();
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_PHONE, phone);
        editor.putString(KEY_USER_NAME, name);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putLong(KEY_SESSION_TIMESTAMP, System.currentTimeMillis());
        editor.apply();
    }

    /**
     * Save authentication tokens
     */
    public void saveAuthTokens(String authToken, String refreshToken) {
        SharedPreferences.Editor editor = encryptedSharedPreferences.edit();
        editor.putString(KEY_AUTH_TOKEN, authToken);
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.apply();
    }

    /**
     * Save ABHA number for health integration
     */
    public void saveABHANumber(String abhaNumber) {
        SharedPreferences.Editor editor = encryptedSharedPreferences.edit();
        editor.putString(KEY_ABHA_NUMBER, abhaNumber);
        editor.apply();
    }

    /**
     * Get user ID
     */
    public String getUserId() {
        return encryptedSharedPreferences.getString(KEY_USER_ID, null);
    }

    /**
     * Get user email
     */
    public String getUserEmail() {
        return encryptedSharedPreferences.getString(KEY_USER_EMAIL, null);
    }

    /**
     * Get user phone
     */
    public String getUserPhone() {
        return encryptedSharedPreferences.getString(KEY_USER_PHONE, null);
    }

    /**
     * Get user name
     */
    public String getUserName() {
        return encryptedSharedPreferences.getString(KEY_USER_NAME, null);
    }

    /**
     * Get auth token
     */
    public String getAuthToken() {
        return encryptedSharedPreferences.getString(KEY_AUTH_TOKEN, null);
    }

    /**
     * Get refresh token
     */
    public String getRefreshToken() {
        return encryptedSharedPreferences.getString(KEY_REFRESH_TOKEN, null);
    }

    /**
     * Get ABHA number
     */
    public String getABHANumber() {
        return encryptedSharedPreferences.getString(KEY_ABHA_NUMBER, null);
    }

    /**
     * Check if user is logged in
     */
    public boolean isUserLoggedIn() {
        return encryptedSharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Get session timestamp
     */
    public long getSessionTimestamp() {
        return encryptedSharedPreferences.getLong(KEY_SESSION_TIMESTAMP, 0);
    }

    /**
     * Clear all session data (logout)
     */
    public void clearSession() {
        SharedPreferences.Editor editor = encryptedSharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * Check if session is still valid (24 hours)
     */
    public boolean isSessionValid() {
        long sessionTime = getSessionTimestamp();
        long currentTime = System.currentTimeMillis();
        return (currentTime - sessionTime) < SESSION_TIMEOUT_MS;
    }
    
    /**
     * Update last active time (call this when user interacts with app)
     */
    public void updateLastActiveTime() {
        SharedPreferences.Editor editor = encryptedSharedPreferences.edit();
        editor.putLong(KEY_LAST_ACTIVE_TIME, System.currentTimeMillis());
        editor.apply();
    }
    
    /**
     * Get last active time
     */
    public long getLastActiveTime() {
        return encryptedSharedPreferences.getLong(KEY_LAST_ACTIVE_TIME, System.currentTimeMillis());
    }
    
    /**
     * Check if app should be locked due to inactivity
     * @return true if app was inactive for more than 5 minutes
     */
    public boolean shouldAutoLock() {
        if (!isAutoLockEnabled()) return false;
        
        long lastActive = getLastActiveTime();
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastActive) > AUTO_LOCK_TIMEOUT_MS;
    }
    
    /**
     * Enable/disable biometric authentication
     */
    public void setBiometricEnabled(boolean enabled) {
        SharedPreferences.Editor editor = encryptedSharedPreferences.edit();
        editor.putBoolean(KEY_BIOMETRIC_ENABLED, enabled);
        editor.apply();
    }
    
    /**
     * Check if biometric authentication is enabled
     */
    public boolean isBiometricEnabled() {
        return encryptedSharedPreferences.getBoolean(KEY_BIOMETRIC_ENABLED, false);
    }
    
    /**
     * Enable/disable auto-lock on inactivity
     */
    public void setAutoLockEnabled(boolean enabled) {
        SharedPreferences.Editor editor = encryptedSharedPreferences.edit();
        editor.putBoolean(KEY_AUTO_LOCK_ENABLED, enabled);
        editor.apply();
    }
    
    /**
     * Check if auto-lock is enabled
     */
    public boolean isAutoLockEnabled() {
        return encryptedSharedPreferences.getBoolean(KEY_AUTO_LOCK_ENABLED, true); // Default: enabled
    }
}
