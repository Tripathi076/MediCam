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

public class RadiologyPreviewActivity extends AppCompatActivity {

    private String centerName, scanType, scanDate, doctorName, patientName;
    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radiology_preview);

        ImageView btnBack = findViewById(R.id.btnBack);
        ImageView ivReportPreview = findViewById(R.id.ivReportPreview);
        MaterialButton btnSave = findViewById(R.id.btnSave);

        // Get data from intent
        String fileUriString = getIntent().getStringExtra("FILE_URI");
        centerName = getIntent().getStringExtra("CENTER_NAME");
        scanType = getIntent().getStringExtra("SCAN_TYPE");
        scanDate = getIntent().getStringExtra("SCAN_DATE");
        doctorName = getIntent().getStringExtra("DOCTOR_NAME");
        patientName = getIntent().getStringExtra("PATIENT_NAME");

        if (fileUriString != null) {
            fileUri = Uri.parse(fileUriString);
            ivReportPreview.setImageURI(fileUri);
        }

        btnBack.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> saveReport());
    }

    private void saveReport() {
        // Determine category based on scan type
        String category = determineCategory(scanType);

        // Create new report object
        RadiologyReport newReport = new RadiologyReport(
            centerName,
            scanType,
            scanDate,
            doctorName,
            patientName,
            fileUri != null ? fileUri.toString() : "",
            category
        );

        // Load existing reports from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("RadiologyReports", MODE_PRIVATE);
        String json = prefs.getString("reports_list", null);

        List<RadiologyReport> reportsList;
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

        // Add new report to the beginning of the list
        reportsList.add(0, newReport);

        // Save updated list to SharedPreferences
        Gson gson = new Gson();
        String updatedJson = gson.toJson(reportsList);
        prefs.edit().putString("reports_list", updatedJson).apply();

        Toast.makeText(this, "Report Saved Successfully!", Toast.LENGTH_SHORT).show();

        // Navigate to report detail view
        Intent intent = new Intent(RadiologyPreviewActivity.this, RadiologyDetailActivity.class);
        intent.putExtra("CENTER_NAME", centerName);
        intent.putExtra("SCAN_TYPE", scanType);
        intent.putExtra("SCAN_DATE", scanDate);
        intent.putExtra("DOCTOR_NAME", doctorName);
        intent.putExtra("PATIENT_NAME", patientName);
        intent.putExtra("FILE_URI", fileUri != null ? fileUri.toString() : "");
        startActivity(intent);
        finish();
    }

    private String determineCategory(String scanType) {
        if (scanType == null) return "Others";

        String typeLower = scanType.toLowerCase();
        if (typeLower.contains("ct")) {
            return "CT Scan";
        } else if (typeLower.contains("mri")) {
            return "MRI Scan";
        } else if (typeLower.contains("x-ray") || typeLower.contains("xray")) {
            return "X-Ray";
        } else if (typeLower.contains("ultrasound") || typeLower.contains("ultra")) {
            return "Ultrasound";
        } else {
            return "Others";
        }
    }
}
