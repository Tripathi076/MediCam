package com.example.medicam;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;

public class ResetPasswordOTPActivity extends AppCompatActivity {

    private EditText etOTP1, etOTP2, etOTP3, etOTP4;
    private MaterialButton btnVerifyCode;
    private TextView tvEmail, tvResendCode, tvTimer;
    private ImageView btnBack;
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
        tvEmail = findViewById(R.id.tvEmail);
        tvResendCode = findViewById(R.id.tvResendCode);
        tvTimer = findViewById(R.id.tvTimer);
        btnBack = findViewById(R.id.btnBack);

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
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

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
        String enteredOTP = etOTP1.getText().toString() + etOTP2.getText().toString() +
            etOTP3.getText().toString() + etOTP4.getText().toString();

        // TODO: Verify OTP with Firebase
        // For demo, accept "1234" as correct
        if (enteredOTP.equals("1234")) {
            Intent intent = new Intent(ResetPasswordOTPActivity.this, CreateNewPasswordActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Invalid verification code", Toast.LENGTH_SHORT).show();
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
        // Clear OTP fields
        etOTP1.setText("");
        etOTP2.setText("");
        etOTP3.setText("");
        etOTP4.setText("");
        etOTP1.requestFocus();
        
        Toast.makeText(this, "Verification code resent to " + email, Toast.LENGTH_SHORT).show();
        // TODO: Resend OTP via Firebase
        startResendTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
