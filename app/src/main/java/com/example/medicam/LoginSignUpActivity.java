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

public class LoginSignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sign_up);

        ImageView btnBack = findViewById(R.id.btnBack);
        TextInputEditText nameEditText = findViewById(R.id.nameEditText);
        TextInputEditText emailEditText = findViewById(R.id.emailEditText);
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
            String name = nameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String state = stateAutoComplete.getText().toString();
            boolean termsAccepted = cbTerms.isChecked();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || state.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else if (!termsAccepted) {
                Toast.makeText(this, "Please agree to the Terms & Condition", Toast.LENGTH_SHORT).show();
            } else {
                // Simulate successful sign up
                showSuccessDialog();
            }
        });

        tvSignIn.setOnClickListener(v -> {
            // Navigate to Login (Admin Login or user login if you have one)
             Intent intent = new Intent(LoginSignUpActivity.this, AdminLoginActivity.class); // Assuming AdminLoginActivity for now or finish()
             startActivity(intent);
             finish();
        });
    }

    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_signup_success, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        
        MaterialButton btnSuccessLogin = dialogView.findViewById(R.id.btnSuccessLogin);
        btnSuccessLogin.setOnClickListener(v -> {
            dialog.dismiss();
            // Navigate to Login Screen
            Intent intent = new Intent(LoginSignUpActivity.this, AdminLoginActivity.class);
            startActivity(intent);
            finish();
        });

        dialog.show();
    }
}
