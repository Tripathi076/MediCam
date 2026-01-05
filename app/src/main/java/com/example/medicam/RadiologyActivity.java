package com.example.medicam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RadiologyActivity extends AppCompatActivity {

    private RecyclerView reportsRecyclerView;
    private LinearLayout emptyStateLayout;
    private RadiologyReportAdapter adapter;
    private List<RadiologyReport> reportsList;
    private String currentCategory = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radiology);

        // Initialize views
        reportsRecyclerView = findViewById(R.id.reportsRecyclerView);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        ImageView btnBack = findViewById(R.id.btnBack);
        FloatingActionButton fabAddReport = findViewById(R.id.fabAddReport);

        // Set up RecyclerView
        if (reportsRecyclerView != null) {
            reportsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            reportsList = new ArrayList<>();
            adapter = new RadiologyReportAdapter(this, reportsList);
            reportsRecyclerView.setAdapter(adapter);
        }

        // Load saved reports
        loadReports();

        // Set up category tabs
        setupCategoryTabs();

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(RadiologyActivity.this, DashboardActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        });

        fabAddReport.setOnClickListener(v -> {
            Intent intent = new Intent(RadiologyActivity.this, UploadRadiologyReportActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        setupBottomNavigation();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReports();
    }

    private void loadReports() {
        SharedPreferences prefs = getSharedPreferences("RadiologyReports", MODE_PRIVATE);
        String json = prefs.getString("reports_list", null);

        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<RadiologyReport>>(){}.getType();
            reportsList = gson.fromJson(json, type);
            if (reportsList == null) {
                reportsList = new ArrayList<>();
            }
        } else {
            reportsList = new ArrayList<>();
        }

        updateUI();
    }

    private void updateUI() {
        if (reportsList.isEmpty()) {
            if (emptyStateLayout != null) emptyStateLayout.setVisibility(View.VISIBLE);
            if (reportsRecyclerView != null) reportsRecyclerView.setVisibility(View.GONE);
        } else {
            if (emptyStateLayout != null) emptyStateLayout.setVisibility(View.GONE);
            if (reportsRecyclerView != null) {
                reportsRecyclerView.setVisibility(View.VISIBLE);
                if (adapter != null) {
                    adapter.updateReports(reportsList);
                    adapter.filter(currentCategory);
                }
            }
        }
    }

    private void setupCategoryTabs() {
        MaterialButton btnTabAll = findViewById(R.id.btnTabAll);
        MaterialButton btnTabCT = findViewById(R.id.btnTabCT);
        MaterialButton btnTabMRI = findViewById(R.id.btnTabMRI);
        MaterialButton btnTabXRay = findViewById(R.id.btnTabXRay);
        MaterialButton btnTabUltrasound = findViewById(R.id.btnTabUltrasound);

        View.OnClickListener tabClickListener = v -> {
            resetTabColors(btnTabAll, btnTabCT, btnTabMRI, btnTabXRay, btnTabUltrasound);
            MaterialButton clickedButton = (MaterialButton) v;
            if (clickedButton != null) {
                clickedButton.setBackgroundColor(getColor(R.color.medicam_primary));
                clickedButton.setTextColor(getColor(R.color.white));
            }

            if (v.getId() == R.id.btnTabAll) currentCategory = "All";
            else if (v.getId() == R.id.btnTabCT) currentCategory = "CT Scan";
            else if (v.getId() == R.id.btnTabMRI) currentCategory = "MRI Scan";
            else if (v.getId() == R.id.btnTabXRay) currentCategory = "X-Ray";
            else if (v.getId() == R.id.btnTabUltrasound) currentCategory = "Ultrasound";

            if (adapter != null) adapter.filter(currentCategory);
        };

        if (btnTabAll != null) btnTabAll.setOnClickListener(tabClickListener);
        if (btnTabCT != null) btnTabCT.setOnClickListener(tabClickListener);
        if (btnTabMRI != null) btnTabMRI.setOnClickListener(tabClickListener);
        if (btnTabXRay != null) btnTabXRay.setOnClickListener(tabClickListener);
        if (btnTabUltrasound != null) btnTabUltrasound.setOnClickListener(tabClickListener);
    }

    private void resetTabColors(MaterialButton... buttons) {
        for (MaterialButton button : buttons) {
            if (button != null) {
                button.setBackgroundColor(getColor(R.color.white));
                button.setTextColor(getColor(R.color.text_primary));
            }
        }
    }

    private void setupBottomNavigation() {
        View navHome = findViewById(R.id.navHome);
        View navPathology = findViewById(R.id.navPathology);
        View navABHA = findViewById(R.id.navABHA);
        View navBMI = findViewById(R.id.navBMI);
        View navDevices = findViewById(R.id.navDevices);

        if (navHome != null) navHome.setOnClickListener(v -> {
            startActivity(new Intent(RadiologyActivity.this, DashboardActivity.class));
            finish();
        });

        if (navPathology != null) navPathology.setOnClickListener(v -> {
            startActivity(new Intent(RadiologyActivity.this, ReportsActivity.class));
            finish();
        });

        if (navABHA != null) navABHA.setOnClickListener(v -> {
            startActivity(new Intent(RadiologyActivity.this, ABHAActivity.class));
            finish();
        });

        if (navBMI != null) navBMI.setOnClickListener(v -> {
            startActivity(new Intent(RadiologyActivity.this, BMIGenderActivity.class));
            finish();
        });

        if (navDevices != null) navDevices.setOnClickListener(v -> {
            startActivity(new Intent(RadiologyActivity.this, DevicesActivity.class));
            finish();
        });
    }
}
