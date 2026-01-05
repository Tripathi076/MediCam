package com.example.medicam;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WaterTrackerActivity extends AppCompatActivity {

    private TextView tvCurrentIntake, tvGoal, tvPercentage, tvGlassCount;
    private ProgressBar progressWater;
    private MaterialButton btnAdd250, btnAdd500, btnCustom;

    private int currentIntake = 0;
    private int dailyGoal = 2500; // ml
    private SharedPreferences sharedPreferences;

    private static final String PREF_NAME = "WaterTrackerPrefs";
    private static final String KEY_INTAKE = "water_intake";
    private static final String KEY_DATE = "intake_date";
    private static final String KEY_GOAL = "daily_goal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_tracker);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        
        initViews();
        loadData();
        updateUI();
    }

    private void initViews() {
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        tvCurrentIntake = findViewById(R.id.tvCurrentIntake);
        tvGoal = findViewById(R.id.tvGoal);
        tvPercentage = findViewById(R.id.tvPercentage);
        tvGlassCount = findViewById(R.id.tvGlassCount);
        progressWater = findViewById(R.id.progressWater);

        btnAdd250 = findViewById(R.id.btnAdd250);
        btnAdd500 = findViewById(R.id.btnAdd500);
        btnCustom = findViewById(R.id.btnCustom);

        btnAdd250.setOnClickListener(v -> addWater(250));
        btnAdd500.setOnClickListener(v -> addWater(500));
        btnCustom.setOnClickListener(v -> showCustomAmountDialog());

        // Quick add buttons
        findViewById(R.id.cardGlass).setOnClickListener(v -> addWater(250));
        findViewById(R.id.cardBottle).setOnClickListener(v -> addWater(500));
        findViewById(R.id.cardBigBottle).setOnClickListener(v -> addWater(1000));

        // Settings
        findViewById(R.id.btnSetGoal).setOnClickListener(v -> showSetGoalDialog());
        findViewById(R.id.btnReset).setOnClickListener(v -> resetIntake());
    }

    private void loadData() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String savedDate = sharedPreferences.getString(KEY_DATE, "");
        
        if (today.equals(savedDate)) {
            currentIntake = sharedPreferences.getInt(KEY_INTAKE, 0);
        } else {
            // New day, reset intake
            currentIntake = 0;
            saveData();
        }
        
        dailyGoal = sharedPreferences.getInt(KEY_GOAL, 2500);
    }

    private void saveData() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        sharedPreferences.edit()
                .putInt(KEY_INTAKE, currentIntake)
                .putString(KEY_DATE, today)
                .putInt(KEY_GOAL, dailyGoal)
                .apply();
    }

    private void addWater(int amount) {
        currentIntake += amount;
        saveData();
        updateUI();
        
        if (currentIntake >= dailyGoal) {
            Toast.makeText(this, "🎉 Daily goal reached!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "+" + amount + "ml added", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI() {
        tvCurrentIntake.setText(currentIntake + " ml");
        tvGoal.setText("Goal: " + dailyGoal + " ml");
        
        int percentage = (int) ((currentIntake * 100.0) / dailyGoal);
        if (percentage > 100) percentage = 100;
        tvPercentage.setText(percentage + "%");
        progressWater.setProgress(percentage);
        
        int glasses = currentIntake / 250;
        tvGlassCount.setText(glasses + " glasses");
    }

    private void showCustomAmountDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Add Custom Amount");
        
        final android.widget.EditText input = new android.widget.EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        input.setHint("Amount in ml");
        builder.setView(input);
        
        builder.setPositiveButton("Add", (dialog, which) -> {
            String value = input.getText().toString();
            if (!value.isEmpty()) {
                addWater(Integer.parseInt(value));
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showSetGoalDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Set Daily Goal");
        
        final android.widget.EditText input = new android.widget.EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        input.setHint("Goal in ml");
        input.setText(String.valueOf(dailyGoal));
        builder.setView(input);
        
        builder.setPositiveButton("Save", (dialog, which) -> {
            String value = input.getText().toString();
            if (!value.isEmpty()) {
                dailyGoal = Integer.parseInt(value);
                saveData();
                updateUI();
                Toast.makeText(this, "Goal set to " + dailyGoal + "ml", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void resetIntake() {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Reset Intake")
                .setMessage("Reset today's water intake to 0?")
                .setPositiveButton("Reset", (dialog, which) -> {
                    currentIntake = 0;
                    saveData();
                    updateUI();
                    Toast.makeText(this, "Intake reset", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
