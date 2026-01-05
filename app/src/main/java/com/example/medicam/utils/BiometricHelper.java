package com.example.medicam.utils;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import java.util.concurrent.Executor;

/**
 * Helper class for biometric authentication (fingerprint/face)
 */
public class BiometricHelper {
    
    public interface BiometricCallback {
        void onAuthenticationSucceeded();
        void onAuthenticationFailed();
        void onAuthenticationError(int errorCode, String errorMessage);
    }
    
    /**
     * Check if biometric authentication is available on this device
     * @param context Application context
     * @return true if biometric is available and enrolled
     */
    public static boolean isBiometricAvailable(Context context) {
        BiometricManager biometricManager = BiometricManager.from(context);
        int canAuthenticate = biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_WEAK);
        return canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS;
    }
    
    /**
     * Get biometric availability status message
     * @param context Application context
     * @return Status message
     */
    public static String getBiometricStatus(Context context) {
        BiometricManager biometricManager = BiometricManager.from(context);
        int canAuthenticate = biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_WEAK);
        
        switch (canAuthenticate) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                return "Biometric authentication is available";
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                return "No biometric hardware available";
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                return "Biometric hardware is currently unavailable";
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                return "No biometric credentials enrolled. Please set up fingerprint or face in device settings.";
            case BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED:
                return "Security update required for biometric";
            default:
                return "Biometric authentication unavailable";
        }
    }
    
    /**
     * Show biometric authentication prompt
     * @param activity FragmentActivity to show prompt
     * @param title Prompt title
     * @param subtitle Prompt subtitle
     * @param callback Callback for authentication result
     */
    public static void showBiometricPrompt(
            @NonNull FragmentActivity activity,
            @NonNull String title,
            @NonNull String subtitle,
            @NonNull BiometricCallback callback) {
        
        Executor executor = ContextCompat.getMainExecutor(activity);
        
        BiometricPrompt biometricPrompt = new BiometricPrompt(activity, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        callback.onAuthenticationSucceeded();
                    }
                    
                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        callback.onAuthenticationFailed();
                    }
                    
                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        callback.onAuthenticationError(errorCode, errString.toString());
                    }
                });
        
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle(subtitle)
                .setNegativeButtonText("Use PIN instead")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK)
                .build();
        
        biometricPrompt.authenticate(promptInfo);
    }
    
    /**
     * Quick authentication for returning users
     * @param activity FragmentActivity
     * @param callback Callback
     */
    public static void authenticateForLogin(
            @NonNull FragmentActivity activity,
            @NonNull BiometricCallback callback) {
        
        showBiometricPrompt(
                activity,
                "Login to MediCam",
                "Use your fingerprint or face to login quickly",
                callback
        );
    }
}
