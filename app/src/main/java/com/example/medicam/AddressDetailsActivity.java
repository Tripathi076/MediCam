package com.example.medicam;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.example.medicam.models.UserProfile;

public class AddressDetailsActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextInputEditText etAddress, etCity, etState, etPincode;
    private MaterialButton btnSave;

    private SharedPreferences prefs;
    private Gson gson;

    private static final String PREFS_NAME = "ProfilePrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_details);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        gson = new Gson();

        initViews();
        loadAddressData();
        setupClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        etAddress = findViewById(R.id.etAddress);
        etCity = findViewById(R.id.etCity);
        etState = findViewById(R.id.etState);
        etPincode = findViewById(R.id.etPincode);
        btnSave = findViewById(R.id.btnSave);
    }

    private void loadAddressData() {
        String json = prefs.getString("user_profile", null);
        if (json != null) {
            UserProfile profile = gson.fromJson(json, UserProfile.class);
            if (profile != null) {
                if (profile.getAddress() != null) etAddress.setText(profile.getAddress());
                if (profile.getCity() != null) etCity.setText(profile.getCity());
                if (profile.getState() != null) etState.setText(profile.getState());
                if (profile.getPincode() != null) etPincode.setText(profile.getPincode());
            }
        }
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> saveAddressData());
    }

    private void saveAddressData() {
        String address = etAddress.getText() != null ? etAddress.getText().toString().trim() : "";
        String city = etCity.getText() != null ? etCity.getText().toString().trim() : "";
        String state = etState.getText() != null ? etState.getText().toString().trim() : "";
        String pincode = etPincode.getText() != null ? etPincode.getText().toString().trim() : "";

        // Get existing profile or create new
        String json = prefs.getString("user_profile", null);
        UserProfile profile;
        if (json != null) {
            profile = gson.fromJson(json, UserProfile.class);
        } else {
            profile = new UserProfile();
        }

        // Update address fields
        profile.setAddress(address);
        profile.setCity(city);
        profile.setState(state);
        profile.setPincode(pincode);

        // Save updated profile
        String updatedJson = gson.toJson(profile);
        prefs.edit().putString("user_profile", updatedJson).apply();

        Toast.makeText(this, "Address saved successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}
