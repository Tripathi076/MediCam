package com.example.medicam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medicam.views.BMIGaugeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BMIResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi_result);

        double bmi = getIntent().getDoubleExtra("bmi", 0);
        String gender = getIntent().getStringExtra("gender");
        double height = getIntent().getDoubleExtra("height", 0);
        double weight = getIntent().getDoubleExtra("weight", 0);
        String category = getBMICategory(bmi);

        TextView tvBmiValue = findViewById(R.id.tvBmiValue);
        TextView tvBmiCategory = findViewById(R.id.tvBmiCategory);
        BMIGaugeView bmiGaugeView = findViewById(R.id.bmiGaugeView);
        View btnSave = findViewById(R.id.btnSaveResult);
        View backBtn = findViewById(R.id.btnBack);

        // Set BMI value and update gauge
        tvBmiValue.setText(String.format("%.1f", bmi));
        tvBmiCategory.setText(category);
        bmiGaugeView.setBMI((float) bmi);

        btnSave.setOnClickListener(v -> {
            saveBMIResult(bmi, category, gender, height, weight);
            Toast.makeText(BMIResultActivity.this, "BMI Result Saved", Toast.LENGTH_SHORT).show();
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

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

    private void saveBMIResult(double bmi, String category, String gender, double height, double weight) {
        SharedPreferences prefs = getSharedPreferences("BMI_DATA", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        
        // Get existing records
        String bmiListJson = prefs.getString("bmiRecords", "[]");
        Gson gson = new Gson();
        Type type = new TypeToken<List<BMIRecord>>(){}.getType();
        List<BMIRecord> bmiRecords = gson.fromJson(bmiListJson, type);
        if (bmiRecords == null) {
            bmiRecords = new ArrayList<>();
        }
        
        // Create new record
        BMIRecord record = new BMIRecord();
        record.setWeight(weight);
        record.setHeight(height);
        record.setBmiValue(bmi);
        record.setCategory(category);
        record.setGender(gender);
        record.setDate(getCurrentDate());
        
        // Add to list
        bmiRecords.add(record);
        
        // Save back to SharedPreferences
        String updatedJson = gson.toJson(bmiRecords);
        editor.putString("bmiRecords", updatedJson);
        editor.apply();
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }

    private String getBMICategory(double bmi) {
        if (bmi < 18.5) {
            return "Your BMI indicates you are Underweight. Consult a doctor for personalized health guidance.";
        } else if (bmi < 25) {
            return "Your BMI indicates you are at a Normal/Healthy weight. Keep up your healthy lifestyle!";
        } else if (bmi < 30) {
            return "Your BMI indicates you are Overweight. Consider a balanced diet and regular exercise.";
        } else if (bmi < 35) {
            return "Your BMI indicates you are Obese. Consult a healthcare professional for advice.";
        } else {
            return "Your BMI indicates Extreme Obesity. Professional medical guidance is recommended.";
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
                startActivity(new Intent(this, ReportsActivity.class));
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
