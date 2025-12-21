package com.example.medicam;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class AbhaVerifyMobileOtpActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abha_verify_mobile_otp);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnVerifyOtp).setOnClickListener(v ->
                startActivity(new Intent(this, AbhaChooseUsernameActivity.class)));
    }
}
