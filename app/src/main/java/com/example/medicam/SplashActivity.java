package com.example.medicam;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medicam.utils.NetworkUtils;
import com.example.medicam.utils.SessionManager;

public class SplashActivity extends AppCompatActivity {
    
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        sessionManager = SessionManager.getInstance(this);

        // Delay for 2 seconds then check session and navigate
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Check network connectivity
            if (!NetworkUtils.isNetworkAvailable(this)) {
                Toast.makeText(this, "No internet connection. Some features may not work.", Toast.LENGTH_LONG).show();
            }
            
            navigateToNextScreen();
        }, 2000);
    }
    
    private void navigateToNextScreen() {
        Intent intent;
        
        if (sessionManager.isUserLoggedIn()) {
            // User is logged in - check session validity
            if (sessionManager.isSessionValid()) {
                // Session is valid - check if auto-lock applies
                if (sessionManager.shouldAutoLock()) {
                    // App was inactive - require PIN re-entry
                    String phone = sessionManager.getUserPhone();
                    if (phone != null && !phone.isEmpty()) {
                        intent = new Intent(this, PinLoginActivity.class);
                        intent.putExtra("phoneNumber", phone);
                        intent.putExtra("userId", sessionManager.getUserId());
                        intent.putExtra("fromAutoLock", true);
                    } else {
                        // No phone stored, go to login
                        intent = new Intent(this, PhoneLoginActivity.class);
                    }
                } else {
                    // Session valid and not locked - go to dashboard
                    sessionManager.updateLastActiveTime();
                    intent = new Intent(this, DashboardActivity.class);
                }
            } else {
                // Session expired - clear and go to login
                sessionManager.clearSession();
                Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
                intent = new Intent(this, PhoneLoginActivity.class);
            }
        } else {
            // Not logged in - go to main/login
            intent = new Intent(this, MainActivity.class);
        }
        
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
