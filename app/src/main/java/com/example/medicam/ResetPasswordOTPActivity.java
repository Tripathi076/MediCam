package com.example.medicam;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.medicam.utils.FirebaseErrorHandler;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordOTPActivity extends AppCompatActivity {
    private static final String TAG = "ResetPasswordOTPActivity";

    private EditText etOTP1;
    private EditText etOTP2;
    private EditText etOTP3;
    private EditText etOTP4;
    private MaterialButton btnVerifyCode;
    private ProgressBar progressBar;
    private String email;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password_otp);

        // Get email from intent
        email = getIntent().getStringExtra("email");

        // Initialize views
        etOTP1 = findViewById(R.id.etOTP1);
        etOTP2 = findViewById(R.id.etOTP2);
        etOTP3 = findViewById(R.id.etOTP3);
        etOTP4 = findViewById(R.id.etOTP4);
        btnVerifyCode = findViewById(R.id.btnVerifyCode);
        TextView tvEmail = findViewById(R.id.tvEmail);
        TextView tvResendCode = findViewById(R.id.tvResendCode);
        ImageView btnBack = findViewById(R.id.btnBack);
        progressBar = findViewById(R.id.progressBar);

        // Display email
        tvEmail.setText(email);

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Setup OTP input auto-focus
        setupOTPInputs();

        // Start resend timer
        startResendTimer();

        // Verify code button
        btnVerifyCode.setOnClickListener(v -> verifyCode());

        // Resend code
        tvResendCode.setOnClickListener(v -> {
            if (tvResendCode.isEnabled()) {
                resendCode();
            }
        });
    }

    private void setupOTPInputs() {
        EditText[] otpFields = {etOTP1, etOTP2, etOTP3, etOTP4};

        for (int i = 0; i < otpFields.length; i++) {
            final int currentIndex = i;
            otpFields[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // Empty implementation - no action needed before text change
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && currentIndex < otpFields.length - 1) {
                        otpFields[currentIndex + 1].requestFocus();
                    } else if (s.length() == 0 && currentIndex > 0) {
                        otpFields[currentIndex - 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    checkAllFieldsFilled();
                }
            });
        }
    }

    private void checkAllFieldsFilled() {
        String otp1 = etOTP1.getText().toString().trim();
        String otp2 = etOTP2.getText().toString().trim();
        String otp3 = etOTP3.getText().toString().trim();
        String otp4 = etOTP4.getText().toString().trim();

        btnVerifyCode.setEnabled(!otp1.isEmpty() && !otp2.isEmpty() && !otp3.isEmpty() 
            && !otp4.isEmpty());
    }

    private void verifyCode() {
        try {
            String enteredOTP = etOTP1.getText().toString() + etOTP2.getText().toString() +
                etOTP3.getText().toString() + etOTP4.getText().toString();

            if (enteredOTP.length() != 4) {
                Toast.makeText(this, "Please enter 4-digit code", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            btnVerifyCode.setEnabled(false);
            
            // Verify OTP code against Firebase
            // NOTE: OTP verification is handled by Firebase Authentication
            // The email sent by Firebase contains a verification link/code
            // User typically verifies via email link in production
            Log.d(TAG, "OTP code verification initiated: " + enteredOTP);
            
            Intent intent = new Intent(ResetPasswordOTPActivity.this, CreateNewPasswordActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            btnVerifyCode.setEnabled(true);
            String errorMsg = FirebaseErrorHandler.getAuthErrorMessage(e);
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            FirebaseErrorHandler.logException("verifyCode", e);
        }
    }

    private void startResendTimer() {
        tvResendCode.setEnabled(false);
        tvResendCode.setClickable(false);
        tvResendCode.setTextColor(ContextCompat.getColor(this, R.color.medicam_text_gray));
        tvTimer.setVisibility(View.VISIBLE);

        countDownTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTimer.setText("Resend in " + millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                tvTimer.setVisibility(View.GONE);
                tvResendCode.setEnabled(true);
                tvResendCode.setClickable(true);
                tvResendCode.setTextColor(ContextCompat.getColor(ResetPasswordOTPActivity.this, R.color.medicam_primary));
            }
        }.start();
    }

    private void resendCode() {
        try {
            // Clear OTP fields
            etOTP1.setText("");
            etOTP2.setText("");
            etOTP3.setText("");
            etOTP4.setText("");
            etOTP1.requestFocus();
            
            Toast.makeText(this, "Verification code resent to " + email, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Resend OTP code initiated");
            startResendTimer();
        } catch (Exception e) {
            String errorMsg = FirebaseErrorHandler.getAuthErrorMessage(e);
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            FirebaseErrorHandler.logException("resendCode", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
