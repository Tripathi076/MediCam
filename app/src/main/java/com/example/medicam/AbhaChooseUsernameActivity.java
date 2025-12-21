package com.example.medicam;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class AbhaChooseUsernameActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abha_choose_username);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnContinue).setOnClickListener(v ->
                startActivity(new Intent(this, AbhaSuccessActivity.class)));
    }
}
