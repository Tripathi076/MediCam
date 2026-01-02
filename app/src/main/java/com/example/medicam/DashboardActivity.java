package com.example.medicam;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medicam.utils.FirebaseErrorHandler;
import com.example.medicam.utils.SessionManager;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;

public class DashboardActivity extends AppCompatActivity {
    private static final String TAG = "DashboardActivity";
    
    private FirebaseAuth mAuth;
    private SessionManager sessionManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        
        mAuth = FirebaseAuth.getInstance();
        sessionManager = SessionManager.getInstance(this);
        
        // Verify user is logged in
        if (mAuth.getCurrentUser() == null) {
            navigateToLogin();
            return;
        }
        
        // Set up logout button
        ImageView btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> showLogoutDialog());
        
        // Set up service cards
        setupServiceCards();
        
        // Set up navigation click listeners
        setupBottomNavigation();
    }
    
    private void navigateToLogin() {
        Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
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
            // Clear Firebase Auth
            mAuth.signOut();
            
            // Clear SharedPreferences
            sessionManager.clearSession();
            
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "User logged out successfully");
            
            // Navigate back to MainActivity and clear all previous activities
            Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            FirebaseErrorHandler.logException("performLogout", e);
            Toast.makeText(this, "Error during logout", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void setupServiceCards() {
        MaterialCardView cardPathology = findViewById(R.id.cardPathology);
        MaterialCardView cardRadiology = findViewById(R.id.cardRadiology);
        
        cardPathology.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, PathologyActivity.class)));
        
        cardRadiology.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, RadiologyActivity.class)));
    }
    
    private void setupBottomNavigation() {
        View navHome = findViewById(R.id.navHome);
        View navPathology = findViewById(R.id.navPathology);
        View navABHA = findViewById(R.id.navABHA);
        View navBMI = findViewById(R.id.navBMI);
        View navDevices = findViewById(R.id.navDevices);
        
        // Home - Already on Dashboard, do nothing
        navHome.setOnClickListener(v -> Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show());
        
        // Reports - Navigate to unified Reports page (showing both Pathology and Radiology)
        navPathology.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, ReportsActivity.class));
            finish();
        });
        
        // ABHA - Navigate to ABHA page
        navABHA.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, ABHAActivity.class)));
        
        // BMI - Navigate to BMI page
        navBMI.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, BMIActivity.class)));
        
        // Devices - Navigate to Devices page
        navDevices.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, DevicesActivity.class)));
    }
}
