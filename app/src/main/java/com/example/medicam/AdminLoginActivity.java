package com.example.medicam;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medicam.utils.FirebaseErrorHandler;
import com.example.medicam.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class AdminLoginActivity extends AppCompatActivity {
    private static final String TAG = "AdminLoginActivity";
    
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        
        mAuth = FirebaseAuth.getInstance();

        ImageView btnBack = findViewById(R.id.btnBack);
        TextInputEditText emailEditText = findViewById(R.id.emailEditText);
        TextInputEditText passwordEditText = findViewById(R.id.passwordEditText);
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);
        MaterialButton btnLoginAction = findViewById(R.id.btnLoginAction);
        TextView tvSignUp = findViewById(R.id.tvSignUp);
        progressBar = findViewById(R.id.progressBar);

        btnBack.setOnClickListener(v -> finish());

        btnLoginAction.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            } else {
                loginWithEmail(email, password);
            }
        });

        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(AdminLoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(AdminLoginActivity.this, LoginSignUpActivity.class);
            startActivity(intent);
        });
    }
    
    private void loginWithEmail(String email, String password) {
        try {
            progressBar.setVisibility(View.VISIBLE);
            
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        try {
                            if (task.isSuccessful()) {
                                String userId = mAuth.getCurrentUser().getUid();
                                SessionManager sessionManager = SessionManager.getInstance(AdminLoginActivity.this);
                                sessionManager.saveUserSession(userId, email, "", "");
                                showLoginSuccessDialog();
                                Log.d(TAG, "Login successful for: " + email);
                            } else {
                                Exception e = task.getException();
                                String errorMsg = FirebaseErrorHandler.getAuthErrorMessage(e);
                                Toast.makeText(AdminLoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Login failed: " + e.getLocalizedMessage(), e);
                            }
                        } catch (Exception e) {
                            FirebaseErrorHandler.logException("loginWithEmail callback", e);
                        }
                    });
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            String errorMsg = FirebaseErrorHandler.getAuthErrorMessage(e);
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            FirebaseErrorHandler.logException("loginWithEmail", e);
        }
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
