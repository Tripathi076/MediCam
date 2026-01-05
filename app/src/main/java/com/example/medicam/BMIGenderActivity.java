package com.example.medicam;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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
        MaterialButton viewHistoryBtn = findViewById(R.id.btnViewHistory);

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
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        viewHistoryBtn.setOnClickListener(v -> showBMIHistory());

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

    private void showBMIHistory() {
        // Get last 3 BMI records from SharedPreferences
        android.content.SharedPreferences prefs = getSharedPreferences("BMI_DATA", MODE_PRIVATE);
        String bmiListJson = prefs.getString("bmiRecords", "[]");
        
        Gson gson = new Gson();
        Type type = new TypeToken<List<BMIRecord>>(){}.getType();
        List<BMIRecord> bmiRecords = gson.fromJson(bmiListJson, type);
        
        // Get last 3 records
        List<BMIRecord> last3Records = new ArrayList<>();
        if (bmiRecords != null && !bmiRecords.isEmpty()) {
            int startIndex = Math.max(0, bmiRecords.size() - 3);
            for (int i = bmiRecords.size() - 1; i >= startIndex; i--) {
                last3Records.add(bmiRecords.get(i));
            }
        }
        
        if (last3Records.isEmpty()) {
            Toast.makeText(this, "No BMI history found", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Show dialog with history
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("BMI History (Last 3 Records)");
        
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_bmi_history, null);
        RecyclerView recyclerView = dialogView.findViewById(R.id.historyRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        BMIHistoryAdapter adapter = new BMIHistoryAdapter(last3Records);
        recyclerView.setAdapter(adapter);
        
        builder.setView(dialogView);
        builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
        builder.show();
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
                Intent intent = new Intent(this, PathologyActivity.class);
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
