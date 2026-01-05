package com.example.medicam;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicam.models.VitalRecord;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VitalsTrackerActivity extends AppCompatActivity {

    private TextView tvBPValue, tvBPStatus, tvHRValue, tvHRStatus;
    private TextView tvTempValue, tvTempStatus, tvSpO2Value, tvSpO2Status;
    private TextView tvGlucoseValue, tvGlucoseStatus, tvWeightValue, tvWeightStatus;
    private RecyclerView rvRecords;

    private List<VitalRecord> vitalRecords;
    private VitalRecordsAdapter adapter;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    private static final String PREF_NAME = "VitalsPrefs";
    private static final String KEY_RECORDS = "vital_records";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vitals_tracker);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        gson = new Gson();

        initViews();
        loadRecords();
        updateVitalCards();
    }

    private void initViews() {
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Blood Pressure
        tvBPValue = findViewById(R.id.tvBPValue);
        tvBPStatus = findViewById(R.id.tvBPStatus);
        MaterialCardView cardBP = findViewById(R.id.cardBloodPressure);
        cardBP.setOnClickListener(v -> showAddVitalDialog(VitalRecord.TYPE_BLOOD_PRESSURE));

        // Heart Rate
        tvHRValue = findViewById(R.id.tvHRValue);
        tvHRStatus = findViewById(R.id.tvHRStatus);
        MaterialCardView cardHR = findViewById(R.id.cardHeartRate);
        cardHR.setOnClickListener(v -> showAddVitalDialog(VitalRecord.TYPE_HEART_RATE));

        // Temperature
        tvTempValue = findViewById(R.id.tvTempValue);
        tvTempStatus = findViewById(R.id.tvTempStatus);
        MaterialCardView cardTemp = findViewById(R.id.cardTemperature);
        cardTemp.setOnClickListener(v -> showAddVitalDialog(VitalRecord.TYPE_TEMPERATURE));

        // SpO2
        tvSpO2Value = findViewById(R.id.tvSpO2Value);
        tvSpO2Status = findViewById(R.id.tvSpO2Status);
        MaterialCardView cardSpO2 = findViewById(R.id.cardSpO2);
        cardSpO2.setOnClickListener(v -> showAddVitalDialog(VitalRecord.TYPE_SPO2));

        // Glucose
        tvGlucoseValue = findViewById(R.id.tvGlucoseValue);
        tvGlucoseStatus = findViewById(R.id.tvGlucoseStatus);
        MaterialCardView cardGlucose = findViewById(R.id.cardGlucose);
        cardGlucose.setOnClickListener(v -> showAddVitalDialog(VitalRecord.TYPE_GLUCOSE));

        // Weight
        tvWeightValue = findViewById(R.id.tvWeightValue);
        tvWeightStatus = findViewById(R.id.tvWeightStatus);
        MaterialCardView cardWeight = findViewById(R.id.cardWeight);
        cardWeight.setOnClickListener(v -> showAddVitalDialog(VitalRecord.TYPE_WEIGHT));

        // RecyclerView
        rvRecords = findViewById(R.id.rvRecords);
        rvRecords.setLayoutManager(new LinearLayoutManager(this));

        // FAB
        FloatingActionButton fab = findViewById(R.id.fabAddVital);
        fab.setOnClickListener(v -> showVitalTypeSelector());
    }

    private void loadRecords() {
        String json = sharedPreferences.getString(KEY_RECORDS, null);
        if (json != null) {
            Type type = new TypeToken<List<VitalRecord>>(){}.getType();
            vitalRecords = gson.fromJson(json, type);
            if (vitalRecords == null) vitalRecords = new ArrayList<>();
        } else {
            vitalRecords = new ArrayList<>();
        }
        
        adapter = new VitalRecordsAdapter(vitalRecords);
        rvRecords.setAdapter(adapter);
    }

    private void saveRecords() {
        String json = gson.toJson(vitalRecords);
        sharedPreferences.edit().putString(KEY_RECORDS, json).apply();
    }

    private void updateVitalCards() {
        // Get latest record of each type
        VitalRecord latestBP = getLatestRecord(VitalRecord.TYPE_BLOOD_PRESSURE);
        VitalRecord latestHR = getLatestRecord(VitalRecord.TYPE_HEART_RATE);
        VitalRecord latestTemp = getLatestRecord(VitalRecord.TYPE_TEMPERATURE);
        VitalRecord latestSpO2 = getLatestRecord(VitalRecord.TYPE_SPO2);
        VitalRecord latestGlucose = getLatestRecord(VitalRecord.TYPE_GLUCOSE);
        VitalRecord latestWeight = getLatestRecord(VitalRecord.TYPE_WEIGHT);

        updateCard(latestBP, tvBPValue, tvBPStatus, "--/-- mmHg");
        updateCard(latestHR, tvHRValue, tvHRStatus, "-- bpm");
        updateCard(latestTemp, tvTempValue, tvTempStatus, "--°C");
        updateCard(latestSpO2, tvSpO2Value, tvSpO2Status, "--%");
        updateCard(latestGlucose, tvGlucoseValue, tvGlucoseStatus, "-- mg/dL");
        updateCard(latestWeight, tvWeightValue, tvWeightStatus, "-- kg");
    }

    private VitalRecord getLatestRecord(String type) {
        for (int i = vitalRecords.size() - 1; i >= 0; i--) {
            if (type.equals(vitalRecords.get(i).getType())) {
                return vitalRecords.get(i);
            }
        }
        return null;
    }

    private void updateCard(VitalRecord record, TextView tvValue, TextView tvStatus, String defaultValue) {
        if (record != null) {
            tvValue.setText(record.getDisplayValue());
            tvStatus.setText(record.getStatus());
            tvStatus.setTextColor(record.getStatusColor());
        } else {
            tvValue.setText(defaultValue);
            tvStatus.setText("Tap to add");
            tvStatus.setTextColor(0xFF999999);
        }
    }

    private void showVitalTypeSelector() {
        String[] types = {"Blood Pressure", "Heart Rate", "Temperature", "SpO2", "Blood Glucose", "Weight"};
        String[] typeKeys = {VitalRecord.TYPE_BLOOD_PRESSURE, VitalRecord.TYPE_HEART_RATE, 
                VitalRecord.TYPE_TEMPERATURE, VitalRecord.TYPE_SPO2, 
                VitalRecord.TYPE_GLUCOSE, VitalRecord.TYPE_WEIGHT};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Vital Type");
        builder.setItems(types, (dialog, which) -> showAddVitalDialog(typeKeys[which]));
        builder.show();
    }

    private void showAddVitalDialog(String type) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_vital, null);
        dialog.setContentView(view);

        TextView tvTitle = view.findViewById(R.id.tvDialogTitle);
        EditText etValue1 = view.findViewById(R.id.etValue1);
        EditText etValue2 = view.findViewById(R.id.etValue2);
        TextView tvUnit = view.findViewById(R.id.tvUnit);
        EditText etNotes = view.findViewById(R.id.etNotes);

        String unit = "";
        String hint1 = "Value";
        String hint2 = "";

        switch (type) {
            case VitalRecord.TYPE_BLOOD_PRESSURE:
                tvTitle.setText("Add Blood Pressure");
                hint1 = "Systolic";
                hint2 = "Diastolic";
                unit = "mmHg";
                etValue2.setVisibility(View.VISIBLE);
                break;
            case VitalRecord.TYPE_HEART_RATE:
                tvTitle.setText("Add Heart Rate");
                hint1 = "Heart Rate";
                unit = "bpm";
                break;
            case VitalRecord.TYPE_TEMPERATURE:
                tvTitle.setText("Add Temperature");
                hint1 = "Temperature";
                unit = "°C";
                break;
            case VitalRecord.TYPE_SPO2:
                tvTitle.setText("Add SpO2");
                hint1 = "Oxygen Saturation";
                unit = "%";
                break;
            case VitalRecord.TYPE_GLUCOSE:
                tvTitle.setText("Add Blood Glucose");
                hint1 = "Blood Glucose";
                unit = "mg/dL";
                break;
            case VitalRecord.TYPE_WEIGHT:
                tvTitle.setText("Add Weight");
                hint1 = "Weight";
                unit = "kg";
                break;
        }

        etValue1.setHint(hint1);
        if (!hint2.isEmpty()) etValue2.setHint(hint2);
        tvUnit.setText(unit);

        String finalUnit = unit;
        view.findViewById(R.id.btnSave).setOnClickListener(v -> {
            String val1Str = etValue1.getText().toString().trim();
            if (val1Str.isEmpty()) {
                etValue1.setError("Required");
                return;
            }

            double value1 = Double.parseDouble(val1Str);
            double value2 = 0;

            if (etValue2.getVisibility() == View.VISIBLE) {
                String val2Str = etValue2.getText().toString().trim();
                if (val2Str.isEmpty()) {
                    etValue2.setError("Required");
                    return;
                }
                value2 = Double.parseDouble(val2Str);
            }

            VitalRecord record = new VitalRecord(type, value1, value2, finalUnit);
            record.setNotes(etNotes.getText().toString().trim());

            vitalRecords.add(record);
            saveRecords();
            adapter.notifyDataSetChanged();
            updateVitalCards();

            Toast.makeText(this, "Vital recorded", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        view.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    // Inner adapter class for records list
    private class VitalRecordsAdapter extends RecyclerView.Adapter<VitalRecordsAdapter.ViewHolder> {
        private List<VitalRecord> records;
        private SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());

        VitalRecordsAdapter(List<VitalRecord> records) {
            this.records = records;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_vital_record, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            // Show from newest to oldest
            VitalRecord record = records.get(records.size() - 1 - position);
            
            String typeName = getTypeName(record.getType());
            holder.tvType.setText(typeName);
            holder.tvValue.setText(record.getDisplayValue());
            holder.tvTime.setText(sdf.format(new Date(record.getTimestamp())));
            holder.tvStatus.setText(record.getStatus());
            holder.tvStatus.setTextColor(record.getStatusColor());
        }

        private String getTypeName(String type) {
            switch (type) {
                case VitalRecord.TYPE_BLOOD_PRESSURE: return "Blood Pressure";
                case VitalRecord.TYPE_HEART_RATE: return "Heart Rate";
                case VitalRecord.TYPE_TEMPERATURE: return "Temperature";
                case VitalRecord.TYPE_SPO2: return "SpO2";
                case VitalRecord.TYPE_GLUCOSE: return "Blood Glucose";
                case VitalRecord.TYPE_WEIGHT: return "Weight";
                default: return type;
            }
        }

        @Override
        public int getItemCount() {
            return Math.min(records.size(), 10); // Show last 10 records
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvType, tvValue, tvTime, tvStatus;

            ViewHolder(View itemView) {
                super(itemView);
                tvType = itemView.findViewById(R.id.tvVitalType);
                tvValue = itemView.findViewById(R.id.tvVitalValue);
                tvTime = itemView.findViewById(R.id.tvVitalTime);
                tvStatus = itemView.findViewById(R.id.tvVitalStatus);
            }
        }
    }
}
