package com.example.medicam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ReportPreviewActivity extends AppCompatActivity {

    private ImageView ivReportPreview, btnBack;
    private MaterialButton btnSave;
    private String labName, testName, sampleDate, doctorName, patientName;
    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_preview);

        // Get data from intent
        String fileUriString = getIntent().getStringExtra("fileUri");
        fileUri = Uri.parse(fileUriString);
        labName = getIntent().getStringExtra("labName");
        testName = getIntent().getStringExtra("testName");
        sampleDate = getIntent().getStringExtra("sampleDate");
        doctorName = getIntent().getStringExtra("doctorName");
        patientName = getIntent().getStringExtra("patientName");

        // Initialize views
        ivReportPreview = findViewById(R.id.ivReportPreview);
        btnBack = findViewById(R.id.btnBack);
        btnSave = findViewById(R.id.btnSave);

        // Load image preview
        ivReportPreview.setImageURI(fileUri);

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Save button
        btnSave.setOnClickListener(v -> saveReport());
    }

    private void saveReport() {
        // Determine category based on test name
        String category = determineCategory(testName);
        
        // Create new report object
        PathologyReport newReport = new PathologyReport(
            labName,
            testName,
            sampleDate,
            doctorName,
            patientName,
            fileUri.toString(),
            category
        );
        
        // Load existing reports from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("PathologyReports", MODE_PRIVATE);
        String json = prefs.getString("reports_list", null);
        
        List<PathologyReport> reportsList;
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
        
        // Add new report to the beginning of the list
        reportsList.add(0, newReport);
        
        // Save updated list to SharedPreferences
        Gson gson = new Gson();
        String updatedJson = gson.toJson(reportsList);
        prefs.edit().putString("reports_list", updatedJson).apply();
        
        Toast.makeText(this, "Report Saved Successfully!", Toast.LENGTH_SHORT).show();
        
        // Navigate to report detail view
        Intent intent = new Intent(ReportPreviewActivity.this, ReportDetailActivity.class);
        intent.putExtra("LAB_NAME", labName);
        intent.putExtra("TEST_NAME", testName);
        intent.putExtra("COLLECTION_DATE", sampleDate);
        intent.putExtra("DOCTOR_NAME", doctorName);
        intent.putExtra("PATIENT_NAME", patientName);
        intent.putExtra("REPORT_IMAGE_URI", fileUri.toString());
        startActivity(intent);
        finish();
    }
    
    private String determineCategory(String testName) {
        if (testName == null) return "Others";
        
        String testLower = testName.toLowerCase();
        if (testLower.contains("blood") || testLower.contains("cbc") || testLower.contains("hemoglobin")) {
            return "Blood Tests";
        } else if (testLower.contains("genetic") || testLower.contains("dna") || testLower.contains("gene")) {
            return "Genetic Tests";
        } else if (testLower.contains("biopsy") || testLower.contains("tissue")) {
            return "Biopsy";
        } else if (testLower.contains("ultrasound") || testLower.contains("ultra sound")) {
            return "UltraSound";
        }
        return "Others";
    }
}

