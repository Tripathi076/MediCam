package com.example.medicam;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class RadiologyPreviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radiology_preview);

        ImageView btnBack = findViewById(R.id.btnBack);
        ImageView ivReportPreview = findViewById(R.id.ivReportPreview);
        MaterialButton btnSave = findViewById(R.id.btnSave);

        String fileUriString = getIntent().getStringExtra("FILE_URI");
        if (fileUriString != null) {
            Uri fileUri = Uri.parse(fileUriString);
            ivReportPreview.setImageURI(fileUri);
        }

        btnBack.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> {
            // TODO: Save report to database
            Toast.makeText(this, "Report saved successfully!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(RadiologyPreviewActivity.this, RadiologyDetailActivity.class);
            intent.putExtra("FILE_URI", fileUriString);
            intent.putExtras(getIntent().getExtras());
            startActivity(intent);
            finish();
        });
    }
}
