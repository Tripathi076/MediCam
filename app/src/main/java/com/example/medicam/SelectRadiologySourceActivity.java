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

public class SelectRadiologySourceActivity extends AppCompatActivity {

    private Uri photoUri;
    private ActivityResultLauncher<Uri> cameraLauncher;
    private ActivityResultLauncher<String> filePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_radiology_source);

        ImageView btnBack = findViewById(R.id.btnBack);
        MaterialCardView cardTakePhoto = findViewById(R.id.cardTakePhoto);
        MaterialCardView cardUploadFile = findViewById(R.id.cardUploadFile);

        btnBack.setOnClickListener(v -> finish());

        setupActivityLaunchers();

        cardTakePhoto.setOnClickListener(v -> {
            if (checkCameraPermission()) {
                openCamera();
            } else {
                requestCameraPermission();
            }
        });

        cardUploadFile.setOnClickListener(v -> openFilePicker());
    }

    private void setupActivityLaunchers() {
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                result -> {
                    if (result && photoUri != null) {
                        navigateToPreview(photoUri);
                    }
                }
        );

        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        navigateToPreview(uri);
                    }
                }
        );
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
    }

    private void openCamera() {
        try {
            File photoFile = createImageFile();
            photoUri = FileProvider.getUriForFile(this, "com.example.medicam.fileprovider", photoFile);
            cameraLauncher.launch(photoUri);
        } catch (IOException e) {
            Toast.makeText(this, "Error creating file", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String fileName = "radiology_" + System.currentTimeMillis();
        File storageDir = getExternalFilesDir("Pictures");
        return File.createTempFile(fileName, ".jpg", storageDir);
    }

    private void openFilePicker() {
        filePickerLauncher.launch("image/*");
    }

    private void navigateToPreview(Uri fileUri) {
        Intent intent = new Intent(SelectRadiologySourceActivity.this, RadiologyPreviewActivity.class);
        intent.putExtra("FILE_URI", fileUri.toString());
        intent.putExtras(getIntent().getExtras());
        startActivity(intent);
    }
}
