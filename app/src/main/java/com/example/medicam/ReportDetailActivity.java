package com.example.medicam;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class ReportDetailActivity extends AppCompatActivity {

    private ImageView ivReportDetail, btnBack, btnShare, btnDownload;
    private MaterialButton btnSyncABHA;
    private String labName, testName, sampleDate, doctorName, patientName;
    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_detail);

        // Get data from intent - support both old and new keys
        String fileUriString = getIntent().getStringExtra("REPORT_IMAGE_URI");
        if (fileUriString == null) {
            fileUriString = getIntent().getStringExtra("fileUri");
        }
        fileUri = Uri.parse(fileUriString);
        
        labName = getIntent().getStringExtra("LAB_NAME");
        if (labName == null) labName = getIntent().getStringExtra("labName");
        
        testName = getIntent().getStringExtra("TEST_NAME");
        if (testName == null) testName = getIntent().getStringExtra("testName");
        
        sampleDate = getIntent().getStringExtra("COLLECTION_DATE");
        if (sampleDate == null) sampleDate = getIntent().getStringExtra("sampleDate");
        
        doctorName = getIntent().getStringExtra("DOCTOR_NAME");
        if (doctorName == null) doctorName = getIntent().getStringExtra("doctorName");
        
        patientName = getIntent().getStringExtra("PATIENT_NAME");
        if (patientName == null) patientName = getIntent().getStringExtra("patientName");

        // Initialize views
        ivReportDetail = findViewById(R.id.ivReportDetail);
        btnBack = findViewById(R.id.btnBack);
        btnShare = findViewById(R.id.btnShare);
        btnDownload = findViewById(R.id.btnDownload);
        btnSyncABHA = findViewById(R.id.btnSyncABHA);

        // Load report image
        ivReportDetail.setImageURI(fileUri);

        // Back button
        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(ReportDetailActivity.this, PathologyActivity.class));
            finish();
        });

        // Share button
        btnShare.setOnClickListener(v -> shareReport());

        // Download button
        btnDownload.setOnClickListener(v -> downloadReport());

        // Sync with ABHA button
        btnSyncABHA.setOnClickListener(v -> syncWithABHA());
    }

    private void shareReport() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share Report"));
    }

    private void downloadReport() {
        // TODO: Implement download logic
        Toast.makeText(this, "Report Downloaded", Toast.LENGTH_SHORT).show();
    }

    private void syncWithABHA() {
        // TODO: Implement ABHA sync logic
        Toast.makeText(this, "Syncing with ABHA...", Toast.LENGTH_SHORT).show();
        // Navigate to ABHA activity or show sync dialog
    }
}
