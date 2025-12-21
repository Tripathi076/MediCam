package com.example.medicam;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class BMIGenderActivity extends AppCompatActivity {
    private String selectedGender = "male";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi_gender);

        View maleBtn = findViewById(R.id.btnMale);
        View femaleBtn = findViewById(R.id.btnFemale);
        View nextBtn = findViewById(R.id.btnNext);
        View backBtn = findViewById(R.id.btnBack);

        maleBtn.setOnClickListener(v -> {
            selectedGender = "male";
            maleBtn.setBackground(getDrawable(R.drawable.bg_gender_selected));
            femaleBtn.setBackground(getDrawable(R.drawable.bg_gender_unselected));
        });

        femaleBtn.setOnClickListener(v -> {
            selectedGender = "female";
            femaleBtn.setBackground(getDrawable(R.drawable.bg_gender_selected));
            maleBtn.setBackground(getDrawable(R.drawable.bg_gender_unselected));
        });

        nextBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, BMIInputActivity.class);
            intent.putExtra("gender", selectedGender);
            startActivity(intent);
        });

        backBtn.setOnClickListener(v -> finish());
        setupBottomNavigation();
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
