package com.example.medicam.utils;

import android.util.Log;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.FirebaseFirestoreException;

/**
 * FirebaseErrorHandler provides user-friendly error messages for Firebase exceptions
 */
public class FirebaseErrorHandler {
    private static final String TAG = "FirebaseErrorHandler";

    // Private constructor to prevent instantiation of utility class
    private FirebaseErrorHandler() {
        throw new AssertionError("Cannot instantiate utility class");
    }

    /**
     * Get user-friendly error message from Firebase Auth exception
     */
    public static String getAuthErrorMessage(Exception e) {
        Log.e(TAG, "Auth Error: " + e.getLocalizedMessage(), e);

        if (e instanceof FirebaseAuthWeakPasswordException) {
            return "Password is too weak. Use at least 6 characters with letters and numbers.";
        } else if (e instanceof FirebaseAuthInvalidUserException) {
            return "User account not found. Please sign up first.";
        } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
            return "Invalid email or password. Please try again.";
        } else if (e instanceof FirebaseAuthUserCollisionException) {
            return "This email is already registered. Please login instead.";
        } else if (e.getLocalizedMessage() != null) {
            if (e.getLocalizedMessage().contains("password is invalid")) {
                return "Invalid password. Please try again.";
            } else if (e.getLocalizedMessage().contains("There is no user record")) {
                return "User not found. Please sign up first.";
            } else if (e.getLocalizedMessage().contains("too many requests")) {
                return "Too many login attempts. Please try again later.";
            }
        }
        return "Authentication failed. Please try again.";
    }

    /**
     * Get user-friendly error message from Firestore exception
     */
    public static String getFirestoreErrorMessage(Exception e) {
        Log.e(TAG, "Firestore Error: " + e.getLocalizedMessage(), e);

        if (e instanceof FirebaseFirestoreException) {
            FirebaseFirestoreException firebaseException = (FirebaseFirestoreException) e;
            switch (firebaseException.getCode()) {
                case NOT_FOUND:
                    return "Data not found. Please try again.";
                case PERMISSION_DENIED:
                    return "You don't have permission to access this data.";
                case UNAVAILABLE:
                    return "Service temporarily unavailable. Please try again.";
                case DEADLINE_EXCEEDED:
                    return "Request timeout. Please check your connection.";
                default:
                    return "Database error. Please try again.";
            }
        }
        return "An error occurred. Please try again.";
    }

    /**
     * Log Firebase exception with detailed information
     */
    public static void logException(String context, Exception e) {
        Log.e(TAG, "Exception in " + context + ": " + e.getLocalizedMessage(), e);
    }

    /**
     * Check if error is due to network
     */
    public static boolean isNetworkError(Exception e) {
        return e.getLocalizedMessage() != null &&
                (e.getLocalizedMessage().contains("connection") ||
                 e.getLocalizedMessage().contains("network") ||
                 e.getLocalizedMessage().contains("timeout"));
    }
}
