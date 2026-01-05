package com.example.medicam;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class BMIInputActivity extends AppCompatActivity {
    private TextInputEditText etHeight, etWeight, etAge;
    private String gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi_input);

        gender = getIntent().getStringExtra("gender");

        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);
        etAge = findViewById(R.id.etAge);

        View calculateBtn = findViewById(R.id.btnCalculate);
        View backBtn = findViewById(R.id.btnBack);

        calculateBtn.setOnClickListener(v -> calculateBMI());
        backBtn.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
        setupBottomNavigation();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void calculateBMI() {
        String heightStr = etHeight.getText().toString().trim();
        String weightStr = etWeight.getText().toString().trim();
        String ageStr = etAge.getText().toString().trim();

        if (TextUtils.isEmpty(heightStr) || TextUtils.isEmpty(weightStr) || TextUtils.isEmpty(ageStr)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double heightCm = Double.parseDouble(heightStr);
            double weight = Double.parseDouble(weightStr);
            int age = Integer.parseInt(ageStr);

            // Validate reasonable ranges
            if (heightCm < 50 || heightCm > 300) {
                Toast.makeText(this, "Please enter height between 50-300 cm", Toast.LENGTH_SHORT).show();
                return;
            }
            if (weight < 10 || weight > 500) {
                Toast.makeText(this, "Please enter weight between 10-500 kg", Toast.LENGTH_SHORT).show();
                return;
            }
            if (age < 1 || age > 120) {
                Toast.makeText(this, "Please enter age between 1-120 years", Toast.LENGTH_SHORT).show();
                return;
            }

            double heightM = heightCm / 100.0;
            double bmi = weight / (heightM * heightM);

            Intent intent = new Intent(this, BMIResultActivity.class);
            intent.putExtra("bmi", bmi);
            intent.putExtra("gender", gender);
            intent.putExtra("age", age);
            intent.putExtra("height", heightCm);
            intent.putExtra("weight", weight);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
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
                Intent intent = new Intent(this, DashboardActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            });
        }
        if (navPathology != null) {
            navPathology.setOnClickListener(v -> {
                Intent intent = new Intent(this, ReportsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            });
        }
        if (navABHA != null) {
            navABHA.setOnClickListener(v -> {
                Intent intent = new Intent(this, ABHAActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
