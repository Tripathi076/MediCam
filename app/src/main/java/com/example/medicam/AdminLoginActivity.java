package com.example.medicam;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AdminLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        ImageView btnBack = findViewById(R.id.btnBack);
        TextInputEditText emailEditText = findViewById(R.id.emailEditText);
        TextInputEditText passwordEditText = findViewById(R.id.passwordEditText);
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);
        MaterialButton btnLoginAction = findViewById(R.id.btnLoginAction);
        TextView tvSignUp = findViewById(R.id.tvSignUp);

        btnBack.setOnClickListener(v -> finish());

        btnLoginAction.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            } else {
                // TODO: Integrate Firebase Email/Password Authentication
                // For demo: accept admin@medicam.com with password "admin123"
                if (email.equals("admin@medicam.com") && password.equals("admin123")) {
                    showLoginSuccessDialog();
                } else {
                    Intent intent = new Intent(AdminLoginActivity.this, LoginUnsuccessActivity.class);
                    startActivity(intent);
                }
            }
        });

        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(AdminLoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        tvSignUp.setOnClickListener(v -> {
            // Navigate to Admin Sign Up Screen (LoginSignUpActivity)
            Intent intent = new Intent(AdminLoginActivity.this, LoginSignUpActivity.class);
            startActivity(intent);
        });
    }

    private void showLoginSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_login_success, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        MaterialButton btnGoToDashboard = dialogView.findViewById(R.id.btnGoToDashboard);
        btnGoToDashboard.setOnClickListener(v -> {
            dialog.dismiss();
            // Navigate to Dashboard
            Intent intent = new Intent(AdminLoginActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish(); // Close login activity so user can't go back to it
        });

        dialog.show();
    }
}
