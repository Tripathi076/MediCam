package com.example.medicam;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initViews();
        setupClickListeners();
        loadAppInfo();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvVersion = findViewById(R.id.tvVersion);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadAppInfo() {
        try {
            String versionName = getPackageManager()
                .getPackageInfo(getPackageName(), 0).versionName;
            tvVersion.setText("Version " + versionName);
        } catch (Exception e) {
            tvVersion.setText("Version 1.0.0");
        }
    }
}
