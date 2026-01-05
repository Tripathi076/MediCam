package com.example.medicam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.example.medicam.models.UserProfile;
import com.example.medicam.utils.SessionManager;

public class ProfileActivity extends AppCompatActivity {

    private ImageView btnBack, imgProfile;
    private TextView tvUserName, tvUserInfo;
    private LinearLayout menuPersonalInfo, menuAddressDetails, menuFamilyDetails;
    private LinearLayout menuHealthInfo, menuSettings, menuHelpSupport, menuAbout;
    private MaterialButton btnLogout;

    private SessionManager sessionManager;
    private SharedPreferences prefs;
    private Gson gson;

    private static final String PREFS_NAME = "ProfilePrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sessionManager = SessionManager.getInstance(this);
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        gson = new Gson();

        initViews();
        loadUserProfile();
        setupClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        imgProfile = findViewById(R.id.imgProfile);
        tvUserName = findViewById(R.id.tvUserName);
        tvUserInfo = findViewById(R.id.tvUserInfo);

        menuPersonalInfo = findViewById(R.id.menuPersonalInfo);
        menuAddressDetails = findViewById(R.id.menuAddressDetails);
        menuFamilyDetails = findViewById(R.id.menuFamilyDetails);
        menuHealthInfo = findViewById(R.id.menuHealthInfo);
        menuSettings = findViewById(R.id.menuSettings);
        menuHelpSupport = findViewById(R.id.menuHelpSupport);
        menuAbout = findViewById(R.id.menuAbout);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void loadUserProfile() {
        UserProfile profile = getUserProfile();

        if (profile != null && profile.getFullName() != null) {
            tvUserName.setText(profile.getFullName());
            int age = profile.getAge();
            String gender = profile.getGender() != null ? profile.getGender() : "";
            if (age > 0 && !gender.isEmpty()) {
                tvUserInfo.setText(age + " Years/" + gender);
            } else if (age > 0) {
                tvUserInfo.setText(age + " Years");
            } else {
                tvUserInfo.setText("Update your profile");
            }

            // Load profile image
            if (profile.getProfileImageUri() != null && !profile.getProfileImageUri().isEmpty()) {
                try {
                    imgProfile.setImageURI(Uri.parse(profile.getProfileImageUri()));
                } catch (Exception e) {
                    imgProfile.setImageResource(R.drawable.ic_person);
                }
            }
        } else {
            // Use data from SessionManager
            String userName = sessionManager.getUserName();
            String userPhone = sessionManager.getUserPhone();
            tvUserName.setText(userName != null && !userName.isEmpty() ? userName : "User");
            tvUserInfo.setText(userPhone != null ? userPhone : "Update your profile");
        }
    }

    private UserProfile getUserProfile() {
        String json = prefs.getString("user_profile", null);
        if (json != null) {
            return gson.fromJson(json, UserProfile.class);
        }
        return null;
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        menuPersonalInfo.setOnClickListener(v -> {
            startActivity(new Intent(this, PersonalInformationActivity.class));
        });

        menuAddressDetails.setOnClickListener(v -> {
            startActivity(new Intent(this, AddressDetailsActivity.class));
        });

        menuFamilyDetails.setOnClickListener(v -> {
            startActivity(new Intent(this, FamilyDetailsActivity.class));
        });

        menuHealthInfo.setOnClickListener(v -> {
            startActivity(new Intent(this, HealthInformationActivity.class));
        });

        menuSettings.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
        });

        menuHelpSupport.setOnClickListener(v -> {
            startActivity(new Intent(this, HelpSupportActivity.class));
        });

        menuAbout.setOnClickListener(v -> {
            startActivity(new Intent(this, AboutActivity.class));
        });

        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes", (dialog, which) -> performLogout())
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void performLogout() {
        sessionManager.clearSession();
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserProfile();
    }
}
