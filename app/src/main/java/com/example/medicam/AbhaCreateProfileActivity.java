package com.example.medicam;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class AbhaCreateProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abha_create_profile);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnSendOtp).setOnClickListener(v ->
                startActivity(new Intent(this, AbhaVerifyAadhaarOtpActivity.class)));
    }
}
