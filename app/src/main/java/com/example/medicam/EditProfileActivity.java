package com.example.medicam;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.example.medicam.models.UserProfile;

import java.util.Calendar;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView btnBack, imgProfile, btnCamera;
    private TextInputEditText etFullName, etDateOfBirth, etEmail, etHeight, etWeight;
    private AutoCompleteTextView spinnerBloodGroup;
    private CheckBox cbMale, cbFemale, cbOthers;
    private MaterialButton btnSave;
    private TextView tvTitle;

    private SharedPreferences prefs;
    private Gson gson;
    private UserProfile userProfile;
    private Uri selectedImageUri;

    private static final String PREFS_NAME = "ProfilePrefs";
    private String[] bloodGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        gson = new Gson();

        initViews();
        setupImagePicker();
        loadUserProfile();
        setupClickListeners();
        setupBloodGroupSpinner();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        imgProfile = findViewById(R.id.imgProfile);
        btnCamera = findViewById(R.id.btnCamera);
        tvTitle = findViewById(R.id.tvTitle);
        etFullName = findViewById(R.id.etFullName);
        etDateOfBirth = findViewById(R.id.etDateOfBirth);
        etEmail = findViewById(R.id.etEmail);
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
                        // Take persistent permission
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

    private void loadUserProfile() {
        String json = prefs.getString("user_profile", null);
        if (json != null) {
            userProfile = gson.fromJson(json, UserProfile.class);
            displayUserProfile();
        } else {
            userProfile = new UserProfile();
        }
    }

    private void displayUserProfile() {
        if (userProfile.getFullName() != null) {
            etFullName.setText(userProfile.getFullName());
        }
        if (userProfile.getDateOfBirth() != null) {
            etDateOfBirth.setText(userProfile.getDateOfBirth());
        }
        if (userProfile.getEmail() != null) {
            etEmail.setText(userProfile.getEmail());
        }
        if (userProfile.getBloodGroup() != null) {
            spinnerBloodGroup.setText(userProfile.getBloodGroup(), false);
        }
        if (userProfile.getHeight() > 0) {
            etHeight.setText(String.valueOf((int) userProfile.getHeight()));
        }
        if (userProfile.getWeight() > 0) {
            etWeight.setText(String.valueOf((int) userProfile.getWeight()));
        }

        // Set gender
        String gender = userProfile.getGender();
        if (gender != null) {
            cbMale.setChecked(gender.equals("Male"));
            cbFemale.setChecked(gender.equals("Female"));
            cbOthers.setChecked(gender.equals("Others"));
        }

        // Load profile image
        if (userProfile.getProfileImageUri() != null && !userProfile.getProfileImageUri().isEmpty()) {
            try {
                selectedImageUri = Uri.parse(userProfile.getProfileImageUri());
                imgProfile.setImageURI(selectedImageUri);
            } catch (Exception e) {
                imgProfile.setImageResource(R.drawable.ic_person);
            }
        }
    }

    private void setupBloodGroupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_dropdown_item_1line, bloodGroups);
        spinnerBloodGroup.setAdapter(adapter);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnCamera.setOnClickListener(v -> openImagePicker());
        imgProfile.setOnClickListener(v -> openImagePicker());

        etDateOfBirth.setOnClickListener(v -> showDatePicker());

        // Gender checkboxes - only one can be selected
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

        btnSave.setOnClickListener(v -> saveProfile());
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

        // Parse existing date if available
        String existingDate = etDateOfBirth.getText().toString();
        if (!existingDate.isEmpty()) {
            try {
                String[] parts = existingDate.split("/");
                if (parts.length == 3) {
                    day = Integer.parseInt(parts[0]);
                    month = Integer.parseInt(parts[1]) - 1;
                    year = Integer.parseInt(parts[2]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
            (view, selectedYear, selectedMonth, selectedDay) -> {
                String date = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                etDateOfBirth.setText(date);
            }, year, month, day);

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void saveProfile() {
        String fullName = etFullName.getText().toString().trim();
        String dob = etDateOfBirth.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String bloodGroup = spinnerBloodGroup.getText().toString().trim();
        String heightStr = etHeight.getText().toString().trim();
        String weightStr = etWeight.getText().toString().trim();

        if (fullName.isEmpty()) {
            etFullName.setError("Name is required");
            return;
        }

        userProfile.setFullName(fullName);
        userProfile.setDateOfBirth(dob);
        userProfile.setEmail(email);
        userProfile.setBloodGroup(bloodGroup);

        if (!heightStr.isEmpty()) {
            try {
                userProfile.setHeight(Float.parseFloat(heightStr));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        if (!weightStr.isEmpty()) {
            try {
                userProfile.setWeight(Float.parseFloat(weightStr));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        // Set gender
        if (cbMale.isChecked()) {
            userProfile.setGender("Male");
        } else if (cbFemale.isChecked()) {
            userProfile.setGender("Female");
        } else if (cbOthers.isChecked()) {
            userProfile.setGender("Others");
        }

        // Set profile image
        if (selectedImageUri != null) {
            userProfile.setProfileImageUri(selectedImageUri.toString());
        }

        // Save to SharedPreferences
        String json = gson.toJson(userProfile);
        prefs.edit().putString("user_profile", json).apply();

        Toast.makeText(this, "Profile saved successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}
