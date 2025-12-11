package com.example.medicam;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class RadiologyDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radiology_detail);

        ImageView btnBack = findViewById(R.id.btnBack);
        ImageView btnShare = findViewById(R.id.btnShare);
        ImageView btnDownload = findViewById(R.id.btnDownload);
        ImageView ivReportImage = findViewById(R.id.ivReportImage);
        MaterialButton btnSyncABHA = findViewById(R.id.btnSyncABHA);

        String fileUriString = getIntent().getStringExtra("FILE_URI");
        if (fileUriString != null) {
            Uri fileUri = Uri.parse(fileUriString);
            ivReportImage.setImageURI(fileUri);
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
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Radiology Report");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Sharing radiology report");
        startActivity(Intent.createChooser(shareIntent, "Share Report"));
    }

    private void downloadReport() {
        // TODO: Implement download functionality
        Toast.makeText(this, "Download functionality coming soon", Toast.LENGTH_SHORT).show();
    }

    private void syncWithABHA() {
        // TODO: Implement ABHA sync
        Toast.makeText(this, "Syncing with ABHA...", Toast.LENGTH_SHORT).show();
    }
}
