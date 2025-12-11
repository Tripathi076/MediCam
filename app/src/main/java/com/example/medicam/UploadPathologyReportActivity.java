package com.example.medicam;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class UploadPathologyReportActivity extends AppCompatActivity {

    private TextInputEditText etLabName, etDoctorName, etPatientName, etSampleDate;
    private AutoCompleteTextView actvTestName;
    private MaterialButton btnNext;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pathology_report);

        // Initialize views
        etLabName = findViewById(R.id.etLabName);
        actvTestName = findViewById(R.id.actvTestName);
        etSampleDate = findViewById(R.id.etSampleDate);
        etDoctorName = findViewById(R.id.etDoctorName);
        etPatientName = findViewById(R.id.etPatientName);
        btnNext = findViewById(R.id.btnNext);
        btnBack = findViewById(R.id.btnBack);

        // Setup test name dropdown
        setupTestNameDropdown();

        // Setup date picker
        setupDatePicker();

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Next button
        btnNext.setOnClickListener(v -> {
            if (validateFields()) {
                Intent intent = new Intent(UploadPathologyReportActivity.this, SelectReportSourceActivity.class);
                intent.putExtra("labName", etLabName.getText().toString());
                intent.putExtra("testName", actvTestName.getText().toString());
                intent.putExtra("sampleDate", etSampleDate.getText().toString());
                intent.putExtra("doctorName", etDoctorName.getText().toString());
                intent.putExtra("patientName", etPatientName.getText().toString());
                startActivity(intent);
            }
        });
    }

    private void setupTestNameDropdown() {
        String[] testNames = {
                "Complete Blood Count (CBC)",
                "Blood Glucose Test",
                "Lipid Profile",
                "Liver Function Test",
                "Kidney Function Test",
                "Thyroid Function Test",
                "Hemoglobin A1C",
                "Vitamin D Test",
                "Urine Analysis",
                "Chest X-Ray"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, testNames);
        actvTestName.setAdapter(adapter);
    }

    private void setupDatePicker() {
        etSampleDate.setFocusable(false);
        etSampleDate.setClickable(true);
        etSampleDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    UploadPathologyReportActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        etSampleDate.setText(date);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });
    }

    private boolean validateFields() {
        if (etLabName.getText().toString().trim().isEmpty()) {
            etLabName.setError("Enter Lab/Pathology Name");
            return false;
        }
        if (actvTestName.getText().toString().trim().isEmpty()) {
            actvTestName.setError("Select Test Name");
            return false;
        }
        if (etSampleDate.getText().toString().trim().isEmpty()) {
            etSampleDate.setError("Select Sample Collection Date");
            return false;
        }
        if (etDoctorName.getText().toString().trim().isEmpty()) {
            etDoctorName.setError("Enter Doctor Name");
            return false;
        }
        if (etPatientName.getText().toString().trim().isEmpty()) {
            etPatientName.setError("Enter Patient Name");
            return false;
        }
        return true;
    }
}
