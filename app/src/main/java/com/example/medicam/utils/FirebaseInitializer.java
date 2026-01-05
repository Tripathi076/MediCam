package com.example.medicam.utils;

import android.util.Log;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Handles Firebase initialization and configuration
 */
public class FirebaseInitializer {
    private static final String TAG = "FirebaseInitializer";
    private static boolean isInitialized = false;

    /**
     * Initialize Firebase with required settings
     * Call this once in your Application class or MainActivity onCreate
     */
    public static void initialize() {
        if (isInitialized) {
            return; // Already initialized
        }

        try {
            // Enable Firebase Realtime Database offline persistence
            // This allows the app to work offline and sync when reconnected
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            
            // Note: Realtime Database uses default 10MB cache
            // For larger cache, use Firestore or Cloud Storage
            
            // Keep connections alive for realtime updates
            FirebaseDatabase.getInstance().goOnline();
            
            Log.d(TAG, "Firebase initialized successfully");
            isInitialized = true;
        } catch (Exception e) {
            Log.e(TAG, "Error initializing Firebase: " + e.getMessage(), e);
        }
    }

    /**
     * Check if Firebase is initialized
     */
    public static boolean isInitialized() {
        return isInitialized;
    }

    /**
     * Force offline mode (useful for testing)
     */
    public static void goOffline() {
        try {
            FirebaseDatabase.getInstance().goOffline();
            Log.d(TAG, "Firebase went offline");
        } catch (Exception e) {
            Log.e(TAG, "Error going offline: " + e.getMessage(), e);
        }
    }

    /**
     * Force online mode
     */
    public static void goOnline() {
        try {
            FirebaseDatabase.getInstance().goOnline();
            Log.d(TAG, "Firebase went online");
        } catch (Exception e) {
            Log.e(TAG, "Error going online: " + e.getMessage(), e);
        }
    }
}
