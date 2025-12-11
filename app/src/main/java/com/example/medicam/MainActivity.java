package com.example.medicam;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize Firebase
        try {
            FirebaseApp.initializeApp(this);
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            Log.d(TAG, "Firebase initialized successfully!");
            Toast.makeText(this, "Firebase Connected! ✓", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(TAG, "Firebase initialization failed: " + e.getMessage());
            Toast.makeText(this, "Firebase Connection Failed - Check google-services.json", Toast.LENGTH_LONG).show();
        }

        // Handle Window Insets for EdgeToEdge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Views
        MaterialButton btnLogin = findViewById(R.id.btnLogin);
        MaterialButton btnSignUp = findViewById(R.id.btnSignUp);
        TextView footerText = findViewById(R.id.footer);

        // Set Listeners
        btnLogin.setOnClickListener(v -> {
            // Navigate to Login Screen (For now pointing to Admin Login or just a placeholder)
            Intent intent = new Intent(MainActivity.this, PhoneLoginActivity.class);
            startActivity(intent);
        });

        btnSignUp.setOnClickListener(v -> {
            // Navigate to Sign Up Screen
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
        
        // Make "Admin Portal" clickable, green, and underlined
        String fullText = "Are You An Organization ? Admin Portal";
        SpannableString spannableString = new SpannableString(fullText);
        
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(MainActivity.this, AdminLoginActivity.class);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true); // Underline
                ds.setColor(ContextCompat.getColor(MainActivity.this, R.color.medicam_primary)); // Green color
            }
        };

        int startIndex = fullText.indexOf("Admin Portal");
        int endIndex = startIndex + "Admin Portal".length();

        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        footerText.setText(spannableString);
        footerText.setMovementMethod(LinkMovementMethod.getInstance());
        footerText.setHighlightColor(android.graphics.Color.TRANSPARENT); // Remove shadow on click
    }
}
