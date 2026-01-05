package com.example.medicam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.example.medicam.models.UserProfile;
import com.example.medicam.models.FamilyMember;
import com.example.medicam.adapters.FamilyMemberSmallAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PersonalInformationActivity extends AppCompatActivity {

    private ImageView btnBack, imgProfile;
    private TextView tvTitle, tvUserName, tvHeight, tvWeight, tvAge, tvBloodGroup, tvAboutMe;
    private MaterialButton btnEditProfile;
    private RecyclerView rvFamilyMembers;
    private LinearLayout layoutAddNew;

    private SharedPreferences prefs;
    private Gson gson;
    private UserProfile userProfile;
    private List<FamilyMember> familyMembers;
    private FamilyMemberSmallAdapter adapter;

    private static final String PREFS_NAME = "ProfilePrefs";
    private static final String FAMILY_PREFS = "FamilyPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        gson = new Gson();

        initViews();
        loadUserProfile();
        loadFamilyMembers();
        setupClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        imgProfile = findViewById(R.id.imgProfile);
        tvTitle = findViewById(R.id.tvTitle);
        tvUserName = findViewById(R.id.tvUserName);
        tvHeight = findViewById(R.id.tvHeight);
        tvWeight = findViewById(R.id.tvWeight);
        tvAge = findViewById(R.id.tvAge);
        tvBloodGroup = findViewById(R.id.tvBloodGroup);
        tvAboutMe = findViewById(R.id.tvAboutMe);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        rvFamilyMembers = findViewById(R.id.rvFamilyMembers);
        layoutAddNew = findViewById(R.id.layoutAddNew);
    }

    private void loadUserProfile() {
        String json = prefs.getString("user_profile", null);
        if (json != null) {
            userProfile = gson.fromJson(json, UserProfile.class);
            displayUserProfile();
        } else {
            userProfile = new UserProfile();
            tvUserName.setText("Update Profile");
            tvHeight.setText("--");
            tvWeight.setText("--");
            tvAge.setText("--");
            tvBloodGroup.setText("--");
            tvAboutMe.setText("Tell us about yourself...");
        }
    }

    private void displayUserProfile() {
        if (userProfile.getFullName() != null && !userProfile.getFullName().isEmpty()) {
            tvUserName.setText(userProfile.getFullName());
        }

        if (userProfile.getHeight() > 0) {
            tvHeight.setText(String.format("%.1f in", userProfile.getHeight() / 2.54f));
        } else {
            tvHeight.setText("--");
        }

        if (userProfile.getWeight() > 0) {
            tvWeight.setText(String.format("%.0f Kg", userProfile.getWeight()));
        } else {
            tvWeight.setText("--");
        }

        int age = userProfile.getAge();
        if (age > 0) {
            tvAge.setText(age + " Years");
        } else {
            tvAge.setText("--");
        }

        if (userProfile.getBloodGroup() != null && !userProfile.getBloodGroup().isEmpty()) {
            tvBloodGroup.setText(userProfile.getBloodGroup());
        } else {
            tvBloodGroup.setText("--");
        }

        if (userProfile.getAboutMe() != null && !userProfile.getAboutMe().isEmpty()) {
            tvAboutMe.setText(userProfile.getAboutMe());
        } else {
            tvAboutMe.setText("Tell us about yourself...");
        }

        // Load profile image
        if (userProfile.getProfileImageUri() != null && !userProfile.getProfileImageUri().isEmpty()) {
            try {
                imgProfile.setImageURI(Uri.parse(userProfile.getProfileImageUri()));
            } catch (Exception e) {
                imgProfile.setImageResource(R.drawable.ic_person);
            }
        }
    }

    private void loadFamilyMembers() {
        SharedPreferences familyPrefs = getSharedPreferences(FAMILY_PREFS, MODE_PRIVATE);
        String json = familyPrefs.getString("family_members", null);
        
        if (json != null) {
            Type type = new TypeToken<ArrayList<FamilyMember>>(){}.getType();
            familyMembers = gson.fromJson(json, type);
        } else {
            familyMembers = new ArrayList<>();
        }

        setupFamilyRecyclerView();
    }

    private void setupFamilyRecyclerView() {
        rvFamilyMembers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new FamilyMemberSmallAdapter(this, familyMembers, true, new FamilyMemberSmallAdapter.OnMemberClickListener() {
            @Override
            public void onMemberClick(FamilyMember member) {
                Intent intent = new Intent(PersonalInformationActivity.this, FamilyMemberInfoActivity.class);
                intent.putExtra("member_id", member.getId());
                startActivity(intent);
            }
            
            @Override
            public void onAddNewClick() {
                startActivity(new Intent(PersonalInformationActivity.this, AddFamilyMemberActivity.class));
            }
        });
        rvFamilyMembers.setAdapter(adapter);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnEditProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, EditProfileActivity.class));
        });

        layoutAddNew.setOnClickListener(v -> {
            startActivity(new Intent(this, AddFamilyMemberActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserProfile();
        loadFamilyMembers();
    }
}
