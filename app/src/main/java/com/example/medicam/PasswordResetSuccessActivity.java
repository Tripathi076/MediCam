package com.example.medicam;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class PasswordResetSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset_success);

        MaterialButton btnBackToLogin = findViewById(R.id.btnBackToLogin);

        // Back to login button
        btnBackToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(PasswordResetSuccessActivity.this, AdminLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
