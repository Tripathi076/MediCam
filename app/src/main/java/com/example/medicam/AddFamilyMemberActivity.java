package com.example.medicam;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.example.medicam.models.FamilyMember;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddFamilyMemberActivity extends AppCompatActivity {

    private ImageView btnBack, imgProfile, btnCamera;
    private TextInputEditText etFullName, etDateOfBirth, etHeight, etWeight;
    private AutoCompleteTextView spinnerRelation, spinnerBloodGroup;
    private CheckBox cbMale, cbFemale, cbOthers;
    private MaterialButton btnSave;
    private TextView tvTitle;

    private SharedPreferences prefs;
    private Gson gson;
    private List<FamilyMember> familyMembers;
    private Uri selectedImageUri;

    private static final String FAMILY_PREFS = "FamilyPrefs";
    private String[] bloodGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
    private String[] relations = {"Brother", "Sister", "Father", "Mother", "Spouse", "Son", "Daughter", "Cousin", "Uncle", "Aunt", "Grandparent", "Other"};

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_family_member);

        prefs = getSharedPreferences(FAMILY_PREFS, MODE_PRIVATE);
        gson = new Gson();

        initViews();
        setupImagePicker();
        loadExistingMembers();
        setupClickListeners();
        setupSpinners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        imgProfile = findViewById(R.id.imgProfile);
        btnCamera = findViewById(R.id.btnCamera);
        tvTitle = findViewById(R.id.tvTitle);
        etFullName = findViewById(R.id.etFullName);
        etDateOfBirth = findViewById(R.id.etDateOfBirth);
        spinnerRelation = findViewById(R.id.spinnerRelation);
        spinnerBloodGroup = findViewById(R.id.spinnerBloodGroup);
        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);
        cbMale = findViewById(R.id.cbMale);
        cbFemale = findViewById(R.id.cbFemale);
        cbOthers = findViewById(R.id.cbOthers);
        btnSave = findViewById(R.id.btnSave);
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        try {
                            getContentResolver().takePersistableUriPermission(selectedImageUri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        imgProfile.setImageURI(selectedImageUri);
                    }
                }
            }
        );
    }

    private void loadExistingMembers() {
        String json = prefs.getString("family_members", null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<FamilyMember>>(){}.getType();
            familyMembers = gson.fromJson(json, type);
        } else {
            familyMembers = new ArrayList<>();
        }
    }

    private void setupSpinners() {
        ArrayAdapter<String> bloodAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_dropdown_item_1line, bloodGroups);
        spinnerBloodGroup.setAdapter(bloodAdapter);

        ArrayAdapter<String> relationAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_dropdown_item_1line, relations);
        spinnerRelation.setAdapter(relationAdapter);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnCamera.setOnClickListener(v -> openImagePicker());
        imgProfile.setOnClickListener(v -> openImagePicker());

        etDateOfBirth.setOnClickListener(v -> showDatePicker());

        // Gender checkboxes
        cbMale.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                cbFemale.setChecked(false);
                cbOthers.setChecked(false);
            }
        });
        cbFemale.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                cbMale.setChecked(false);
                cbOthers.setChecked(false);
            }
        });
        cbOthers.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                cbMale.setChecked(false);
                cbFemale.setChecked(false);
            }
        });

        btnSave.setOnClickListener(v -> saveFamilyMember());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        imagePickerLauncher.launch(intent);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
            (view, selectedYear, selectedMonth, selectedDay) -> {
                String date = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                etDateOfBirth.setText(date);
            }, year, month, day);

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void saveFamilyMember() {
        String fullName = etFullName.getText().toString().trim();
        String dob = etDateOfBirth.getText().toString().trim();
        String relation = spinnerRelation.getText().toString().trim();
        String bloodGroup = spinnerBloodGroup.getText().toString().trim();
        String heightStr = etHeight.getText().toString().trim();
        String weightStr = etWeight.getText().toString().trim();

        if (fullName.isEmpty()) {
            etFullName.setError("Name is required");
            return;
        }

        if (relation.isEmpty()) {
            Toast.makeText(this, "Please select relation", Toast.LENGTH_SHORT).show();
            return;
        }

        FamilyMember member = new FamilyMember();
        member.setName(fullName);
        member.setRelationship(relation);
        member.setBloodType(bloodGroup);
        member.setDateOfBirth(dob);

        // Parse height and weight
        if (!heightStr.isEmpty()) {
            try {
                member.setHeight(Float.parseFloat(heightStr));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        
        if (!weightStr.isEmpty()) {
            try {
                member.setWeight(Float.parseFloat(weightStr));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        // Parse date of birth to calculate age
        if (!dob.isEmpty()) {
            try {
                String[] parts = dob.split("/");
                if (parts.length == 3) {
                    int birthYear = Integer.parseInt(parts[2]);
                    int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                    member.setAge(currentYear - birthYear);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Set gender
        if (cbMale.isChecked()) {
            member.setGender("Male");
        } else if (cbFemale.isChecked()) {
            member.setGender("Female");
        } else if (cbOthers.isChecked()) {
            member.setGender("Other");
        }

        // Set profile image
        if (selectedImageUri != null) {
            member.setProfileImageUri(selectedImageUri.toString());
        }

        // Add to list
        familyMembers.add(member);

        // Save to SharedPreferences
        String json = gson.toJson(familyMembers);
        prefs.edit().putString("family_members", json).apply();

        Toast.makeText(this, "Family member added successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}
