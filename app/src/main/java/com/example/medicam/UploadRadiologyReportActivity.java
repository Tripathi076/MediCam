package com.example.medicam;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class UploadRadiologyReportActivity extends AppCompatActivity {

    private TextInputEditText etCenterName, etScanDate, etDoctorName, etPatientName;
    private AutoCompleteTextView etScanType;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_radiology_report);

        ImageView btnBack = findViewById(R.id.btnBack);
        etCenterName = findViewById(R.id.etCenterName);
        etScanType = findViewById(R.id.etScanType);
        etScanDate = findViewById(R.id.etScanDate);
        etDoctorName = findViewById(R.id.etDoctorName);
        etPatientName = findViewById(R.id.etPatientName);
        MaterialButton btnNext = findViewById(R.id.btnNext);

        calendar = Calendar.getInstance();

        btnBack.setOnClickListener(v -> finish());

        setupScanTypeDropdown();
        setupDatePicker();

        btnNext.setOnClickListener(v -> {
            if (validateFields()) {
                Intent intent = new Intent(UploadRadiologyReportActivity.this, SelectRadiologySourceActivity.class);
                intent.putExtra("CENTER_NAME", etCenterName.getText().toString());
                intent.putExtra("SCAN_TYPE", etScanType.getText().toString());
                intent.putExtra("SCAN_DATE", etScanDate.getText().toString());
                intent.putExtra("DOCTOR_NAME", etDoctorName.getText().toString());
                intent.putExtra("PATIENT_NAME", etPatientName.getText().toString());
                startActivity(intent);
            }
        });
    }

    private void setupScanTypeDropdown() {
        String[] scanTypes = {"CT Scan", "MRI Scan", "X-Ray", "Ultrasound", "PET Scan", "Mammography", "Bone Scan", "Angiography", "Fluoroscopy", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, scanTypes);
        etScanType.setAdapter(adapter);
    }

    private void setupDatePicker() {
        etScanDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    UploadRadiologyReportActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        etScanDate.setText(sdf.format(calendar.getTime()));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
    }

    private boolean validateFields() {
        String centerName = etCenterName.getText().toString().trim();
        String scanType = etScanType.getText().toString().trim();
        String scanDate = etScanDate.getText().toString().trim();
        String doctorName = etDoctorName.getText().toString().trim();
        String patientName = etPatientName.getText().toString().trim();

        if (centerName.isEmpty()) {
            etCenterName.setError("Required");
            return false;
        }
        if (scanType.isEmpty()) {
            etScanType.setError("Required");
            return false;
        }
        if (scanDate.isEmpty()) {
            etScanDate.setError("Required");
            return false;
        }
        if (doctorName.isEmpty()) {
            etDoctorName.setError("Required");
            return false;
        }
        if (patientName.isEmpty()) {
            etPatientName.setError("Required");
            return false;
        }
        return true;
    }
}
