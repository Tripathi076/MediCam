package com.example.medicam;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicam.adapters.MedicationAdapter;
import com.example.medicam.models.Medication;
import com.example.medicam.models.MedicationLog;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MedicationReminderActivity extends AppCompatActivity implements MedicationAdapter.OnMedicationClickListener {

    private RecyclerView rvMedications;
    private LinearLayout emptyState;
    private TextView tvTotalMeds, tvTakenCount, tvPendingCount;
    private FloatingActionButton fabAddMedication;

    private MedicationAdapter adapter;
    private List<Medication> medications;
    private List<MedicationLog> todayLogs;

    private SharedPreferences sharedPreferences;
    private Gson gson;

    private static final String PREF_NAME = "MedicationPrefs";
    private static final String KEY_MEDICATIONS = "medications";
    private static final String KEY_LOGS = "medication_logs";

    private final String[] UNITS = {"mg", "ml", "tablets", "capsules", "drops", "units", "puffs"};
    private final String[] FREQUENCIES = {"Once daily", "Twice daily", "Three times daily", "Four times daily", "Every 8 hours", "Every 12 hours", "Weekly", "As needed"};
    private final String[] INSTRUCTIONS = {"Before meal", "After meal", "With meal", "Before bed", "On empty stomach", "With water", "As directed"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication_reminder);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        gson = new Gson();

        initViews();
        setupRecyclerView();
        loadMedications();
        updateStats();
    }

    private void initViews() {
        rvMedications = findViewById(R.id.rvMedications);
        emptyState = findViewById(R.id.emptyState);
        tvTotalMeds = findViewById(R.id.tvTotalMeds);
        tvTakenCount = findViewById(R.id.tvTakenCount);
        tvPendingCount = findViewById(R.id.tvPendingCount);
        fabAddMedication = findViewById(R.id.fabAddMedication);

        ImageView btnBack = findViewById(R.id.btnBack);
        ImageView btnHistory = findViewById(R.id.btnHistory);

        btnBack.setOnClickListener(v -> finish());
        btnHistory.setOnClickListener(v -> showHistoryDialog());
        fabAddMedication.setOnClickListener(v -> showAddMedicationDialog(null));
    }

    private void setupRecyclerView() {
        medications = new ArrayList<>();
        adapter = new MedicationAdapter(this, medications, this);
        rvMedications.setLayoutManager(new LinearLayoutManager(this));
        rvMedications.setAdapter(adapter);
    }

    private void loadMedications() {
        String json = sharedPreferences.getString(KEY_MEDICATIONS, null);
        if (json != null) {
            Type type = new TypeToken<List<Medication>>(){}.getType();
            medications = gson.fromJson(json, type);
            if (medications == null) medications = new ArrayList<>();
        } else {
            medications = new ArrayList<>();
        }
        
        adapter.updateMedications(medications);
        updateEmptyState();
    }

    private void saveMedications() {
        String json = gson.toJson(medications);
        sharedPreferences.edit().putString(KEY_MEDICATIONS, json).apply();
    }

    private void loadTodayLogs() {
        String json = sharedPreferences.getString(KEY_LOGS, null);
        if (json != null) {
            Type type = new TypeToken<List<MedicationLog>>(){}.getType();
            todayLogs = gson.fromJson(json, type);
            if (todayLogs == null) todayLogs = new ArrayList<>();
        } else {
            todayLogs = new ArrayList<>();
        }
    }

    private void saveLogs() {
        String json = gson.toJson(todayLogs);
        sharedPreferences.edit().putString(KEY_LOGS, json).apply();
    }

    private void updateEmptyState() {
        if (medications.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            rvMedications.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            rvMedications.setVisibility(View.VISIBLE);
        }
    }

    private void updateStats() {
        loadTodayLogs();
        
        int total = medications.size();
        int taken = 0;
        int pending = 0;

        // Count today's taken medications
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        for (MedicationLog log : todayLogs) {
            String logDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(log.getScheduledTime()));
            if (logDate.equals(today)) {
                if (MedicationLog.STATUS_TAKEN.equals(log.getStatus())) {
                    taken++;
                } else if (MedicationLog.STATUS_PENDING.equals(log.getStatus())) {
                    pending++;
                }
            }
        }

        // Calculate pending based on reminder times
        for (Medication med : medications) {
            if (med.isActive() && med.getReminderTimes() != null) {
                pending += med.getReminderTimes().size();
            }
        }
        pending -= taken;
        if (pending < 0) pending = 0;

        tvTotalMeds.setText(String.valueOf(total));
        tvTakenCount.setText(String.valueOf(taken));
        tvPendingCount.setText(String.valueOf(pending));
    }

    private void showAddMedicationDialog(Medication editMedication) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_medication, null);
        dialog.setContentView(view);

        EditText etName = view.findViewById(R.id.etMedicationName);
        EditText etDosage = view.findViewById(R.id.etDosage);
        AutoCompleteTextView actvUnit = view.findViewById(R.id.actvUnit);
        AutoCompleteTextView actvFrequency = view.findViewById(R.id.actvFrequency);
        AutoCompleteTextView actvInstructions = view.findViewById(R.id.actvInstructions);
        ChipGroup chipGroupTimes = view.findViewById(R.id.chipGroupTimes);
        Chip chipAddTime = view.findViewById(R.id.chipAddTime);
        EditText etCurrentStock = view.findViewById(R.id.etCurrentStock);
        EditText etRemindAt = view.findViewById(R.id.etRemindAt);
        EditText etNotes = view.findViewById(R.id.etNotes);

        // Setup dropdowns
        actvUnit.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, UNITS));
        actvFrequency.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, FREQUENCIES));
        actvInstructions.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, INSTRUCTIONS));

        List<String> selectedTimes = new ArrayList<>();

        // Add time chip click
        chipAddTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (timePicker, hourOfDay, minute) -> {
                String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                selectedTimes.add(time);
                
                Chip chip = new Chip(this);
                chip.setText(time);
                chip.setCloseIconVisible(true);
                chip.setOnCloseIconClickListener(cv -> {
                    selectedTimes.remove(time);
                    chipGroupTimes.removeView(chip);
                });
                chipGroupTimes.addView(chip, chipGroupTimes.getChildCount() - 1);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        });

        // Pre-fill if editing
        if (editMedication != null) {
            etName.setText(editMedication.getName());
            etDosage.setText(editMedication.getDosage());
            actvUnit.setText(editMedication.getUnit(), false);
            actvFrequency.setText(editMedication.getFrequency(), false);
            actvInstructions.setText(editMedication.getInstructions(), false);
            etCurrentStock.setText(String.valueOf(editMedication.getPillsRemaining()));
            etRemindAt.setText(String.valueOf(editMedication.getRefillReminderAt()));
            etNotes.setText(editMedication.getNotes());

            if (editMedication.getReminderTimes() != null) {
                for (String time : editMedication.getReminderTimes()) {
                    selectedTimes.add(time);
                    Chip chip = new Chip(this);
                    chip.setText(time);
                    chip.setCloseIconVisible(true);
                    chip.setOnCloseIconClickListener(cv -> {
                        selectedTimes.remove(time);
                        chipGroupTimes.removeView(chip);
                    });
                    chipGroupTimes.addView(chip, chipGroupTimes.getChildCount() - 1);
                }
            }
        }

        view.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());
        view.findViewById(R.id.btnSave).setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String dosage = etDosage.getText().toString().trim();
            String unit = actvUnit.getText().toString().trim();
            String frequency = actvFrequency.getText().toString().trim();

            if (name.isEmpty()) {
                etName.setError("Required");
                return;
            }

            Medication medication = editMedication != null ? editMedication : new Medication();
            medication.setName(name);
            medication.setDosage(dosage);
            medication.setUnit(unit);
            medication.setFrequency(frequency);
            medication.setInstructions(actvInstructions.getText().toString().trim());
            medication.setReminderTimes(selectedTimes);
            medication.setNotes(etNotes.getText().toString().trim());

            try {
                medication.setPillsRemaining(Integer.parseInt(etCurrentStock.getText().toString().trim()));
            } catch (NumberFormatException e) {
                medication.setPillsRemaining(0);
            }

            try {
                medication.setRefillReminderAt(Integer.parseInt(etRemindAt.getText().toString().trim()));
            } catch (NumberFormatException e) {
                medication.setRefillReminderAt(5);
            }

            // Assign a color
            int[] colors = {0xFF4CAF50, 0xFF2196F3, 0xFFFF9800, 0xFF9C27B0, 0xFFE91E63};
            medication.setColor(colors[medications.size() % colors.length]);

            if (editMedication == null) {
                medications.add(medication);
            }

            saveMedications();
            adapter.updateMedications(medications);
            updateEmptyState();
            updateStats();
            scheduleReminders(medication);

            Toast.makeText(this, "Medication saved", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void scheduleReminders(Medication medication) {
        // Schedule alarms for each reminder time
        if (medication.getReminderTimes() == null) return;

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        
        for (int i = 0; i < medication.getReminderTimes().size(); i++) {
            String time = medication.getReminderTimes().get(i);
            String[] parts = time.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);

            if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }

            Intent intent = new Intent(this, MedicationReminderReceiver.class);
            intent.putExtra("medication_name", medication.getName());
            intent.putExtra("medication_dosage", medication.getDosageDisplay());
            intent.putExtra("medication_id", medication.getId());

            int requestCode = (medication.getId() + i).hashCode();
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, 
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            if (alarmManager != null) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pendingIntent);
            }
        }
    }

    @Override
    public void onMedicationClick(Medication medication) {
        // Show medication details
        showMedicationDetails(medication);
    }

    @Override
    public void onTakeMedication(Medication medication) {
        // Log medication taken
        MedicationLog log = new MedicationLog(medication.getId(), medication.getName(), System.currentTimeMillis());
        log.markAsTaken();
        
        loadTodayLogs();
        todayLogs.add(log);
        saveLogs();

        // Decrease pill count
        if (medication.getPillsRemaining() > 0) {
            medication.setPillsRemaining(medication.getPillsRemaining() - 1);
            saveMedications();
            adapter.notifyDataSetChanged();
        }

        updateStats();
        Toast.makeText(this, medication.getName() + " marked as taken", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEditMedication(Medication medication) {
        showAddMedicationDialog(medication);
    }

    private void showMedicationDetails(Medication medication) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(medication.getName());
        
        StringBuilder message = new StringBuilder();
        message.append("Dosage: ").append(medication.getDosageDisplay()).append("\n");
        message.append("Frequency: ").append(medication.getFrequency()).append("\n");
        
        if (medication.getInstructions() != null && !medication.getInstructions().isEmpty()) {
            message.append("Instructions: ").append(medication.getInstructions()).append("\n");
        }
        
        if (medication.getReminderTimes() != null && !medication.getReminderTimes().isEmpty()) {
            message.append("Reminders: ").append(String.join(", ", medication.getReminderTimes())).append("\n");
        }
        
        if (medication.getPillsRemaining() > 0) {
            message.append("Stock: ").append(medication.getPillsRemaining()).append(" remaining\n");
        }
        
        if (medication.getNotes() != null && !medication.getNotes().isEmpty()) {
            message.append("Notes: ").append(medication.getNotes());
        }

        builder.setMessage(message.toString());
        builder.setPositiveButton("OK", null);
        builder.setNegativeButton("Delete", (dialog, which) -> {
            medications.remove(medication);
            saveMedications();
            adapter.updateMedications(medications);
            updateEmptyState();
            updateStats();
            Toast.makeText(this, "Medication deleted", Toast.LENGTH_SHORT).show();
        });
        builder.show();
    }

    private void showHistoryDialog() {
        loadTodayLogs();
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Medication History");
        
        if (todayLogs.isEmpty()) {
            builder.setMessage("No medication history found.");
        } else {
            StringBuilder history = new StringBuilder();
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
            
            for (int i = todayLogs.size() - 1; i >= 0 && i > todayLogs.size() - 20; i--) {
                MedicationLog log = todayLogs.get(i);
                history.append("• ").append(log.getMedicationName());
                history.append(" - ").append(log.getStatus());
                history.append(" (").append(sdf.format(new Date(log.getTakenTime()))).append(")\n");
            }
            builder.setMessage(history.toString());
        }
        
        builder.setPositiveButton("OK", null);
        builder.setNeutralButton("Clear History", (dialog, which) -> {
            todayLogs.clear();
            saveLogs();
            Toast.makeText(this, "History cleared", Toast.LENGTH_SHORT).show();
        });
        builder.show();
    }
}
