package com.example.medicam;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class PhoneLoginActivity extends AppCompatActivity {

    private EditText etPhoneNumber;
    private MaterialButton btnGetOTP;
    private TextView tvAdminPortal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        // Initialize views
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        btnGetOTP = findViewById(R.id.btnGetOTP);
        tvAdminPortal = findViewById(R.id.tvAdminPortal);

        // Format phone number as user types
        etPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // Enable button only if phone number is 10 digits
                String phone = s.toString().trim();
                btnGetOTP.setEnabled(phone.length() == 10);
            }
        });

        // Get OTP button click
        btnGetOTP.setOnClickListener(v -> {
            String phoneNumber = etPhoneNumber.getText().toString().trim();
            if (phoneNumber.length() == 10) {
                // TODO: Integrate Firebase Phone Authentication here
                // For now, just navigate to OTP screen
                Intent intent = new Intent(PhoneLoginActivity.this, OTPVerificationActivity.class);
                intent.putExtra("phoneNumber", phoneNumber);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please enter valid 10 digit phone number", Toast.LENGTH_SHORT).show();
            }
        });

        // Admin Portal click
        tvAdminPortal.setOnClickListener(v -> {
            Intent intent = new Intent(PhoneLoginActivity.this, AdminLoginActivity.class);
            startActivity(intent);
        });
    }
}
