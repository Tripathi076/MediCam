package com.example.medicam;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.card.MaterialCardView;

import java.io.File;
import java.io.IOException;

public class SelectReportSourceActivity extends AppCompatActivity {

    private MaterialCardView cardTakePhoto, cardUploadFile;
    private ImageView btnBack;
    private Uri photoUri;

    private String labName, testName, sampleDate, doctorName, patientName;

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    openReportPreview(photoUri);
                }
            });

    private final ActivityResultLauncher<Intent> fileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri fileUri = result.getData().getData();
                    openReportPreview(fileUri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_report_source);

        // Get data from previous activity
        labName = getIntent().getStringExtra("labName");
        testName = getIntent().getStringExtra("testName");
        sampleDate = getIntent().getStringExtra("sampleDate");
        doctorName = getIntent().getStringExtra("doctorName");
        patientName = getIntent().getStringExtra("patientName");

        // Initialize views
        cardTakePhoto = findViewById(R.id.cardTakePhoto);
        cardUploadFile = findViewById(R.id.cardUploadFile);
        btnBack = findViewById(R.id.btnBack);

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Take Photo
        cardTakePhoto.setOnClickListener(v -> {
            if (checkCameraPermission()) {
                openCamera();
            } else {
                requestCameraPermission();
            }
        });

        // Upload File
        cardUploadFile.setOnClickListener(v -> openFilePicker());
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA}, 100);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            try {
                File photoFile = File.createTempFile("report_", ".jpg", getCacheDir());
                photoUri = FileProvider.getUriForFile(this,
                        getApplicationContext().getPackageName() + ".provider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                cameraLauncher.launch(intent);
            } catch (IOException e) {
                Toast.makeText(this, "Error creating photo file", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        String[] mimeTypes = {"image/jpeg", "image/png", "application/pdf"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        fileLauncher.launch(intent);
    }

    private void openReportPreview(Uri fileUri) {
        Intent intent = new Intent(SelectReportSourceActivity.this, ReportPreviewActivity.class);
        intent.putExtra("fileUri", fileUri.toString());
        intent.putExtra("labName", labName);
        intent.putExtra("testName", testName);
        intent.putExtra("sampleDate", sampleDate);
        intent.putExtra("doctorName", doctorName);
        intent.putExtra("patientName", patientName);
        startActivity(intent);
    }
}
