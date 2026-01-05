package com.example.medicam;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medicam.utils.SessionManager;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import android.widget.TextView;

public class DashboardActivity extends AppCompatActivity {
    private static final String TAG = "DashboardActivity";
    
    private SessionManager sessionManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        
        sessionManager = SessionManager.getInstance(this);
        
        // Verify user is logged in using SessionManager
        if (!sessionManager.isUserLoggedIn()) {
            navigateToLogin();
            return;
        }
        
        // Load user name from database and display
        loadUserName();
        
        // Set up logout button
        ImageView btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> showLogoutDialog());
        
        // Set up profile image click to navigate to ProfileActivity
        ImageView profileImage = findViewById(R.id.profileImage);
        if (profileImage != null) {
            profileImage.setOnClickListener(v -> 
                startActivity(new Intent(DashboardActivity.this, ProfileActivity.class)));
        }
        
        // Set up service cards
        setupServiceCards();
        
        // Set up navigation click listeners
        setupBottomNavigation();
        
        // Set up AI Chat FAB
        setupAIChatFab();
    }
    
    private void loadUserName() {
        try {
            String userName = sessionManager.getUserName();
            TextView tvUserName = findViewById(R.id.tvUserName);
            
            if (tvUserName != null && userName != null && !userName.isEmpty()) {
                tvUserName.setText("Hi " + userName);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading user name: " + e.getMessage());
        }
    }
    
    private void setupAIChatFab() {
        ExtendedFloatingActionButton fabAIChat = findViewById(R.id.fabAIChat);
        fabAIChat.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, AIChatActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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
            // Clear session
            sessionManager.clearSession();
            
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "User logged out successfully");
            
            // Navigate back to MainActivity and clear all previous activities
            Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Error during logout: " + e.getMessage());
            Toast.makeText(this, "Error during logout", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void setupServiceCards() {
        MaterialCardView cardPathology = findViewById(R.id.cardPathology);
        MaterialCardView cardRadiology = findViewById(R.id.cardRadiology);
        MaterialCardView cardNutrition = findViewById(R.id.cardNutrition);
        
        cardPathology.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, PathologyActivity.class)));
        
        cardRadiology.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, RadiologyActivity.class)));
        
        if (cardNutrition != null) {
            cardNutrition.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, PersonalNutritionActivity.class)));
        }
        
        // Health Tools Cards
        setupHealthToolsCards();
    }
    
    private void setupHealthToolsCards() {
        // Medication Reminder
        MaterialCardView cardMedication = findViewById(R.id.cardMedication);
        if (cardMedication != null) {
            cardMedication.setOnClickListener(v -> 
                startActivity(new Intent(DashboardActivity.this, MedicationReminderActivity.class)));
        }
        
        // Vitals Tracker
        MaterialCardView cardVitals = findViewById(R.id.cardVitals);
        if (cardVitals != null) {
            cardVitals.setOnClickListener(v -> 
                startActivity(new Intent(DashboardActivity.this, VitalsTrackerActivity.class)));
        }
        
        // Symptom Checker
        MaterialCardView cardSymptoms = findViewById(R.id.cardSymptoms);
        if (cardSymptoms != null) {
            cardSymptoms.setOnClickListener(v -> 
                startActivity(new Intent(DashboardActivity.this, SymptomCheckerActivity.class)));
        }
        
        // Appointments & Lab Tests Booking
        MaterialCardView cardAppointments = findViewById(R.id.cardAppointments);
        if (cardAppointments != null) {
            cardAppointments.setOnClickListener(v -> 
                startActivity(new Intent(DashboardActivity.this, BookingActivity.class)));
        }
        
        // Emergency SOS
        MaterialCardView cardEmergency = findViewById(R.id.cardEmergency);
        if (cardEmergency != null) {
            cardEmergency.setOnClickListener(v -> 
                startActivity(new Intent(DashboardActivity.this, EmergencySosActivity.class)));
        }
        
        // Water Tracker
        MaterialCardView cardWater = findViewById(R.id.cardWater);
        if (cardWater != null) {
            cardWater.setOnClickListener(v -> 
                startActivity(new Intent(DashboardActivity.this, WaterTrackerActivity.class)));
        }
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
            Intent intent = new Intent(DashboardActivity.this, ReportsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        });
        
        // ABHA - Navigate to ABHA page
        navABHA.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, ABHAActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        
        // BMI - Navigate to BMI page
        navBMI.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, BMIGenderActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        
        // Devices - Navigate to Devices page
        navDevices.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, DevicesActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }
}
