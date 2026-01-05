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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.example.medicam.utils.NetworkUtils;
import com.example.medicam.utils.FirebaseInitializer;

public class PhoneLoginActivity extends AppCompatActivity {
    private static final String TAG = "PhoneLoginActivity";

    private EditText etPhoneNumber;
    private MaterialButton btnContinue;
    private TextView tvAdminPortal;
    private ProgressBar progressBar;
    
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        // Initialize Firebase with persistence enabled
        FirebaseInitializer.initialize();
        
        // Initialize Firebase Database
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        
        // Initialize views
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        btnContinue = findViewById(R.id.btnGetOTP);
        tvAdminPortal = findViewById(R.id.tvAdminPortal);
        progressBar = findViewById(R.id.progressBar);
        
        setupListeners();
    }
    
    private void setupListeners() {
        // Enable button when phone number is 10 digits
        etPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String phone = s.toString().trim();
                btnContinue.setEnabled(phone.length() == 10);
            }
        });

        // Continue button click
        btnContinue.setOnClickListener(v -> {
            String phoneNumber = etPhoneNumber.getText().toString().trim();
            if (phoneNumber.length() == 10) {
                checkPhoneInDatabase(phoneNumber);
            } else {
                Toast.makeText(this, "Please enter valid 10 digit phone number", Toast.LENGTH_SHORT).show();
            }
        });

        // Admin Portal click
        tvAdminPortal.setOnClickListener(v -> {
            Intent intent = new Intent(PhoneLoginActivity.this, AdminLoginActivity.class);
            startActivity(intent);
        });
    }
    
    private void checkPhoneInDatabase(String phoneNumber) {
        // Check network connectivity first
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection. Please check your network.", Toast.LENGTH_SHORT).show();
            btnContinue.setEnabled(true);
            return;
        }
        
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        btnContinue.setEnabled(false);
        
        Log.d(TAG, "=== SEARCHING FOR PHONE ===");
        Log.d(TAG, "Phone entered: '" + phoneNumber + "'");
        Log.d(TAG, "Phone length: " + phoneNumber.length());
        
        // Read all users and filter by phone (more reliable than orderByChild)
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                btnContinue.setEnabled(true);
                
                Log.d(TAG, "=== DATABASE CONTAINS ===");
                Log.d(TAG, "Total users: " + snapshot.getChildrenCount());
                
                DataSnapshot foundUser = null;
                
                // Loop through all users and find matching phone
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    String storedPhone = userSnap.child("phone").getValue(String.class);
                    String userName = userSnap.child("name").getValue(String.class);
                    String userId = userSnap.getKey();
                    
                    Log.d(TAG, "User ID: " + userId + 
                            " | Phone: '" + storedPhone + "' | Name: " + userName);
                    
                    // Compare phones (trim both to remove spaces)
                    if (storedPhone != null && storedPhone.trim().equals(phoneNumber.trim())) {
                        Log.d(TAG, "✓ MATCH FOUND for phone: " + phoneNumber);
                        foundUser = userSnap;
                        break;
                    }
                }
                
                if (foundUser != null) {
                    // User found
                    String userId = foundUser.getKey();
                    String pin = foundUser.child("pin").getValue(String.class);
                    
                    Log.d(TAG, "Login: User ID: " + userId + ", PIN set: " + (pin != null && !pin.isEmpty()));
                    
                    if (pin != null && !pin.isEmpty()) {
                        Intent intent = new Intent(PhoneLoginActivity.this, PinLoginActivity.class);
                        intent.putExtra("phoneNumber", phoneNumber);
                        intent.putExtra("userId", userId);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(PhoneLoginActivity.this, PasswordPinActivity.class);
                        intent.putExtra("phoneNumber", phoneNumber);
                        intent.putExtra("userId", userId);
                        startActivity(intent);
                    }
                } else {
                    Log.d(TAG, "✗ NO MATCH for phone: " + phoneNumber);
                    showNotRegisteredDialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                btnContinue.setEnabled(true);
                
                Log.e(TAG, "Database error code: " + error.getCode());
                Log.e(TAG, "Database error message: " + error.getMessage());
                
                String errorMsg = getErrorMessage(error);
                Toast.makeText(PhoneLoginActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
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
    
    private void showNotRegisteredDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Not Registered");
        builder.setMessage("This phone number is not registered. Please sign up first to create an account.");
        builder.setCancelable(true);
        
        builder.setPositiveButton("Sign Up", (dialog, which) -> {
            dialog.dismiss();
            // Navigate to Sign Up page
            Intent intent = new Intent(PhoneLoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
        
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
            etPhoneNumber.setText("");
            etPhoneNumber.requestFocus();
        });
        
        builder.show();
    }
}
