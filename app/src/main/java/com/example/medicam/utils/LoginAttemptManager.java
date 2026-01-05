package com.example.medicam.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Manages login attempt limiting to prevent brute force attacks
 */
public class LoginAttemptManager {
    
    private static final String PREFS_NAME = "login_attempts";
    private static final String KEY_ATTEMPT_COUNT = "attempt_count_";
    private static final String KEY_LOCKOUT_TIME = "lockout_time_";
    
    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION_MS = 5 * 60 * 1000; // 5 minutes
    private static final long ATTEMPT_RESET_DURATION_MS = 15 * 60 * 1000; // 15 minutes
    
    private final SharedPreferences prefs;
    
    private static LoginAttemptManager instance;
    
    private LoginAttemptManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    public static synchronized LoginAttemptManager getInstance(Context context) {
        if (instance == null) {
            instance = new LoginAttemptManager(context);
        }
        return instance;
    }
    
    /**
     * Check if the account is currently locked out
     * @param identifier Phone number or user ID
     * @return true if locked out, false if can attempt login
     */
    public boolean isLockedOut(String identifier) {
        long lockoutTime = prefs.getLong(KEY_LOCKOUT_TIME + identifier, 0);
        if (lockoutTime > 0) {
            long currentTime = System.currentTimeMillis();
            if (currentTime < lockoutTime + LOCKOUT_DURATION_MS) {
                return true; // Still locked out
            } else {
                // Lockout expired, reset
                resetAttempts(identifier);
                return false;
            }
        }
        return false;
    }
    
    /**
     * Get remaining lockout time in seconds
     * @param identifier Phone number or user ID
     * @return Remaining seconds, or 0 if not locked
     */
    public int getRemainingLockoutSeconds(String identifier) {
        long lockoutTime = prefs.getLong(KEY_LOCKOUT_TIME + identifier, 0);
        if (lockoutTime > 0) {
            long currentTime = System.currentTimeMillis();
            long remaining = (lockoutTime + LOCKOUT_DURATION_MS) - currentTime;
            if (remaining > 0) {
                return (int) (remaining / 1000);
            }
        }
        return 0;
    }
    
    /**
     * Record a failed login attempt
     * @param identifier Phone number or user ID
     * @return true if account is now locked, false otherwise
     */
    public boolean recordFailedAttempt(String identifier) {
        int attempts = prefs.getInt(KEY_ATTEMPT_COUNT + identifier, 0);
        attempts++;
        
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_ATTEMPT_COUNT + identifier, attempts);
        
        if (attempts >= MAX_ATTEMPTS) {
            // Lock the account
            editor.putLong(KEY_LOCKOUT_TIME + identifier, System.currentTimeMillis());
            editor.apply();
            return true; // Now locked
        }
        
        editor.apply();
        return false;
    }
    
    /**
     * Get remaining attempts before lockout
     * @param identifier Phone number or user ID
     * @return Number of remaining attempts
     */
    public int getRemainingAttempts(String identifier) {
        int attempts = prefs.getInt(KEY_ATTEMPT_COUNT + identifier, 0);
        return Math.max(0, MAX_ATTEMPTS - attempts);
    }
    
    /**
     * Reset attempts after successful login
     * @param identifier Phone number or user ID
     */
    public void resetAttempts(String identifier) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_ATTEMPT_COUNT + identifier);
        editor.remove(KEY_LOCKOUT_TIME + identifier);
        editor.apply();
    }
    
    /**
     * Format lockout message for display
     * @param identifier Phone number or user ID
     * @return User-friendly lockout message
     */
    public String getLockoutMessage(String identifier) {
        int seconds = getRemainingLockoutSeconds(identifier);
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        
        if (minutes > 0) {
            return String.format("Too many failed attempts. Please try again in %d minute(s) %d second(s).", 
                    minutes, remainingSeconds);
        } else {
            return String.format("Too many failed attempts. Please try again in %d second(s).", seconds);
        }
    }
}
