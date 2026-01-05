package com.example.medicam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ReportsActivity extends AppCompatActivity {

    private RecyclerView reportsRecyclerView;
    private LinearLayout emptyStateLayout;
    private PathologyReportAdapter adapter;
    private List<PathologyReport> reportsList;
    private List<PathologyReport> allReports;
    private String currentFilter = "All";

    private MaterialButton btnFilterAll;
    private MaterialButton btnFilterPathology;
    private MaterialButton btnFilterRadiology;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        // Handle Window Insets for EdgeToEdge
        if (findViewById(R.id.bottomNavigation) != null) {
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bottomNavigation), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Initialize views
        reportsRecyclerView = findViewById(R.id.reportsRecyclerView);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);

        btnFilterAll = findViewById(R.id.btnFilterAll);
        btnFilterPathology = findViewById(R.id.btnFilterPathology);
        btnFilterRadiology = findViewById(R.id.btnFilterRadiology);

        // Initialize lists
        reportsList = new ArrayList<>();
        allReports = new ArrayList<>();

        // Setup RecyclerView
        adapter = new PathologyReportAdapter(this, reportsList);
        reportsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reportsRecyclerView.setAdapter(adapter);

        // Load reports from SharedPreferences
        loadReports();

        // Setup back button
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                Intent intent = new Intent(ReportsActivity.this, DashboardActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            });
        }

        // Setup filter buttons
        setupFilterButtons();

        // Setup bottom navigation
        setupBottomNavigation();
    }

    private void loadReports() {
        Gson gson = new Gson();
        
        // Clear all reports
        allReports.clear();
        
        // Load pathology reports from medicam_pref
        SharedPreferences medicamPref = getSharedPreferences("medicam_pref", MODE_PRIVATE);
        String pathologyJson = medicamPref.getString("pathology_reports", "[]");
        Type pathologyType = new TypeToken<List<PathologyReport>>(){}.getType();
        List<PathologyReport> pathologyReports = gson.fromJson(pathologyJson, pathologyType);
        if (pathologyReports != null) {
            // Set category for pathology reports
            for (PathologyReport report : pathologyReports) {
                if (report.getCategory() == null || report.getCategory().isEmpty()) {
                    report.setCategory("Pathology");
                }
            }
            allReports.addAll(pathologyReports);
        }
        
        // Load radiology reports from RadiologyReports preferences
        SharedPreferences radiologyPref = getSharedPreferences("RadiologyReports", MODE_PRIVATE);
        String radiologyJson = radiologyPref.getString("reports_list", "[]");
        Type radiologyType = new TypeToken<List<RadiologyReport>>(){}.getType();
        List<RadiologyReport> radiologyReports = gson.fromJson(radiologyJson, radiologyType);
        if (radiologyReports != null) {
            for (RadiologyReport radReport : radiologyReports) {
                PathologyReport converted = new PathologyReport();
                converted.setLabName(radReport.getCenterName());
                converted.setTestType(radReport.getScanType());
                converted.setReportDate(radReport.getScanDate());
                converted.setDoctorName(radReport.getDoctorName());
                converted.setPatientName(radReport.getPatientName());
                converted.setReportImageUri(radReport.getReportImageUri());
                converted.setCategory("Radiology");
                allReports.add(converted);
            }
        }

        filterReports("All");
        updateUI();
    }

    private void filterReports(String filter) {
        currentFilter = filter;
        reportsList.clear();

        if (filter.equals("All")) {
            reportsList.addAll(allReports);
        } else if (filter.equals("Pathology")) {
            for (PathologyReport report : allReports) {
                if (report.getCategory() != null && report.getCategory().equalsIgnoreCase("Pathology")) {
                    reportsList.add(report);
                }
            }
        } else if (filter.equals("Radiology")) {
            for (PathologyReport report : allReports) {
                if (report.getCategory() != null && report.getCategory().equalsIgnoreCase("Radiology")) {
                    reportsList.add(report);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void setupFilterButtons() {
        btnFilterAll.setOnClickListener(v -> {
            filterReports("All");
            updateFilterButtonStates();
        });

        btnFilterPathology.setOnClickListener(v -> {
            filterReports("Pathology");
            updateFilterButtonStates();
        });

        btnFilterRadiology.setOnClickListener(v -> {
            filterReports("Radiology");
            updateFilterButtonStates();
        });

        updateFilterButtonStates();
    }

    private void updateFilterButtonStates() {
        // Reset all buttons
        btnFilterAll.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        btnFilterPathology.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        btnFilterRadiology.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        // Highlight current filter
        if (currentFilter.equals("All")) {
            btnFilterAll.setBackgroundColor(getResources().getColor(R.color.medicam_primary));
        } else if (currentFilter.equals("Pathology")) {
            btnFilterPathology.setBackgroundColor(getResources().getColor(R.color.medicam_primary));
        } else if (currentFilter.equals("Radiology")) {
            btnFilterRadiology.setBackgroundColor(getResources().getColor(R.color.medicam_primary));
        }
    }

    private void updateUI() {
        if (reportsList.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            reportsRecyclerView.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            reportsRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void setupBottomNavigation() {
        View navHome = findViewById(R.id.navHome);
        View navPathology = findViewById(R.id.navPathology);
        View navABHA = findViewById(R.id.navABHA);
        View navBMI = findViewById(R.id.navBMI);
        View navDevices = findViewById(R.id.navDevices);

        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(ReportsActivity.this, DashboardActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        });

        navPathology.setOnClickListener(v -> {
            // Already on Reports page
            Toast.makeText(this, "Reports", Toast.LENGTH_SHORT).show();
        });

        navABHA.setOnClickListener(v -> {
            Intent intent = new Intent(ReportsActivity.this, ABHAActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        });

        navBMI.setOnClickListener(v -> {
            Intent intent = new Intent(ReportsActivity.this, BMIGenderActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        });

        navDevices.setOnClickListener(v -> {
            startActivity(new Intent(ReportsActivity.this, DevicesActivity.class));
            finish();
        });
    }
}
