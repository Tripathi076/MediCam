package com.example.medicam;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.example.medicam.models.UserProfile;

public class HealthInformationActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvBloodGroup, tvHeight, tvWeight, tvBMI, tvAllergies, tvConditions;

    private SharedPreferences prefs;
    private Gson gson;

    private static final String PREFS_NAME = "ProfilePrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_information);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        gson = new Gson();

        initViews();
        loadHealthData();
        setupClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvBloodGroup = findViewById(R.id.tvBloodGroup);
        tvHeight = findViewById(R.id.tvHeight);
        tvWeight = findViewById(R.id.tvWeight);
        tvBMI = findViewById(R.id.tvBMI);
        tvAllergies = findViewById(R.id.tvAllergies);
        tvConditions = findViewById(R.id.tvConditions);
    }

    private void loadHealthData() {
        String json = prefs.getString("user_profile", null);
        if (json != null) {
            UserProfile profile = gson.fromJson(json, UserProfile.class);
            if (profile != null) {
                tvBloodGroup.setText(profile.getBloodGroup() != null ? profile.getBloodGroup() : "Not set");
                
                float height = profile.getHeight();
                float weight = profile.getWeight();
                
                tvHeight.setText(height > 0 ? String.format("%.0f cm", height) : "Not set");
                tvWeight.setText(weight > 0 ? String.format("%.0f kg", weight) : "Not set");
                
                float bmi = profile.getBMI();
                if (bmi > 0) {
                    String bmiCategory = getBMICategory(bmi);
                    tvBMI.setText(String.format("%.1f (%s)", bmi, bmiCategory));
                } else {
                    tvBMI.setText("Not calculated");
                }
            }
        }
        
        // Placeholder for allergies and conditions
        tvAllergies.setText("None recorded");
        tvConditions.setText("None recorded");
    }

    private String getBMICategory(float bmi) {
        if (bmi < 18.5) return "Underweight";
        if (bmi < 25) return "Normal";
        if (bmi < 30) return "Overweight";
        return "Obese";
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
    }
}
