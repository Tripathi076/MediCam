package com.example.medicam;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medicam.utils.FirebaseErrorHandler;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CreateNewPasswordActivity extends AppCompatActivity {
    private static final String TAG = "CreateNewPasswordActivity";

    private EditText etNewPassword;
    private EditText etConfirmPassword;
    private MaterialButton btnResetPassword;
    private ProgressBar progressBar;
    
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_password);

        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        ImageView btnBack = findViewById(R.id.btnBack);
        progressBar = findViewById(R.id.progressBar);

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Enable button when both passwords are filled
        TextWatcher passwordWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Empty implementation - no action needed before text change
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Empty implementation - password validation happens in afterTextChanged
            }

            @Override
            public void afterTextChanged(Editable s) {
                String newPass = etNewPassword.getText().toString().trim();
                String confirmPass = etConfirmPassword.getText().toString().trim();
                btnResetPassword.setEnabled(!newPass.isEmpty() && !confirmPass.isEmpty());
            }
        };

        etNewPassword.addTextChangedListener(passwordWatcher);
        etConfirmPassword.addTextChangedListener(passwordWatcher);

        // Reset password button
        btnResetPassword.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword() {
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPassword.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        updatePasswordInFirebase(newPassword);
    }
    
    private void updatePasswordInFirebase(String newPassword) {
        try {
            progressBar.setVisibility(View.VISIBLE);
            btnResetPassword.setEnabled(false);
            
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                user.updatePassword(newPassword)
                        .addOnCompleteListener(task -> {
                            progressBar.setVisibility(View.GONE);
                            btnResetPassword.setEnabled(true);
                            
                            try {
                                if (task.isSuccessful()) {
                                    Toast.makeText(CreateNewPasswordActivity.this, 
                                        "Password updated successfully", 
                                        Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "Password updated successfully");
                                    
                                    Intent intent = new Intent(CreateNewPasswordActivity.this, 
                                        PasswordResetSuccessActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    String errorMsg = FirebaseErrorHandler.getAuthErrorMessage(task.getException());
                                    Toast.makeText(CreateNewPasswordActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                FirebaseErrorHandler.logException("updatePassword callback", e);
                            }
                        });
            } else {
                progressBar.setVisibility(View.GONE);
                btnResetPassword.setEnabled(true);
                Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            btnResetPassword.setEnabled(true);
            String errorMsg = FirebaseErrorHandler.getAuthErrorMessage(e);
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            FirebaseErrorHandler.logException("updatePasswordInFirebase", e);
        }
    }
}
