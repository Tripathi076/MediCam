package com.example.medicam;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class ABHACardDisplayActivity extends AppCompatActivity {

    private static final String PREF_NAME = "medicam_pref";

    private ImageView btnBack;
    private MaterialButton tabCard, tabConsent, tabProvider;
    private MaterialButton btnViewProfile, btnLogoutAbha, btnDownload;
    private View cardContent;
    private LinearLayout tabsLayout;
    private View tabIndicator;
    
    // Text views for displaying data
    private TextView tvName, tvAbhaNumber, tvGender, tvDob, tvMobile, tvEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abha_card_display);

        // Edge-to-edge handling
        if (findViewById(R.id.tabsLayout) != null) {
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tabsLayout), (v, insets) -> {
                insets.consumeDisplayCutout();
                return insets;
            });
        }

        initializeViews();
        loadUserData();
        setupTabClickListeners();
        setupButtonClickListeners();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        tabCard = findViewById(R.id.tabCard);
        tabConsent = findViewById(R.id.tabConsent);
        tabProvider = findViewById(R.id.tabProvider);
        btnViewProfile = findViewById(R.id.btnViewProfile);
        btnLogoutAbha = findViewById(R.id.btnLogoutAbha);
        btnDownload = findViewById(R.id.btnDownload);
        cardContent = findViewById(R.id.cardContent);
        tabsLayout = findViewById(R.id.tabsLayout);
        tabIndicator = findViewById(R.id.tabIndicator);
        
        // Initialize text views for user data
        tvName = findViewById(R.id.tvName);
        tvAbhaNumber = findViewById(R.id.tvAbhaNumber);
        tvGender = findViewById(R.id.tvGender);
        tvDob = findViewById(R.id.tvDob);
        tvMobile = findViewById(R.id.tvMobile);
        tvEmail = findViewById(R.id.tvEmail);
    }
    
    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        
        // Load and display user data
        String name = prefs.getString("abha_name", "User");
        String abhaNumber = prefs.getString("abha_number", "XX-XXXX-XXXX-XXXX");
        String abhaAddress = prefs.getString("abha_address", "user@abdm");
        String mobile = prefs.getString("abha_mobile", "Not linked");
        String gender = prefs.getString("abha_gender", "Not specified");
        String dob = prefs.getString("abha_dob", "Not specified");
        String email = prefs.getString("abha_email", abhaAddress);
        
        // Set text views
        if (tvName != null) tvName.setText(name);
        if (tvAbhaNumber != null) tvAbhaNumber.setText(abhaNumber);
        if (tvGender != null) tvGender.setText(gender);
        if (tvDob != null) tvDob.setText(dob);
        if (tvMobile != null) tvMobile.setText(formatMobile(mobile));
        if (tvEmail != null) tvEmail.setText(email);
    }
    
    private String formatMobile(String mobile) {
        if (mobile != null && mobile.length() == 10) {
            return mobile.substring(0, 2) + "-" + mobile.substring(2, 6) + "-" + mobile.substring(6, 10);
        }
        return mobile;
    }

    private void setupTabClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        tabCard.setOnClickListener(v -> {
            setTabActive(tabCard);
            showCardContent();
        });

        tabConsent.setOnClickListener(v -> {
            setTabActive(tabConsent);
            showConsentContent();
        });

        tabProvider.setOnClickListener(v -> {
            setTabActive(tabProvider);
            showProviderContent();
        });
    }

    private void setupButtonClickListeners() {
        if (btnDownload != null) {
            btnDownload.setOnClickListener(v -> {
                Toast.makeText(this, "Downloading ABHA Card...", Toast.LENGTH_SHORT).show();
            });
        }

        if (btnViewProfile != null) {
            btnViewProfile.setOnClickListener(v -> {
                Toast.makeText(this, "Opening Profile...", Toast.LENGTH_SHORT).show();
            });
        }

        if (btnLogoutAbha != null) {
            btnLogoutAbha.setOnClickListener(v -> {
                // Clear ABHA registration data
                SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("abha_registered", false);
                editor.remove("abha_name");
                editor.remove("abha_number");
                editor.remove("abha_address");
                editor.remove("abha_mobile");
                editor.remove("abha_aadhaar");
                editor.apply();
                
                Toast.makeText(this, "ABHA Profile logged out", Toast.LENGTH_SHORT).show();
                finish();
            });
        }
    }

    private void setTabActive(MaterialButton activeTab) {
        // Reset all tabs
        tabCard.setTextColor(getColor(R.color.text_secondary));
        tabConsent.setTextColor(getColor(R.color.text_secondary));
        tabProvider.setTextColor(getColor(R.color.text_secondary));

        // Set active tab
        activeTab.setTextColor(getColor(R.color.medicam_primary));

        // Move indicator
        if (tabIndicator != null) {
            activeTab.post(() -> {
                int[] location = new int[2];
                activeTab.getLocationInWindow(location);
                float indicatorStart = location[0];
                float indicatorWidth = activeTab.getWidth();
                tabIndicator.setX(indicatorStart);
                tabIndicator.animate().scaleX(indicatorWidth / tabIndicator.getWidth()).setDuration(300).start();
            });
        }
    }

    private void showCardContent() {
        if (cardContent != null) {
            cardContent.setVisibility(View.VISIBLE);
        }
    }

    private void showConsentContent() {
        Toast.makeText(this, "Consent tab clicked", Toast.LENGTH_SHORT).show();
    }

    private void showProviderContent() {
        Toast.makeText(this, "Provider tab clicked", Toast.LENGTH_SHORT).show();
    }
}
