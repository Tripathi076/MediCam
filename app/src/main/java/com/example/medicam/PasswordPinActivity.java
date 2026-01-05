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

import com.example.medicam.utils.LoginAttemptManager;
import com.example.medicam.utils.SecurityUtils;
import com.example.medicam.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class PasswordPinActivity extends AppCompatActivity {
    private static final String TAG = "PasswordPinActivity";

    private TextInputEditText etPassword;
    private EditText etPin1, etPin2, etPin3, etPin4;
    private EditText etConfirmPin1, etConfirmPin2, etConfirmPin3, etConfirmPin4;
    private MaterialButton btnLogin;
    private ProgressBar progressBar;
    
    private String phoneNumber;
    private String userId;
    private DatabaseReference usersRef;
    private SessionManager sessionManager;
    private LoginAttemptManager attemptManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_pin);

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
    }

    private void initViews() {
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        etPassword = findViewById(R.id.etPassword);
        
        // PIN fields
        etPin1 = findViewById(R.id.etPin1);
        etPin2 = findViewById(R.id.etPin2);
        etPin3 = findViewById(R.id.etPin3);
        etPin4 = findViewById(R.id.etPin4);
        
        // Confirm PIN fields
        etConfirmPin1 = findViewById(R.id.etConfirmPin1);
        etConfirmPin2 = findViewById(R.id.etConfirmPin2);
        etConfirmPin3 = findViewById(R.id.etConfirmPin3);
        etConfirmPin4 = findViewById(R.id.etConfirmPin4);
        
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupPinInputs() {
        // Setup auto-focus for PIN fields
        EditText[] pinFields = {etPin1, etPin2, etPin3, etPin4};
        EditText[] confirmPinFields = {etConfirmPin1, etConfirmPin2, etConfirmPin3, etConfirmPin4};
        
        setupAutoFocus(pinFields, confirmPinFields[0]);
        setupAutoFocus(confirmPinFields, null);
    }

    private void setupAutoFocus(EditText[] fields, EditText nextField) {
        for (int i = 0; i < fields.length; i++) {
            final int currentIndex = i;
            fields[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1) {
                        if (currentIndex < fields.length - 1) {
                            fields[currentIndex + 1].requestFocus();
                        } else if (nextField != null) {
                            nextField.requestFocus();
                        }
                    } else if (s.length() == 0 && currentIndex > 0) {
                        fields[currentIndex - 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> validateAndLogin());
    }

    private void validateAndLogin() {
        // Check if account is locked out
        if (attemptManager.isLockedOut(phoneNumber)) {
            Toast.makeText(this, attemptManager.getLockoutMessage(phoneNumber), Toast.LENGTH_LONG).show();
            return;
        }
        
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
        
        // Get PIN
        String pin = etPin1.getText().toString() + 
                     etPin2.getText().toString() + 
                     etPin3.getText().toString() + 
                     etPin4.getText().toString();
        
        // Get Confirm PIN
        String confirmPin = etConfirmPin1.getText().toString() + 
                            etConfirmPin2.getText().toString() + 
                            etConfirmPin3.getText().toString() + 
                            etConfirmPin4.getText().toString();

        // Validate inputs
        if (password.isEmpty()) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pin.length() != 4) {
            Toast.makeText(this, "Please enter a 4-digit PIN", Toast.LENGTH_SHORT).show();
            return;
        }

        if (confirmPin.length() != 4) {
            Toast.makeText(this, "Please confirm your 4-digit PIN", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pin.equals(confirmPin)) {
            Toast.makeText(this, "PINs do not match", Toast.LENGTH_SHORT).show();
            clearPinFields();
            return;
        }

        // Validate password with database
        performLogin(password, pin);
    }

    private void performLogin(String password, String pin) {
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
                    String dbPassword = foundUser.child("password").getValue(String.class);
                    String dbUserId = foundUser.getKey();
                    String dbName = foundUser.child("name").getValue(String.class);
                    
                    Log.d(TAG, "User found: " + dbName + " (ID: " + dbUserId + ")");
                    
                    // Verify password using SecurityUtils (supports both hashed and plain text)
                    if (dbPassword != null && SecurityUtils.verifyPassword(password, dbPassword)) {
                        Log.d(TAG, "Password verified successfully");
                        // Password is correct - reset attempts and store the PIN in Firebase
                        attemptManager.resetAttempts(phoneNumber);
                        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
                        btnLogin.setEnabled(false);
                        storePinInFirebase(dbUserId, dbName, pin);
                    } else {
                        // Password is wrong
                        Log.w(TAG, "Password verification failed");
                        boolean isLocked = attemptManager.recordFailedAttempt(phoneNumber);
                        
                        if (isLocked) {
                            Toast.makeText(PasswordPinActivity.this, 
                                attemptManager.getLockoutMessage(phoneNumber), Toast.LENGTH_LONG).show();
                        } else {
                            int remaining = attemptManager.getRemainingAttempts(phoneNumber);
                            Toast.makeText(PasswordPinActivity.this, 
                                "Wrong password! " + remaining + " attempt(s) remaining", Toast.LENGTH_LONG).show();
                        }
                        // Clear fields
                        etPassword.setText("");
                        clearPinFields();
                        etPassword.requestFocus();
                    }
                } else {
                    Log.e(TAG, "User not found for phone: " + phoneNumber);
                    Toast.makeText(PasswordPinActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
                Log.e(TAG, "Database error: " + error.getMessage());
                Toast.makeText(PasswordPinActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void storePinInFirebase(String odbc_userId, String userName, String pin) {
        // First, clear old PIN if it exists
        usersRef.child(odbc_userId).child("pin").removeValue()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Old PIN cleared for user: " + phoneNumber);
                    // Now store the new PIN
                    saveNewPin(odbc_userId, userName, pin);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Failed to clear old PIN: " + e.getMessage());
                    // Continue to save new PIN anyway
                    saveNewPin(odbc_userId, userName, pin);
                });
    }
    
    private void saveNewPin(String odbc_userId, String userName, String pin) {
        // Hash the PIN before storing
        String hashedPin = SecurityUtils.hashPin(pin);
        
        usersRef.child(odbc_userId).child("pin").setValue(hashedPin)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "New PIN stored successfully for user: " + phoneNumber);
                    
                    // Save session
                    sessionManager.saveUserSession(
                        odbc_userId,
                        "", // email not used
                        phoneNumber,
                        userName != null ? userName : ""
                    );
                    
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    
                    // Show success and navigate to dashboard
                    showLoginSuccessDialog();
                })
                .addOnFailureListener(e -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);
                    Log.e(TAG, "Failed to store new PIN: " + e.getMessage());
                    Toast.makeText(PasswordPinActivity.this, "Failed to save PIN. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }
    private void clearPinFields() {
        etPin1.setText("");
        etPin2.setText("");
        etPin3.setText("");
        etPin4.setText("");
        etConfirmPin1.setText("");
        etConfirmPin2.setText("");
        etConfirmPin3.setText("");
        etConfirmPin4.setText("");
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
                Intent intent = new Intent(PasswordPinActivity.this, DashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }

        dialog.show();
    }

    private void showLoginUnsuccessfulDialog(int remainingAttempts) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Login Failed");
        String message = "Incorrect password. Please try again.";
        if (remainingAttempts <= 3) {
            message += "\n\n" + remainingAttempts + " attempt(s) remaining before lockout.";
        }
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("Try Again", (dialog, which) -> {
            etPassword.setText("");
            clearPinFields();
            etPassword.requestFocus();
            dialog.dismiss();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
            finish();
        });
        builder.show();
    }
}
