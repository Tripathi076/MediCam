package com.example.medicam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AbhaCreateProfileActivity extends AppCompatActivity {

    private static final String PREF_NAME = "medicam_pref";
    private EditText etAadhaar1, etAadhaar2, etAadhaar3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abha_create_profile);

        etAadhaar1 = findViewById(R.id.etAadhaar1);
        etAadhaar2 = findViewById(R.id.etAadhaar2);
        etAadhaar3 = findViewById(R.id.etAadhaar3);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        
        findViewById(R.id.btnSendOtp).setOnClickListener(v -> {
            String aadhaar = getAadhaarNumber();
            if (aadhaar.length() != 12) {
                Toast.makeText(this, "Please enter valid 12-digit Aadhaar number", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Save Aadhaar (masked) to SharedPreferences
            SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            prefs.edit().putString("abha_aadhaar", maskAadhaar(aadhaar)).apply();
            
            startActivity(new Intent(this, AbhaVerifyAadhaarOtpActivity.class));
        });
    }
    
    private String getAadhaarNumber() {
        String part1 = etAadhaar1 != null ? etAadhaar1.getText().toString() : "";
        String part2 = etAadhaar2 != null ? etAadhaar2.getText().toString() : "";
        String part3 = etAadhaar3 != null ? etAadhaar3.getText().toString() : "";
        return part1 + part2 + part3;
    }
    
    private String maskAadhaar(String aadhaar) {
        if (aadhaar.length() >= 12) {
            return "XXXX-XXXX-" + aadhaar.substring(8);
        }
        return aadhaar;
    }
}
