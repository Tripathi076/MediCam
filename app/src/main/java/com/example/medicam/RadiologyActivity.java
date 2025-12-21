package com.example.medicam;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class RadiologyActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radiology);

        ImageView btnBack = findViewById(R.id.btnBack);
        FloatingActionButton fabAddReport = findViewById(R.id.fabAddReport);

        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(RadiologyActivity.this, DashboardActivity.class));
            finish();
        });

        fabAddReport.setOnClickListener(v -> {
            Intent intent = new Intent(RadiologyActivity.this, UploadRadiologyReportActivity.class);
            startActivity(intent);
        });

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        View navHome = findViewById(R.id.navHome);
        View navPathology = findViewById(R.id.navPathology);
        View navABHA = findViewById(R.id.navABHA);
        View navBMI = findViewById(R.id.navBMI);
        View navDevices = findViewById(R.id.navDevices);

        navHome.setOnClickListener(v -> {
            startActivity(new Intent(RadiologyActivity.this, DashboardActivity.class));
            finish();
        });

        navPathology.setOnClickListener(v -> {
            startActivity(new Intent(RadiologyActivity.this, ReportsActivity.class));
            finish();
        });

        navABHA.setOnClickListener(v -> {
            startActivity(new Intent(RadiologyActivity.this, ABHAActivity.class));
            finish();
        });

        navBMI.setOnClickListener(v -> {
            startActivity(new Intent(RadiologyActivity.this, BMIActivity.class));
            finish();
        });

        navDevices.setOnClickListener(v -> {
            startActivity(new Intent(RadiologyActivity.this, DevicesActivity.class));
            finish();
        });
    }
}
