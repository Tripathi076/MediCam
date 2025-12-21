package com.example.medicam;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class ABHACardDisplayActivity extends AppCompatActivity {

    private ImageView btnBack;
    private MaterialButton tabCard, tabConsent, tabProvider;
    private MaterialButton btnViewProfile, btnLogoutAbha, btnDownload;
    private View cardContent;
    private LinearLayout tabsLayout;
    private View tabIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abha_card_display);

        // Edge-to-edge handling
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tabsLayout), (v, insets) -> {
            insets.consumeDisplayCutout();
            return insets;
        });

        initializeViews();
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
        btnDownload.setOnClickListener(v -> {
            Toast.makeText(this, "Downloading ABHA Card...", Toast.LENGTH_SHORT).show();
            // TODO: Implement actual download functionality
        });

        btnViewProfile.setOnClickListener(v -> {
            Toast.makeText(this, "Opening Profile...", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to profile view
        });

        btnLogoutAbha.setOnClickListener(v -> {
            // TODO: Implement logout functionality
            Toast.makeText(this, "Logging out ABHA Profile...", Toast.LENGTH_SHORT).show();
        });
    }

    private void setTabActive(MaterialButton activeTab) {
        // Reset all tabs
        tabCard.setTextColor(getColor(R.color.text_secondary));
        tabConsent.setTextColor(getColor(R.color.text_secondary));
        tabProvider.setTextColor(getColor(R.color.text_secondary));

        // Set active tab
        activeTab.setTextColor(getColor(R.color.medicam_primary));

        // Move indicator
        activeTab.post(() -> {
            int[] location = new int[2];
            activeTab.getLocationInWindow(location);
            float indicatorStart = location[0];
            float indicatorWidth = activeTab.getWidth();
            tabIndicator.setX(indicatorStart);
            tabIndicator.animate().scaleX(indicatorWidth / tabIndicator.getWidth()).setDuration(300).start();
        });
    }

    private void showCardContent() {
        // Show card content - it's already visible
        if (cardContent != null) {
            cardContent.setVisibility(View.VISIBLE);
        }
    }

    private void showConsentContent() {
        // TODO: Load consent fragment or content
        Toast.makeText(this, "Consent tab clicked", Toast.LENGTH_SHORT).show();
    }

    private void showProviderContent() {
        // TODO: Load provider fragment or content
        Toast.makeText(this, "Provider tab clicked", Toast.LENGTH_SHORT).show();
    }
}
