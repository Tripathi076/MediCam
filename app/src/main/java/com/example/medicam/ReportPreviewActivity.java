package com.example.medicam;

import android.content.Intent;
import android.content.Context;
import android.util.Log;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.example.medicam.utils.SessionManager;
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

        // Get userId from SessionManager
        String userId = SessionManager.getInstance(this).getUserId();
        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress
        Toast.makeText(this, "Uploading report...", Toast.LENGTH_SHORT).show();

        // Upload image to Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        String fileName = "reports/" + userId + "/" + System.currentTimeMillis() + ".jpg";
        StorageReference reportImageRef = storageRef.child(fileName);

        reportImageRef.putFile(fileUri)
            .addOnSuccessListener(taskSnapshot -> reportImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // On image upload success, save metadata to Firebase Database
                PathologyReport newReport = new PathologyReport(
                        labName,
                        testName,
                        sampleDate,
                        doctorName,
                        patientName,
                        uri.toString(),
                        category
                );

                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
                String reportId = dbRef.child("reports").child(userId).push().getKey();
                dbRef.child("reports").child(userId).child(reportId).setValue(newReport)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Report Saved Successfully!", Toast.LENGTH_SHORT).show();
                            // Navigate to report detail view
                            Intent intent = new Intent(ReportPreviewActivity.this, ReportDetailActivity.class);
                            intent.putExtra("LAB_NAME", labName);
                            intent.putExtra("TEST_NAME", testName);
                            intent.putExtra("COLLECTION_DATE", sampleDate);
                            intent.putExtra("DOCTOR_NAME", doctorName);
                            intent.putExtra("PATIENT_NAME", patientName);
                            intent.putExtra("REPORT_IMAGE_URI", uri.toString());
                            startActivity(intent);
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to save report metadata", Toast.LENGTH_SHORT).show();
                            Log.e("ReportPreview", "DB error: ", e);
                        });
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to get image URL", Toast.LENGTH_SHORT).show();
                Log.e("ReportPreview", "URL error: ", e);
            }))
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                Log.e("ReportPreview", "Upload error: ", e);
            });
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

