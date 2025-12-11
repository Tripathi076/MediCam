package com.example.medicam;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ABHAActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abha);
        
        // Handle Window Insets for EdgeToEdge
        if (findViewById(R.id.main) != null) {
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
        
        // Set up navigation click listeners
        setupBottomNavigation();
    }
    
    private void setupBottomNavigation() {
        View navHome = findViewById(R.id.navHome);
        View navPathology = findViewById(R.id.navPathology);
        View navABHA = findViewById(R.id.navABHA);
        View navBMI = findViewById(R.id.navBMI);
        View navDevices = findViewById(R.id.navDevices);
        
        // Home
        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                startActivity(new Intent(ABHAActivity.this, DashboardActivity.class));
                finish();
            });
        }
        
        // Pathology
        if (navPathology != null) {
            navPathology.setOnClickListener(v -> {
                startActivity(new Intent(ABHAActivity.this, PathologyActivity.class));
                finish();
            });
        }
        
        // ABHA - Already on this page
        if (navABHA != null) {
            navABHA.setOnClickListener(v -> {
                Toast.makeText(this, "ABHA Manager", Toast.LENGTH_SHORT).show();
            });
        }
        
        // BMI
        if (navBMI != null) {
            navBMI.setOnClickListener(v -> {
                startActivity(new Intent(ABHAActivity.this, BMIActivity.class));
                finish();
            });
        }
        
        // Devices
        if (navDevices != null) {
            navDevices.setOnClickListener(v -> {
                startActivity(new Intent(ABHAActivity.this, DevicesActivity.class));
                finish();
            });
        }
    }
}
