package com.example.medicam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.medicam.utils.BiometricHelper;
import com.example.medicam.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";

    private ImageView btnBack;
    private RadioGroup rgLanguage;
    private RadioButton rbEnglish, rbHindi, rbMarathi;
    private SwitchCompat switchNotification;
    private SwitchCompat switchBiometric;
    private SwitchCompat switchAutoLock;
    private MaterialButton btnLogout;

    private SharedPreferences prefs;
    private static final String SETTINGS_PREFS = "SettingsPrefs";
    
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = getSharedPreferences(SETTINGS_PREFS, MODE_PRIVATE);
        sessionManager = SessionManager.getInstance(this);

        initViews();
        loadSettings();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        rgLanguage = findViewById(R.id.rgLanguage);
        rbEnglish = findViewById(R.id.rbEnglish);
        rbHindi = findViewById(R.id.rbHindi);
        rbMarathi = findViewById(R.id.rbMarathi);
        switchNotification = findViewById(R.id.switchNotification);
        switchBiometric = findViewById(R.id.switchBiometric);
        switchAutoLock = findViewById(R.id.switchAutoLock);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void loadSettings() {
        // Load language preference
        String language = prefs.getString("language", "English");
        switch (language) {
            case "Hindi":
                rbHindi.setChecked(true);
                break;
            case "Marathi":
                rbMarathi.setChecked(true);
                break;
            default:
                rbEnglish.setChecked(true);
                break;
        }

        // Load notification preference
        boolean notificationsEnabled = prefs.getBoolean("notifications_enabled", true);
        switchNotification.setChecked(notificationsEnabled);
        
        // Load biometric preference
        if (switchBiometric != null) {
            boolean biometricAvailable = BiometricHelper.isBiometricAvailable(this);
            switchBiometric.setEnabled(biometricAvailable);
            if (!biometricAvailable) {
                switchBiometric.setChecked(false);
            } else {
                switchBiometric.setChecked(sessionManager.isBiometricEnabled());
            }
        }
        
        // Load auto-lock preference
        if (switchAutoLock != null) {
            switchAutoLock.setChecked(sessionManager.isAutoLockEnabled());
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        rgLanguage.setOnCheckedChangeListener((group, checkedId) -> {
            String selectedLanguage = "English";
            if (checkedId == R.id.rbHindi) {
                selectedLanguage = "Hindi";
            } else if (checkedId == R.id.rbMarathi) {
                selectedLanguage = "Marathi";
            }

            prefs.edit().putString("language", selectedLanguage).apply();
            Toast.makeText(this, "Language set to " + selectedLanguage, Toast.LENGTH_SHORT).show();
        });

        switchNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("notifications_enabled", isChecked).apply();
            String message = isChecked ? "Notifications enabled" : "Notifications disabled";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });
        
        // Biometric switch listener
        if (switchBiometric != null) {
            switchBiometric.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked && !BiometricHelper.isBiometricAvailable(this)) {
                    switchBiometric.setChecked(false);
                    Toast.makeText(this, BiometricHelper.getBiometricStatus(this), Toast.LENGTH_LONG).show();
                    return;
                }
                sessionManager.setBiometricEnabled(isChecked);
                String message = isChecked ? "Biometric login enabled" : "Biometric login disabled";
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            });
        }
        
        // Auto-lock switch listener
        if (switchAutoLock != null) {
            switchAutoLock.setOnCheckedChangeListener((buttonView, isChecked) -> {
                sessionManager.setAutoLockEnabled(isChecked);
                String message = isChecked ? "Auto-lock enabled (5 min inactivity)" : "Auto-lock disabled";
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            });
        }
        
        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }
    
    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes", (dialog, which) -> performLogout())
            .setNegativeButton("Cancel", null)
            .show();
    }
    
    private void performLogout() {
        try {
            // Clear session
            sessionManager.clearSession();
            
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "User logged out successfully");
            
            // Navigate back to MainActivity and clear all previous activities
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Error during logout: " + e.getMessage());
            Toast.makeText(this, "Error during logout", Toast.LENGTH_SHORT).show();
        }
    }
}
