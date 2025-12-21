package com.example.medicam;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class AbhaLinkMobileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abha_link_mobile);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnSendMobileOtp).setOnClickListener(v ->
                startActivity(new Intent(this, AbhaVerifyMobileOtpActivity.class)));
    }
}
