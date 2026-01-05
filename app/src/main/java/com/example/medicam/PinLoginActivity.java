package com.example.medicam;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medicam.utils.BiometricHelper;
import com.example.medicam.utils.LoginAttemptManager;
import com.example.medicam.utils.SecurityUtils;
import com.example.medicam.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class PinLoginActivity extends AppCompatActivity {
    private static final String TAG = "PinLoginActivity";

    private EditText etPin1, etPin2, etPin3, etPin4;
    private MaterialButton btnLogin;
    private ProgressBar progressBar;
    private TextView tvPhoneNumber;
    
    private String phoneNumber;
    private String userId;
    private DatabaseReference usersRef;
    private SessionManager sessionManager;
    private LoginAttemptManager attemptManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_login);

        // Get phone number and userId from intent
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        userId = getIntent().getStringExtra("userId");
        
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            Toast.makeText(this, "Error: Phone number not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Firebase Database
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        sessionManager = SessionManager.getInstance(this);
        attemptManager = LoginAttemptManager.getInstance(this);

        initViews();
        setupPinInputs();
        setupListeners();
        
        // Try biometric authentication if enabled
        checkBiometricLogin();
    }
    
    private void checkBiometricLogin() {
        // Check if biometric is enabled and available
        if (sessionManager.isBiometricEnabled() && BiometricHelper.isBiometricAvailable(this)) {
            BiometricHelper.authenticateForLogin(this, new BiometricHelper.BiometricCallback() {
                @Override
                public void onAuthenticationSucceeded() {
                    // Biometric success - login directly
                    attemptManager.resetAttempts(phoneNumber);
                    sessionManager.updateLastActiveTime();
                    
                    // Navigate to Dashboard
                    Intent intent = new Intent(PinLoginActivity.this, DashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                
                @Override
                public void onAuthenticationFailed() {
                    // Biometric didn't match - user can still use PIN
                    Toast.makeText(PinLoginActivity.this, "Biometric not recognized. Use PIN.", Toast.LENGTH_SHORT).show();
                }
                
                @Override
                public void onAuthenticationError(int errorCode, String errorMessage) {
                    // User cancelled or error - they can use PIN
                    if (errorCode != 10 && errorCode != 13) { // Not user cancelled
                        Toast.makeText(PinLoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void initViews() {
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        if (tvPhoneNumber != null) {
            tvPhoneNumber.setText("Enter PIN for +91 " + phoneNumber);
        }
        
        // PIN fields
        etPin1 = findViewById(R.id.etPin1);
        etPin2 = findViewById(R.id.etPin2);
        etPin3 = findViewById(R.id.etPin3);
        etPin4 = findViewById(R.id.etPin4);
        
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupPinInputs() {
        EditText[] pinFields = {etPin1, etPin2, etPin3, etPin4};
        
        for (int i = 0; i < pinFields.length; i++) {
            final int currentIndex = i;
            pinFields[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1) {
                        if (currentIndex < pinFields.length - 1) {
                            pinFields[currentIndex + 1].requestFocus();
                        }
                    } else if (s.length() == 0 && currentIndex > 0) {
                        pinFields[currentIndex - 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> validateAndLogin());
        
        // Forgot PIN - use password instead
        TextView tvForgotPin = findViewById(R.id.tvForgotPin);
        if (tvForgotPin != null) {
            tvForgotPin.setOnClickListener(v -> {
                // Navigate to Password + PIN activity to reset PIN
                Intent intent = new Intent(PinLoginActivity.this, PasswordPinActivity.class);
                intent.putExtra("phoneNumber", phoneNumber);
                intent.putExtra("userId", userId);
                startActivity(intent);
                finish();
            });
        }
    }

    private void validateAndLogin() {
        // Check if account is locked out
        if (attemptManager.isLockedOut(phoneNumber)) {
            Toast.makeText(this, attemptManager.getLockoutMessage(phoneNumber), Toast.LENGTH_LONG).show();
            return;
        }
        
        // Get PIN
        String pin = etPin1.getText().toString() + 
                     etPin2.getText().toString() + 
                     etPin3.getText().toString() + 
                     etPin4.getText().toString();

        if (pin.length() != 4) {
            Toast.makeText(this, "Please enter your 4-digit PIN", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate PIN with Firebase
        performPinLogin(pin);
    }

    private void performPinLogin(String enteredPin) {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        // Read all users and find by phone (more reliable)
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
                
                DataSnapshot foundUser = null;
                
                // Find user by phone number
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    String storedPhone = userSnap.child("phone").getValue(String.class);
                    if (storedPhone != null && storedPhone.trim().equals(phoneNumber.trim())) {
                        foundUser = userSnap;
                        break;
                    }
                }
                
                if (foundUser != null) {
                    String dbPin = foundUser.child("pin").getValue(String.class);
                    String dbUserId = foundUser.getKey();
                    String dbName = foundUser.child("name").getValue(String.class);
                    
                    Log.d(TAG, "User found: " + dbName + " (ID: " + dbUserId + ")");
                    
                    // Verify PIN using SecurityUtils (supports both hashed and plain text)
                    if (dbPin != null && SecurityUtils.verifyPin(enteredPin, dbPin)) {
                        Log.d(TAG, "PIN verified successfully");
                        // PIN is correct - reset attempts
                        attemptManager.resetAttempts(phoneNumber);
                        
                        // Save session
                        sessionManager.saveUserSession(
                            dbUserId,
                            "", // email not used
                            phoneNumber,
                            dbName != null ? dbName : ""
                        );
                        
                        sessionManager.updateLastActiveTime();
                        
                        // Show success and navigate to dashboard
                        showLoginSuccessDialog();
                    } else {
                        Log.w(TAG, "PIN verification failed");
                        // PIN is wrong - record failed attempt
                        boolean isLocked = attemptManager.recordFailedAttempt(phoneNumber);
                        
                        if (isLocked) {
                            Toast.makeText(PinLoginActivity.this, 
                                attemptManager.getLockoutMessage(phoneNumber), Toast.LENGTH_LONG).show();
                        } else {
                            int remaining = attemptManager.getRemainingAttempts(phoneNumber);
                            Toast.makeText(PinLoginActivity.this, 
                                "Wrong PIN! " + remaining + " attempt(s) remaining", Toast.LENGTH_LONG).show();
                        }
                        clearPinFields();
                    }
                } else {
                    Log.e(TAG, "User not found for phone: " + phoneNumber);
                    Toast.makeText(PinLoginActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
                Log.e(TAG, "Database error: " + error.getMessage());
                Toast.makeText(PinLoginActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearPinFields() {
        etPin1.setText("");
        etPin2.setText("");
        etPin3.setText("");
        etPin4.setText("");
        etPin1.requestFocus();
    }

    private void showLoginSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_login_success, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.setCancelable(false);

        MaterialButton btnGoToDashboard = dialogView.findViewById(R.id.btnGoToDashboard);
        if (btnGoToDashboard != null) {
            btnGoToDashboard.setOnClickListener(v -> {
                dialog.dismiss();
                // Navigate to Dashboard
                Intent intent = new Intent(PinLoginActivity.this, DashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }

        dialog.show();
    }

    private void showPinIncorrectDialog(int remainingAttempts) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Incorrect PIN");
        String message = "The PIN you entered is incorrect. Please try again.";
        if (remainingAttempts <= 3) {
            message += "\n\n" + remainingAttempts + " attempt(s) remaining before lockout.";
        }
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("Try Again", (dialog, which) -> {
            clearPinFields();
            dialog.dismiss();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
            finish();
        });
        builder.show();
    }
}
