package com.example.medicam;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etEmail;
    private MaterialButton btnSendVerification;
    private ImageView btnBack;
    private TextView tvBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initialize views
        etEmail = findViewById(R.id.etEmail);
        btnSendVerification = findViewById(R.id.btnSendVerification);
        btnBack = findViewById(R.id.btnBack);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Send verification code
        btnSendVerification.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            } else {
                // TODO: Send verification code via Firebase
                // For now, navigate to verification screen
                Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordOTPActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });

        // Back to login
        tvBackToLogin.setOnClickListener(v -> finish());
    }
}
