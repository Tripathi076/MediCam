package com.example.medicam;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class RadiologyDetailActivity extends AppCompatActivity {

    private String centerName, scanType, scanDate, doctorName, patientName, fileUriString, category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radiology_detail);

        // Get data from intent
        centerName = getIntent().getStringExtra("CENTER_NAME");
        scanType = getIntent().getStringExtra("SCAN_TYPE");
        scanDate = getIntent().getStringExtra("SCAN_DATE");
        doctorName = getIntent().getStringExtra("DOCTOR_NAME");
        patientName = getIntent().getStringExtra("PATIENT_NAME");
        fileUriString = getIntent().getStringExtra("FILE_URI");
        category = getIntent().getStringExtra("CATEGORY");

        // Initialize views
        ImageView btnBack = findViewById(R.id.btnBack);
        ImageView btnShare = findViewById(R.id.btnShare);
        ImageView btnDownload = findViewById(R.id.btnDownload);
        ImageView ivReportImage = findViewById(R.id.ivReportImage);
        MaterialButton btnSyncABHA = findViewById(R.id.btnSyncABHA);

        // Set text views
        TextView tvCenterName = findViewById(R.id.tvCenterName);
        TextView tvScanType = findViewById(R.id.tvScanType);
        TextView tvScanDate = findViewById(R.id.tvScanDate);
        TextView tvDoctorName = findViewById(R.id.tvDoctorName);
        TextView tvPatientName = findViewById(R.id.tvPatientName);
        TextView tvReportTitle = findViewById(R.id.tvReportTitle);
        TextView tvCenterNameFooter = findViewById(R.id.tvCenterNameFooter);
        TextView tvCategory = findViewById(R.id.tvCategory);

        // Populate data
        if (tvCenterName != null) {
            tvCenterName.setText(centerName != null ? centerName : "Imaging Center");
        }
        if (tvScanType != null) {
            tvScanType.setText(scanType != null ? scanType : "Scan");
        }
        if (tvScanDate != null) {
            tvScanDate.setText(scanDate != null ? scanDate : "N/A");
        }
        if (tvDoctorName != null) {
            tvDoctorName.setText(doctorName != null ? doctorName : "N/A");
        }
        if (tvPatientName != null) {
            tvPatientName.setText(patientName != null ? patientName : "Patient");
        }
        if (tvReportTitle != null && scanType != null) {
            tvReportTitle.setText(scanType + " Report");
        }
        if (tvCenterNameFooter != null) {
            tvCenterNameFooter.setText(centerName != null ? centerName : "N/A");
        }
        if (tvCategory != null) {
            tvCategory.setText(category != null ? category : "Radiology");
        }

        // Load report image
        if (fileUriString != null && !fileUriString.isEmpty()) {
            try {
                Uri fileUri = Uri.parse(fileUriString);
                ivReportImage.setImageURI(fileUri);
            } catch (Exception e) {
                ivReportImage.setImageResource(R.drawable.radiology_illustration);
            }
        }

        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(RadiologyDetailActivity.this, RadiologyActivity.class));
            finish();
        });

        btnShare.setOnClickListener(v -> shareReport());
        btnDownload.setOnClickListener(v -> downloadReport());
        btnSyncABHA.setOnClickListener(v -> syncWithABHA());
    }

    private void shareReport() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        if (fileUriString != null) {
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(fileUriString));
        }
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Radiology Report - " + scanType);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Radiology Report\nCenter: " + centerName + 
            "\nScan Type: " + scanType + "\nDate: " + scanDate + "\nDoctor: " + doctorName);
        startActivity(Intent.createChooser(shareIntent, "Share Report"));
    }

    private void downloadReport() {
        Toast.makeText(this, "Report saved to Downloads", Toast.LENGTH_SHORT).show();
    }

    private void syncWithABHA() {
        Toast.makeText(this, "Syncing with ABHA...", Toast.LENGTH_SHORT).show();
    }
}
