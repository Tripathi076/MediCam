package com.example.medicam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.UUID;

public class AbhaChooseUsernameActivity extends AppCompatActivity {

    private static final String PREF_NAME = "medicam_pref";
    private TextInputEditText etUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abha_choose_username);

        etUserName = findViewById(R.id.etUserName);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        
        findViewById(R.id.btnContinue).setOnClickListener(v -> {
            String username = etUserName != null ? etUserName.getText().toString().trim() : "";
            
            if (username.length() < 8) {
                Toast.makeText(this, "Username must be at least 8 characters", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Save username and generate ABHA number to SharedPreferences
            SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            
            // Generate ABHA address
            String abhaAddress = username + "@abdm";
            editor.putString("abha_address", abhaAddress);
            
            // Generate random ABHA number (14 digits format: XX-XXXX-XXXX-XXXX)
            String abhaNumber = generateAbhaNumber();
            editor.putString("abha_number", abhaNumber);
            
            // Use username as display name if no name saved
            if (!prefs.contains("abha_name")) {
                editor.putString("abha_name", capitalizeFirstLetter(username));
            }
            
            editor.apply();
            
            startActivity(new Intent(this, AbhaSuccessActivity.class));
            finish();
        });
    }
    
    private String generateAbhaNumber() {
        // Generate random 14-digit ABHA number
        StringBuilder sb = new StringBuilder();
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < 14; i++) {
            sb.append(random.nextInt(10));
        }
        String num = sb.toString();
        // Format: XX-XXXX-XXXX-XXXX
        return num.substring(0, 2) + "-" + num.substring(2, 6) + "-" + num.substring(6, 10) + "-" + num.substring(10, 14);
    }
    
    private String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
