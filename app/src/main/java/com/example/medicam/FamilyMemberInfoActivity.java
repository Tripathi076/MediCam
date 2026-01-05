package com.example.medicam;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.example.medicam.models.FamilyMember;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FamilyMemberInfoActivity extends AppCompatActivity {

    private ImageView btnBack, imgProfile;
    private TextView tvTitle, tvMemberName, tvRelation;
    private TextView tvHeight, tvWeight, tvBloodGroup, tvAge, tvAbhaNumber, tvBMI;
    private MaterialButton btnDelete;

    private SharedPreferences prefs;
    private Gson gson;
    private List<FamilyMember> familyMembers;
    private FamilyMember currentMember;
    private String memberId;

    private static final String FAMILY_PREFS = "FamilyPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_member_info);

        prefs = getSharedPreferences(FAMILY_PREFS, MODE_PRIVATE);
        gson = new Gson();

        memberId = getIntent().getStringExtra("member_id");

        initViews();
        loadMemberData();
        setupClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        imgProfile = findViewById(R.id.imgProfile);
        tvTitle = findViewById(R.id.tvTitle);
        tvMemberName = findViewById(R.id.tvMemberName);
        tvRelation = findViewById(R.id.tvRelation);
        tvHeight = findViewById(R.id.tvHeight);
        tvWeight = findViewById(R.id.tvWeight);
        tvBloodGroup = findViewById(R.id.tvBloodGroup);
        tvAge = findViewById(R.id.tvAge);
        tvAbhaNumber = findViewById(R.id.tvAbhaNumber);
        tvBMI = findViewById(R.id.tvBMI);
        btnDelete = findViewById(R.id.btnDelete);
    }

    private void loadMemberData() {
        String json = prefs.getString("family_members", null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<FamilyMember>>(){}.getType();
            familyMembers = gson.fromJson(json, type);

            // Find the member by ID
            for (FamilyMember member : familyMembers) {
                if (member.getId().equals(memberId)) {
                    currentMember = member;
                    break;
                }
            }

            if (currentMember != null) {
                displayMemberData();
            } else {
                Toast.makeText(this, "Member not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "No family members found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void displayMemberData() {
        tvMemberName.setText(currentMember.getName() != null ? currentMember.getName() : "N/A");
        tvRelation.setText(currentMember.getRelationship() != null ? currentMember.getRelationship() : "N/A");

        // Display stats using actual model data
        float height = currentMember.getHeight();
        float weight = currentMember.getWeight();
        
        tvHeight.setText(height > 0 ? String.format("%.0f CM", height) : "-- CM");
        tvWeight.setText(weight > 0 ? String.format("%.0f KG", weight) : "-- KG");
        tvBloodGroup.setText(currentMember.getBloodType() != null ? currentMember.getBloodType() : "--");
        tvAge.setText(currentMember.getAge() > 0 ? currentMember.getAge() + " Years" : "-- Years");

        // ABHA Number (placeholder for now)
        tvAbhaNumber.setText("Not linked");

        // BMI Calculation
        float bmi = currentMember.getBMI();
        if (bmi > 0) {
            tvBMI.setText(String.format("%.1f kg/m²", bmi));
        } else {
            tvBMI.setText("-- kg/m²");
        }

        // Load profile image
        if (currentMember.getProfileImageUri() != null && !currentMember.getProfileImageUri().isEmpty()) {
            try {
                imgProfile.setImageURI(Uri.parse(currentMember.getProfileImageUri()));
            } catch (Exception e) {
                imgProfile.setImageResource(R.drawable.ic_person);
            }
        }
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnDelete.setOnClickListener(v -> showDeleteConfirmation());
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
            .setTitle("Delete Profile")
            .setMessage("Are you sure you want to delete this family member?")
            .setPositiveButton("Delete", (dialog, which) -> deleteMember())
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void deleteMember() {
        if (currentMember != null && familyMembers != null) {
            familyMembers.remove(currentMember);

            String json = gson.toJson(familyMembers);
            prefs.edit().putString("family_members", json).apply();

            Toast.makeText(this, "Member deleted successfully", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
