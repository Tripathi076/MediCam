package com.example.medicam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class AbhaLinkMobileActivity extends AppCompatActivity {

    private static final String PREF_NAME = "medicam_pref";
    private TextInputEditText etMobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abha_link_mobile);

        etMobile = findViewById(R.id.etMobile);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        
        findViewById(R.id.btnSendMobileOtp).setOnClickListener(v -> {
            String mobile = etMobile != null ? etMobile.getText().toString().trim() : "";
            
            if (mobile.length() != 10) {
                Toast.makeText(this, "Please enter valid 10-digit mobile number", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Save mobile to SharedPreferences
            SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            prefs.edit().putString("abha_mobile", mobile).apply();
            
            startActivity(new Intent(this, AbhaVerifyMobileOtpActivity.class));
        });
    }
}
