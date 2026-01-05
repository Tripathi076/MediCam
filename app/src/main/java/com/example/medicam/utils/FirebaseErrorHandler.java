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
        if (e == null) {
            return "An unknown error occurred. Please try again.";
        }
        
        Log.e(TAG, "Auth Error: " + e.getLocalizedMessage(), e);
        Log.e(TAG, "Exception class: " + e.getClass().getName());

        if (e instanceof FirebaseAuthWeakPasswordException) {
            return "Password is too weak. Use at least 6 characters with letters and numbers.";
        } else if (e instanceof FirebaseAuthInvalidUserException) {
            return "User account not found. Please sign up first.";
        } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
            return "Invalid email or password. Please try again.";
        } else if (e instanceof FirebaseAuthUserCollisionException) {
            return "This email is already registered. Please login instead.";
        } else if (e.getLocalizedMessage() != null) {
            String msg = e.getLocalizedMessage().toLowerCase();
            if (msg.contains("password is invalid")) {
                return "Invalid password. Please try again.";
            } else if (msg.contains("there is no user record")) {
                return "User not found. Please sign up first.";
            } else if (msg.contains("too many requests")) {
                return "Too many attempts. Please try again later.";
            } else if (msg.contains("network") || msg.contains("connection")) {
                return "Network error. Please check your internet connection.";
            } else if (msg.contains("email") && msg.contains("badly formatted")) {
                return "Invalid email format. Please enter a valid email.";
            } else if (msg.contains("email") && msg.contains("already in use")) {
                return "This email is already registered. Please login instead.";
            } else if (msg.contains("internal error") || msg.contains("billing")) {
                return "Service temporarily unavailable. Please try again later.";
            } else if (msg.contains("sign_in_method") || msg.contains("not enabled")) {
                return "Email/Password sign-in is not enabled. Please contact support.";
            }
            // Return the actual Firebase message for debugging
            return "Error: " + e.getLocalizedMessage();
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
