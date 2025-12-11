package com.example.medicam;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;

public class OTPVerificationActivity extends AppCompatActivity {

    private EditText etOTP1, etOTP2, etOTP3, etOTP4;
    private MaterialButton btnVerifyOTP;
    private TextView tvPhoneNumber, tvResendOTP, tvTimer;
    private String phoneNumber;
    private String fromScreen;
    private String name, password, state;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        // Get phone number from intent
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        if (phoneNumber == null) {
            phoneNumber = getIntent().getStringExtra("PHONE_NUMBER");
        }
        
        // Get additional data if coming from signup
        fromScreen = getIntent().getStringExtra("FROM");
        name = getIntent().getStringExtra("NAME");
        password = getIntent().getStringExtra("PASSWORD");
        state = getIntent().getStringExtra("STATE");

        // Initialize views
        etOTP1 = findViewById(R.id.etOTP1);
        etOTP2 = findViewById(R.id.etOTP2);
        etOTP3 = findViewById(R.id.etOTP3);
        etOTP4 = findViewById(R.id.etOTP4);
        btnVerifyOTP = findViewById(R.id.btnVerifyOTP);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        tvResendOTP = findViewById(R.id.tvResendOTP);
        tvTimer = findViewById(R.id.tvTimer);

    // Display masked phone number (+91 ****** last 4 digits)
    String maskedPhone = maskPhoneNumber(phoneNumber);
    tvPhoneNumber.setText(maskedPhone);

        // Setup OTP input auto-focus
        setupOTPInputs();

        // Start resend timer
        startResendTimer();

        // Verify OTP button click
        btnVerifyOTP.setOnClickListener(v -> verifyOTP());

        // Resend OTP click
        tvResendOTP.setOnClickListener(v -> {
            if (tvResendOTP.isEnabled()) {
                resendOTP();
            }
        });
    }

    private String maskPhoneNumber(String phone) {
        if (phone != null && phone.length() >= 4) {
            String lastFour = phone.substring(phone.length() - 4);
            return "+91 ****** " + lastFour;
        }
        return "+91 " + phone;
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

        btnVerifyOTP.setEnabled(!otp1.isEmpty() && !otp2.isEmpty() && !otp3.isEmpty() 
            && !otp4.isEmpty());
    }

    private void verifyOTP() {
        String enteredOTP = etOTP1.getText().toString() + etOTP2.getText().toString() +
            etOTP3.getText().toString() + etOTP4.getText().toString();

        // TODO: Integrate Firebase OTP verification here
        // For demo purposes, accept "1234" as correct OTP
        if (enteredOTP.equals("1234")) {
            // OTP is correct
            if ("SIGNUP".equals(fromScreen)) {
                // Coming from signup - complete registration and show success
                // TODO: Save user data to Firebase/Database
                Toast.makeText(this, "Signup successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(OTPVerificationActivity.this, LoginSuccessActivity.class);
                intent.putExtra("phoneNumber", phoneNumber);
                intent.putExtra("FROM", "SIGNUP");
                startActivity(intent);
                finish();
            } else {
                // Coming from login - navigate to success page
                Intent intent = new Intent(OTPVerificationActivity.this, LoginSuccessActivity.class);
                intent.putExtra("phoneNumber", phoneNumber);
                startActivity(intent);
                finish();
            }
        } else {
            // OTP is incorrect - navigate to failure page
            Toast.makeText(this, "Invalid OTP. Please try again.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(OTPVerificationActivity.this, LoginUnsuccessActivity.class);
            startActivity(intent);
        }
    }

    private void startResendTimer() {
        tvResendOTP.setEnabled(false);
        tvResendOTP.setClickable(false);
        tvResendOTP.setTextColor(ContextCompat.getColor(this, R.color.medicam_text_gray));
        tvTimer.setVisibility(View.VISIBLE);

        countDownTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTimer.setText("Resend in " + millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                tvTimer.setVisibility(View.GONE);
                tvResendOTP.setEnabled(true);
                tvResendOTP.setClickable(true);
                tvResendOTP.setTextColor(ContextCompat.getColor(OTPVerificationActivity.this, R.color.medicam_primary));
            }
        }.start();
    }

    private void resendOTP() {
        // Clear OTP fields
        etOTP1.setText("");
        etOTP2.setText("");
        etOTP3.setText("");
        etOTP4.setText("");
        etOTP1.requestFocus();
        
        Toast.makeText(this, "OTP Resent to " + maskPhoneNumber(phoneNumber), Toast.LENGTH_SHORT).show();
        // TODO: Implement Firebase resend OTP
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
