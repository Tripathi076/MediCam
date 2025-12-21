package com.example.medicam;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class AbhaRegistrationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abha_registration);

        View createButton = findViewById(R.id.btnCreateAbha);
        View linkButton = findViewById(R.id.btnLinkAbha);
        View connectLater = findViewById(R.id.tvConnectLater);

        createButton.setOnClickListener(v ->
                startActivity(new Intent(this, AbhaCreateProfileActivity.class)));

        linkButton.setOnClickListener(v ->
                startActivity(new Intent(this, AbhaLinkMobileActivity.class)));

        connectLater.setOnClickListener(v -> finish());
    }
}
