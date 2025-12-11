package com.example.medicam;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class LoginUnsuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_unsuccess);

        MaterialButton btnTryAgain = findViewById(R.id.btnTryAgain);
        TextView tvBackToLogin = findViewById(R.id.tvBackToLogin);

        // Try Again button - go back to OTP screen
        btnTryAgain.setOnClickListener(v -> {
            finish();
        });

        // Back to Login - go to phone login
        tvBackToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(LoginUnsuccessActivity.this, PhoneLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }
}
