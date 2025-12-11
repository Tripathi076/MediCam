package com.example.medicam;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ImageView btnBack = findViewById(R.id.btnBack);
        TextInputEditText nameEditText = findViewById(R.id.nameEditText);
        TextInputEditText phoneEditText = findViewById(R.id.SignupPhoneNumber);
        TextInputEditText passwordEditText = findViewById(R.id.passwordEditText);
        AutoCompleteTextView stateAutoComplete = findViewById(R.id.stateAutoComplete);
        CheckBox cbTerms = findViewById(R.id.cbTerms);
        MaterialButton btnSignUpAction = findViewById(R.id.btnSignUpAction);
        TextView tvSignIn = findViewById(R.id.tvSignIn);

        // Setup State Dropdown
        String[] states = {"Maharashtra", "Delhi", "Karnataka", "Tamil Nadu", "Gujarat", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, states);
        stateAutoComplete.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        btnSignUpAction.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String state = stateAutoComplete.getText().toString().trim();
            boolean termsAccepted = cbTerms.isChecked();

            if (name.isEmpty() || phone.isEmpty() || password.isEmpty() || state.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else if (phone.length() != 10) {
                Toast.makeText(this, "Please enter a valid 10-digit phone number", Toast.LENGTH_SHORT).show();
            } else if (!termsAccepted) {
                Toast.makeText(this, "Please agree to the Terms & Condition", Toast.LENGTH_SHORT).show();
            } else {
                // Send OTP to verify phone number
                Intent intent = new Intent(SignUpActivity.this, OTPVerificationActivity.class);
                intent.putExtra("PHONE_NUMBER", phone);
                intent.putExtra("NAME", name);
                intent.putExtra("PASSWORD", password);
                intent.putExtra("STATE", state);
                intent.putExtra("FROM", "SIGNUP");
                startActivity(intent);
            }
        });

        tvSignIn.setOnClickListener(v -> {
            // Navigate to Phone Login
            Intent intent = new Intent(SignUpActivity.this, PhoneLoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
