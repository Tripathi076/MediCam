package com.example.medicam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PathologyActivity extends AppCompatActivity {
    
    private RecyclerView reportsRecyclerView;
    private LinearLayout emptyStateLayout;
    private PathologyReportAdapter adapter;
    private List<PathologyReport> reportsList;
    private String currentCategory = "All";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pathology);
        
        // Handle Window Insets for EdgeToEdge
        if (findViewById(R.id.main) != null) {
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
        
        // Initialize views
        reportsRecyclerView = findViewById(R.id.reportsRecyclerView);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        
        // Set up RecyclerView
        reportsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reportsList = new ArrayList<>();
        adapter = new PathologyReportAdapter(this, reportsList);
        reportsRecyclerView.setAdapter(adapter);
        
        // Load saved reports
        loadReports();
        
        // Set up category tabs
        setupCategoryTabs();
        
        // Set up FAB click listener
        FloatingActionButton fabAddReport = findViewById(R.id.fabAddReport);
        if (fabAddReport != null) {
            fabAddReport.setOnClickListener(v -> {
                startActivity(new Intent(PathologyActivity.this, UploadPathologyReportActivity.class));
            });
        }
        
        // Set up back button
        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                startActivity(new Intent(PathologyActivity.this, DashboardActivity.class));
                finish();
            });
        }
        
        // Set up navigation click listeners
        setupBottomNavigation();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Reload reports when returning to this activity
        loadReports();
    }
    
    private void loadReports() {
        SharedPreferences prefs = getSharedPreferences("PathologyReports", MODE_PRIVATE);
        String json = prefs.getString("reports_list", null);
        
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<PathologyReport>>(){}.getType();
            reportsList = gson.fromJson(json, type);
            
            if (reportsList == null) {
                reportsList = new ArrayList<>();
            }
        } else {
            reportsList = new ArrayList<>();
        }
        
        // Update UI
        if (reportsList.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            reportsRecyclerView.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            reportsRecyclerView.setVisibility(View.VISIBLE);
            adapter = new PathologyReportAdapter(this, reportsList);
            reportsRecyclerView.setAdapter(adapter);
            adapter.filter(currentCategory);
        }
    }
    
    private void setupCategoryTabs() {
        MaterialButton btnTabAll = findViewById(R.id.btnTabAll);
        MaterialButton btnTabBlood = findViewById(R.id.btnTabBlood);
        MaterialButton btnTabGenetic = findViewById(R.id.btnTabGenetic);
        MaterialButton btnTabBiopsy = findViewById(R.id.btnTabBiopsy);
        MaterialButton btnTabUltraSound = findViewById(R.id.btnTabUltraSound);
        
        View.OnClickListener tabClickListener = v -> {
            // Reset all tabs to inactive state
            resetTabColors(btnTabAll, btnTabBlood, btnTabGenetic, btnTabBiopsy, btnTabUltraSound);
            
            // Set clicked tab to active state
            MaterialButton clickedButton = (MaterialButton) v;
            clickedButton.setBackgroundColor(getColor(R.color.medicam_primary));
            clickedButton.setTextColor(getColor(R.color.white));
            
            // Filter reports based on selected category
            if (v.getId() == R.id.btnTabAll) {
                currentCategory = "All";
            } else if (v.getId() == R.id.btnTabBlood) {
                currentCategory = "Blood Tests";
            } else if (v.getId() == R.id.btnTabGenetic) {
                currentCategory = "Genetic Tests";
            } else if (v.getId() == R.id.btnTabBiopsy) {
                currentCategory = "Biopsy";
            } else if (v.getId() == R.id.btnTabUltraSound) {
                currentCategory = "UltraSound";
            }
            
            if (adapter != null) {
                adapter.filter(currentCategory);
            }
        };
        
        if (btnTabAll != null) btnTabAll.setOnClickListener(tabClickListener);
        if (btnTabBlood != null) btnTabBlood.setOnClickListener(tabClickListener);
        if (btnTabGenetic != null) btnTabGenetic.setOnClickListener(tabClickListener);
        if (btnTabBiopsy != null) btnTabBiopsy.setOnClickListener(tabClickListener);
        if (btnTabUltraSound != null) btnTabUltraSound.setOnClickListener(tabClickListener);
    }
    
    private void resetTabColors(MaterialButton... buttons) {
        for (MaterialButton button : buttons) {
            if (button != null) {
                button.setBackgroundColor(getColor(R.color.white));
                button.setTextColor(getColor(R.color.text_primary));
            }
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
        
        // Home
        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                startActivity(new Intent(PathologyActivity.this, DashboardActivity.class));
                finish();
            });
        }
        
        // Pathology - Already on this page
        if (navPathology != null) {
            navPathology.setOnClickListener(v -> {
                Toast.makeText(this, "Pathology Reports", Toast.LENGTH_SHORT).show();
            });
        }
        
        // ABHA
        if (navABHA != null) {
            navABHA.setOnClickListener(v -> {
                startActivity(new Intent(PathologyActivity.this, ABHAActivity.class));
                finish();
            });
        }
        
        // BMI
        if (navBMI != null) {
            navBMI.setOnClickListener(v -> {
                startActivity(new Intent(PathologyActivity.this, BMIGenderActivity.class));
                finish();
            });
        }
        
        // Devices
        if (navDevices != null) {
            navDevices.setOnClickListener(v -> {
                startActivity(new Intent(PathologyActivity.this, DevicesActivity.class));
                finish();
            });
        }
    }
}
