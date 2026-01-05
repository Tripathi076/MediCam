package com.example.medicam;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

public class HelpSupportActivity extends AppCompatActivity {

    private ImageView btnBack;
    private MaterialCardView cardReportBug, cardRequestFeature, cardShareFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_support);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        cardReportBug = findViewById(R.id.cardReportBug);
        cardRequestFeature = findViewById(R.id.cardRequestFeature);
        cardShareFeedback = findViewById(R.id.cardShareFeedback);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        cardReportBug.setOnClickListener(v -> {
            openEmailClient(
                "Bug Report - MediCam App",
                "Please describe the bug you encountered:\n\n" +
                "Device: \n" +
                "Android Version: \n" +
                "Steps to reproduce:\n" +
                "1. \n" +
                "2. \n" +
                "3. \n\n" +
                "Expected behavior:\n\n" +
                "Actual behavior:\n"
            );
        });

        cardRequestFeature.setOnClickListener(v -> {
            openEmailClient(
                "Feature Request - MediCam App",
                "Please describe the feature you'd like to see:\n\n" +
                "Feature title:\n\n" +
                "Description:\n\n" +
                "Why would this feature be useful?\n"
            );
        });

        cardShareFeedback.setOnClickListener(v -> {
            openEmailClient(
                "Feedback - MediCam App",
                "We'd love to hear your feedback!\n\n" +
                "What do you like about the app?\n\n" +
                "What could be improved?\n\n" +
                "Any other comments:\n"
            );
        });
    }

    private void openEmailClient(String subject, String body) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@medicam.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);

        try {
            startActivity(Intent.createChooser(intent, "Send Email"));
        } catch (Exception e) {
            Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show();
        }
    }
}
