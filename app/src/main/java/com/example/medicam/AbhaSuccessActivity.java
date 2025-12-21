package com.example.medicam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class AbhaSuccessActivity extends AppCompatActivity {

    private static final String PREF_NAME = "medicam_pref";
    private static final String KEY_ABHA_REGISTERED = "abha_registered";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abha_success);

        // Save ABHA registration status
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_ABHA_REGISTERED, true);
        
        editor.apply();

        // Find button to proceed to card view
        MaterialButton btnProceed = findViewById(R.id.btnProceed);
        if (btnProceed != null) {
            btnProceed.setOnClickListener(v -> {
                // Navigate to ABHA Card Display screen
                Intent intent = new Intent(AbhaSuccessActivity.this, ABHACardDisplayActivity.class);
                startActivity(intent);
                finish();
            });
        }

        View card = findViewById(R.id.tvSuccessMessage);
        if (card != null) {
            card.setOnClickListener(v -> {
                Intent intent = new Intent(AbhaSuccessActivity.this, ABHACardDisplayActivity.class);
                startActivity(intent);
                finish();
            });
        }
    }
}

