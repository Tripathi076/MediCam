package com.example.medicam;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medicam.utils.FirebaseErrorHandler;
import com.example.medicam.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {
    private static final String TAG = "PhoneLoginActivity";

    private EditText etPhoneNumber;
    private MaterialButton btnGetOTP;
    private TextView tvAdminPortal;
    private ProgressBar progressBar;
    
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        // Initialize Firebase and Session Manager
        mAuth = FirebaseAuth.getInstance();
        
        // Initialize views
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        btnGetOTP = findViewById(R.id.btnGetOTP);
        tvAdminPortal = findViewById(R.id.tvAdminPortal);
        progressBar = findViewById(R.id.progressBar);
        
        setupPhoneAuthCallbacks();
        setupListeners();
    }
    
    private void setupPhoneAuthCallbacks() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                try {
                    Log.d(TAG, "Verification completed successfully");
                    signInWithCredential(credential);
                } catch (Exception e) {
                    FirebaseErrorHandler.logException("onVerificationCompleted", e);
                }
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                try {
                    progressBar.setVisibility(View.GONE);
                    btnGetOTP.setEnabled(true);
                    String errorMsg = FirebaseErrorHandler.getAuthErrorMessage(e);
                    Toast.makeText(PhoneLoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Verification failed: " + e.getLocalizedMessage(), e);
                } catch (Exception ex) {
                    FirebaseErrorHandler.logException("onVerificationFailed", ex);
                }
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                try {
                    Log.d(TAG, "Code sent successfully");
                    mVerificationId = verificationId;
                    mResendToken = token;
                    progressBar.setVisibility(View.GONE);
                    
                    // Navigate to OTP verification screen
                    Intent intent = new Intent(PhoneLoginActivity.this, OTPVerificationActivity.class);
                    intent.putExtra("phoneNumber", etPhoneNumber.getText().toString().trim());
                    intent.putExtra("verificationId", verificationId);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    FirebaseErrorHandler.logException("onCodeSent", e);
                }
            }
        };
    }
    
    private void setupListeners() {
        // Format phone number as user types
        etPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Empty implementation - no action needed before text change
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Empty implementation - text formatting handled in afterTextChanged
            }

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
                sendOTP(phoneNumber);
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
    
    private void sendOTP(String phoneNumber) {
        try {
            progressBar.setVisibility(View.VISIBLE);
            btnGetOTP.setEnabled(false);
            
            String fullPhoneNumber = "+91" + phoneNumber;
            
            PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                    .setPhoneNumber(fullPhoneNumber)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(PhoneLoginActivity.this)
                    .setCallbacks(mCallbacks)
                    .build();
            
            PhoneAuthProvider.verifyPhoneNumber(options);
            Log.d(TAG, "OTP send initiated for: " + fullPhoneNumber);
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            btnGetOTP.setEnabled(true);
            String errorMsg = FirebaseErrorHandler.getAuthErrorMessage(e);
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            FirebaseErrorHandler.logException("sendOTP", e);
        }
    }
    
    private void signInWithCredential(PhoneAuthCredential credential) {
        try {
            progressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        try {
                            if (task.isSuccessful()) {
                                String userId = mAuth.getCurrentUser().getUid();
                                String phoneNumber = etPhoneNumber.getText().toString().trim();
                                SessionManager sessionManager = SessionManager.getInstance(PhoneLoginActivity.this);
                                sessionManager.saveUserSession(userId, "", phoneNumber, "");
                                
                                Toast.makeText(PhoneLoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(PhoneLoginActivity.this, DashboardActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                String errorMsg = FirebaseErrorHandler.getAuthErrorMessage(task.getException());
                                Toast.makeText(PhoneLoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            FirebaseErrorHandler.logException("signInWithCredential", e);
                        }
                    });
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            String errorMsg = FirebaseErrorHandler.getAuthErrorMessage(e);
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            FirebaseErrorHandler.logException("signInWithCredential", e);
        }
    }
}
