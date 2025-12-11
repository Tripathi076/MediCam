package com.example.medicam;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class CreateNewPasswordActivity extends AppCompatActivity {

    private EditText etNewPassword, etConfirmPassword;
    private MaterialButton btnResetPassword;
    private ImageView btnBack;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_password);

        // Get email from intent
        email = getIntent().getStringExtra("email");

        // Initialize views
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        btnBack = findViewById(R.id.btnBack);

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Enable button when both passwords are filled
        TextWatcher passwordWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

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

        // TODO: Update password in Firebase
        // For now, show success and navigate to password reset success screen
        Intent intent = new Intent(CreateNewPasswordActivity.this, PasswordResetSuccessActivity.class);
        startActivity(intent);
        finish();
    }
}
