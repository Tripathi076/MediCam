package com.example.medicam;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class BMIResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi_result);

        double bmi = getIntent().getDoubleExtra("bmi", 0);
        String category = getBMICategory(bmi);

        TextView tvBmiValue = findViewById(R.id.tvBmiValue);
        TextView tvBmiCategory = findViewById(R.id.tvBmiCategory);
        View btnSave = findViewById(R.id.btnSaveResult);
        View backBtn = findViewById(R.id.btnBack);

        tvBmiValue.setText(String.format("%.1f", bmi));
        tvBmiCategory.setText(category);

        btnSave.setOnClickListener(v -> {
            // TODO: Save BMI result to database or SharedPreferences
            finish();
        });

        backBtn.setOnClickListener(v -> finish());
        setupBottomNavigation();
    }

    private String getBMICategory(double bmi) {
        if (bmi < 18.5) {
            return "indicating your weight is in the Underweight category for adults of your height.";
        } else if (bmi < 25) {
            return "indicating your weight is in the Normal weight category for adults of your height.";
        } else if (bmi < 30) {
            return "indicating your weight is in the Overweight category for adults of your height.";
        } else {
            return "indicating your weight is in the Obese category for adults of your height.";
        }
    }

    private void setupBottomNavigation() {
        View navHome = findViewById(R.id.navHome);
        View navPathology = findViewById(R.id.navPathology);
        View navABHA = findViewById(R.id.navABHA);
        View navBMI = findViewById(R.id.navBMI);
        View navDevices = findViewById(R.id.navDevices);

        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                startActivity(new Intent(this, DashboardActivity.class));
                finish();
            });
        }
        if (navPathology != null) {
            navPathology.setOnClickListener(v -> {
                startActivity(new Intent(this, PathologyActivity.class));
                finish();
            });
        }
        if (navABHA != null) {
            navABHA.setOnClickListener(v -> {
                startActivity(new Intent(this, ABHAActivity.class));
                finish();
            });
        }
        if (navBMI != null) {
            navBMI.setOnClickListener(v -> {});
        }
        if (navDevices != null) {
            navDevices.setOnClickListener(v -> {
                startActivity(new Intent(this, DevicesActivity.class));
                finish();
            });
        }
    }
}
