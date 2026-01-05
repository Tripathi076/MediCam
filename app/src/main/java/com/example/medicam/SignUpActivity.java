package com.example.medicam;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.example.medicam.utils.SecurityUtils;
import com.example.medicam.utils.NetworkUtils;
import com.example.medicam.utils.FirebaseInitializer;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";
    
    private DatabaseReference usersRef;
    
    private TextInputEditText nameEditText;
    private TextInputEditText phoneEditText;
    private TextInputEditText passwordEditText;
    private AutoCompleteTextView stateAutoComplete;
    private CheckBox cbTerms;
    private MaterialButton btnSignUpAction;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase with persistence enabled
        FirebaseInitializer.initialize();
        
        // Initialize Firebase Database
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        
        ImageView btnBack = findViewById(R.id.btnBack);
        nameEditText = findViewById(R.id.nameEditText);
        phoneEditText = findViewById(R.id.SignupPhoneNumber);
        passwordEditText = findViewById(R.id.passwordEditText);
        stateAutoComplete = findViewById(R.id.stateAutoComplete);
        cbTerms = findViewById(R.id.cbTerms);
        btnSignUpAction = findViewById(R.id.btnSignUpAction);
        TextView tvSignIn = findViewById(R.id.tvSignIn);
        progressBar = findViewById(R.id.progressBar);

        // Setup State Dropdown
        String[] states = {"Maharashtra", "Delhi", "Karnataka", "Tamil Nadu", "Gujarat", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, states);
        stateAutoComplete.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        btnSignUpAction.setOnClickListener(v -> {
            String name = nameEditText.getText() != null ? nameEditText.getText().toString().trim() : "";
            String phone = phoneEditText.getText() != null ? phoneEditText.getText().toString().trim() : "";
            String password = passwordEditText.getText() != null ? passwordEditText.getText().toString().trim() : "";
            String state = stateAutoComplete.getText() != null ? stateAutoComplete.getText().toString().trim() : "";
            boolean termsAccepted = cbTerms.isChecked();

            // Validate name
            String nameError = SecurityUtils.validateName(name);
            if (nameError != null) {
                Toast.makeText(this, nameError, Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Validate phone
            String phoneError = SecurityUtils.validatePhoneNumber(phone);
            if (phoneError != null) {
                Toast.makeText(this, phoneError, Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Validate password strength
            String passwordError = SecurityUtils.validatePasswordStrength(password);
            if (passwordError != null) {
                Toast.makeText(this, passwordError, Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (state.isEmpty()) {
                Toast.makeText(this, "Please select your state", Toast.LENGTH_SHORT).show();
            } else if (!termsAccepted) {
                Toast.makeText(this, "Please agree to the Terms & Condition", Toast.LENGTH_SHORT).show();
            } else {
                // Check if phone already registered, then register user
                checkAndRegisterUser(name, phone, password, state);
            }
        });

        tvSignIn.setOnClickListener(v -> {
            // Navigate to Phone Login
            Intent intent = new Intent(SignUpActivity.this, PhoneLoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
    
    private void checkAndRegisterUser(String name, String phone, String password, String state) {
        // Check network connectivity first
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection. Please check your network.", Toast.LENGTH_SHORT).show();
            btnSignUpAction.setEnabled(true);
            return;
        }
        
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        btnSignUpAction.setEnabled(false);
        
        Log.d(TAG, "Checking if phone number exists: " + phone);
        
        // Check if phone already exists in Firebase - read all users and filter locally
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                btnSignUpAction.setEnabled(true);
                
                boolean phoneExists = false;
                
                // Find user by phone number
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    String storedPhone = userSnap.child("phone").getValue(String.class);
                    if (storedPhone != null && storedPhone.trim().equals(phone.trim())) {
                        phoneExists = true;
                        break;
                    }
                }
                
                if (phoneExists) {
                    // Phone already registered
                    Log.d(TAG, "Phone number already exists");
                    Toast.makeText(SignUpActivity.this, "Phone number already registered. Please login.", Toast.LENGTH_SHORT).show();
                } else {
                    // Phone not registered - proceed with registration
                    Log.d(TAG, "Phone number is available, proceeding with registration");
                    registerUserInFirebase(name, phone, password, state);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                btnSignUpAction.setEnabled(true);
                
                Log.e(TAG, "Database error code: " + error.getCode());
                Log.e(TAG, "Database error message: " + error.getMessage());
                
                String errorMsg = getErrorMessage(error);
                Toast.makeText(SignUpActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }
    
    private void registerUserInFirebase(String name, String phone, String password, String state) {
        // Generate unique user ID
        String odbc_userId = usersRef.push().getKey();
        
        if (odbc_userId == null) {
            if (progressBar != null) progressBar.setVisibility(View.GONE);
            btnSignUpAction.setEnabled(true);
            Toast.makeText(this, "Failed to generate user ID", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Hash the password before storing
        String hashedPassword = SecurityUtils.hashPassword(password);
        
        // Create user data map
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", odbc_userId);
        userData.put("name", name);
        userData.put("phone", phone);
        userData.put("password", hashedPassword); // Store hashed password
        userData.put("state", state);
        userData.put("pin", ""); // PIN will be set during first login
        userData.put("createdAt", System.currentTimeMillis());
        
        // Save to Firebase
        usersRef.child(odbc_userId).setValue(userData)
                .addOnSuccessListener(aVoid -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    btnSignUpAction.setEnabled(true);
                    Log.d(TAG, "User registered successfully with ID: " + odbc_userId);
                    showSignupSuccessDialog();
                })
                .addOnFailureListener(e -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    btnSignUpAction.setEnabled(true);
                    Log.e(TAG, "Failed to register user: " + e.getMessage());
                    Toast.makeText(SignUpActivity.this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    
    private void showSignupSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_login_success, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.setCancelable(false);

        // Update dialog text for signup
        TextView tvTitle = dialogView.findViewById(R.id.tvSuccessTitle);
        if (tvTitle != null) {
            tvTitle.setText("Registration Successful!");
        }
        
        TextView tvMessage = dialogView.findViewById(R.id.tvSuccessMessage);
        if (tvMessage != null) {
            tvMessage.setText("Your account has been created. Please login to continue.");
        }

        MaterialButton btnGoToDashboard = dialogView.findViewById(R.id.btnGoToDashboard);
        if (btnGoToDashboard != null) {
            btnGoToDashboard.setText("Go to Login");
            btnGoToDashboard.setOnClickListener(v -> {
                dialog.dismiss();
                // Navigate to Login
                Intent intent = new Intent(SignUpActivity.this, PhoneLoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }

        dialog.show();
    }
    
    private String getErrorMessage(DatabaseError error) {
        switch (error.getCode()) {
            case DatabaseError.PERMISSION_DENIED:
                return "Permission denied. Unable to access database. Check Firebase rules.";
            case DatabaseError.UNAVAILABLE:
                return "Database service unavailable. Please try again later.";
            case DatabaseError.NETWORK_ERROR:
                return "Network error. Please check your internet connection.";
            case DatabaseError.DISCONNECTED:
                return "Disconnected from database. Reconnecting...";
            default:
                return "Error: " + error.getMessage();
        }
    }
}
