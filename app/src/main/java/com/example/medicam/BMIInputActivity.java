package com.example.medicam;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class BMIInputActivity extends AppCompatActivity {
    private TextInputEditText etHeightLeft, etHeightRight, etWeight, etAge;
    private String gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi_input);

        gender = getIntent().getStringExtra("gender");

        etHeightLeft = findViewById(R.id.etHeightLeft);
        etHeightRight = findViewById(R.id.etHeightRight);
        etWeight = findViewById(R.id.etWeight);
        etAge = findViewById(R.id.etAge);

        View calculateBtn = findViewById(R.id.btnCalculate);
        View backBtn = findViewById(R.id.btnBack);

        calculateBtn.setOnClickListener(v -> calculateBMI());
        backBtn.setOnClickListener(v -> finish());
        setupBottomNavigation();
    }

    private void calculateBMI() {
        String heightLeftStr = etHeightLeft.getText().toString().trim();
        String heightRightStr = etHeightRight.getText().toString().trim();
        String weightStr = etWeight.getText().toString().trim();
        String ageStr = etAge.getText().toString().trim();

        if (TextUtils.isEmpty(heightLeftStr) || TextUtils.isEmpty(heightRightStr) ||
                TextUtils.isEmpty(weightStr) || TextUtils.isEmpty(ageStr)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int height1 = Integer.parseInt(heightLeftStr);
            int height2 = Integer.parseInt(heightRightStr);
            double totalHeightCm = height1 + height2 / 10.0;
            double weight = Double.parseDouble(weightStr);
            int age = Integer.parseInt(ageStr);

            double heightM = totalHeightCm / 100.0;
            double bmi = weight / (heightM * heightM);

            Intent intent = new Intent(this, BMIResultActivity.class);
            intent.putExtra("bmi", bmi);
            intent.putExtra("gender", gender);
            intent.putExtra("age", age);
            startActivity(intent);
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
