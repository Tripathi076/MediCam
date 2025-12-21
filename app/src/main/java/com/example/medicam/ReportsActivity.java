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
        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(ReportsActivity.this, DashboardActivity.class));
            finish();
        });

        // Setup filter buttons
        setupFilterButtons();

        // Setup bottom navigation
        setupBottomNavigation();
    }

    private void loadReports() {
        SharedPreferences sharedPreferences = getSharedPreferences("medicam_pref", MODE_PRIVATE);
        String reportsJson = sharedPreferences.getString("pathology_reports", "[]");

        Gson gson = new Gson();
        Type type = new TypeToken<List<PathologyReport>>(){}.getType();
        allReports = gson.fromJson(reportsJson, type);

        // Load both pathology and radiology reports
        if (allReports == null) {
            allReports = new ArrayList<>();
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

    private void setupBottomNavigation() {
        View navHome = findViewById(R.id.navHome);
        View navPathology = findViewById(R.id.navPathology);
        View navABHA = findViewById(R.id.navABHA);
        View navBMI = findViewById(R.id.navBMI);
        View navDevices = findViewById(R.id.navDevices);

        navHome.setOnClickListener(v -> {
            startActivity(new Intent(ReportsActivity.this, DashboardActivity.class));
            finish();
        });

        navPathology.setOnClickListener(v -> {
            // Already on Reports page
            Toast.makeText(this, "Reports", Toast.LENGTH_SHORT).show();
        });

        navABHA.setOnClickListener(v -> {
            startActivity(new Intent(ReportsActivity.this, ABHAActivity.class));
            finish();
        });

        navBMI.setOnClickListener(v -> {
            startActivity(new Intent(ReportsActivity.this, BMIActivity.class));
            finish();
        });

        navDevices.setOnClickListener(v -> {
            startActivity(new Intent(ReportsActivity.this, DevicesActivity.class));
            finish();
        });
    }
}
