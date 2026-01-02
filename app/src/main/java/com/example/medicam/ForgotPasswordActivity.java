package com.example.medicam;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medicam.utils.FirebaseErrorHandler;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    private static final String TAG = "ForgotPasswordActivity";

    private ProgressBar progressBar;
    
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        EditText etEmail = findViewById(R.id.etEmail);
        MaterialButton btnSendVerification = findViewById(R.id.btnSendVerification);
        ImageView btnBack = findViewById(R.id.btnBack);
        TextView tvBackToLogin = findViewById(R.id.tvBackToLogin);
        progressBar = findViewById(R.id.progressBar);

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
                sendPasswordResetEmail(email);
            }
        });

        // Back to login
        tvBackToLogin.setOnClickListener(v -> finish());
    }
    
    private void sendPasswordResetEmail(String email) {
        try {
            progressBar.setVisibility(View.VISIBLE);
            btnSendVerification.setEnabled(false);
            
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        btnSendVerification.setEnabled(true);
                        
                        try {
                            if (task.isSuccessful()) {
                                Toast.makeText(ForgotPasswordActivity.this, 
                                    "Password reset email sent successfully", 
                                    Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "Password reset email sent to: " + email);
                                
                                // Navigate to reset password screen
                                Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordOTPActivity.class);
                                intent.putExtra("email", email);
                                startActivity(intent);
                                finish();
                            } else {
                                String errorMsg = FirebaseErrorHandler.getAuthErrorMessage(task.getException());
                                Toast.makeText(ForgotPasswordActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Failed to send reset email: " + task.getException().getLocalizedMessage());
                            }
                        } catch (Exception e) {
                            FirebaseErrorHandler.logException("sendPasswordResetEmail callback", e);
                        }
                    });
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            btnSendVerification.setEnabled(true);
            String errorMsg = FirebaseErrorHandler.getAuthErrorMessage(e);
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            FirebaseErrorHandler.logException("sendPasswordResetEmail", e);
        }
    }
}
