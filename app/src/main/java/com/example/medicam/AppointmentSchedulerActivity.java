package com.example.medicam;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicam.adapters.AppointmentAdapter;
import com.example.medicam.models.Appointment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class AppointmentSchedulerActivity extends AppCompatActivity implements AppointmentAdapter.OnAppointmentClickListener {
    
    private static final String PREFS_NAME = "appointments_prefs";
    private static final String KEY_APPOINTMENTS = "appointments";
    
    private RecyclerView recyclerView;
    private AppointmentAdapter adapter;
    private List<Appointment> appointments = new ArrayList<>();
    private List<Appointment> filteredAppointments = new ArrayList<>();
    private TextView tvEmpty;
    private TabLayout tabLayout;
    
    private SharedPreferences prefs;
    private Gson gson = new Gson();
    
    private String currentFilter = "upcoming"; // upcoming, past, all
    
    private final String[] specialties = {
        "General Physician", "Cardiologist", "Dermatologist", "ENT Specialist",
        "Gastroenterologist", "Gynecologist", "Neurologist", "Ophthalmologist",
        "Orthopedic", "Pediatrician", "Psychiatrist", "Pulmonologist", "Urologist"
    };
    
    private final String[] reminderOptions = {
        "30 minutes before", "1 hour before", "2 hours before", "1 day before"
    };
    private final int[] reminderMinutes = { 30, 60, 120, 1440 };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_scheduler);
        
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        
        initializeViews();
        loadAppointments();
        setupRecyclerView();
        setupClickListeners();
    }
    
    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerAppointments);
        tvEmpty = findViewById(R.id.tvEmpty);
        tabLayout = findViewById(R.id.tabLayout);
        
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        
        // Setup tabs
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: currentFilter = "upcoming"; break;
                    case 1: currentFilter = "past"; break;
                    case 2: currentFilter = "all"; break;
                }
                filterAppointments();
            }
            
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }
    
    private void setupRecyclerView() {
        adapter = new AppointmentAdapter(filteredAppointments, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
    
    private void setupClickListeners() {
        FloatingActionButton fab = findViewById(R.id.fabAddAppointment);
        fab.setOnClickListener(v -> showAddAppointmentDialog(null));
    }
    
    private void loadAppointments() {
        String json = prefs.getString(KEY_APPOINTMENTS, "[]");
        Type type = new TypeToken<List<Appointment>>(){}.getType();
        appointments = gson.fromJson(json, type);
        if (appointments == null) appointments = new ArrayList<>();
        filterAppointments();
    }
    
    private void saveAppointments() {
        String json = gson.toJson(appointments);
        prefs.edit().putString(KEY_APPOINTMENTS, json).apply();
    }
    
    private void filterAppointments() {
        long now = System.currentTimeMillis();
        
        switch (currentFilter) {
            case "upcoming":
                filteredAppointments = appointments.stream()
                    .filter(a -> a.getAppointmentDate() >= now && !a.getStatus().equals("cancelled"))
                    .sorted((a, b) -> Long.compare(a.getAppointmentDate(), b.getAppointmentDate()))
                    .collect(Collectors.toList());
                break;
            case "past":
                filteredAppointments = appointments.stream()
                    .filter(a -> a.getAppointmentDate() < now || a.getStatus().equals("completed"))
                    .sorted((a, b) -> Long.compare(b.getAppointmentDate(), a.getAppointmentDate()))
                    .collect(Collectors.toList());
                break;
            default:
                filteredAppointments = new ArrayList<>(appointments);
                filteredAppointments.sort((a, b) -> Long.compare(b.getAppointmentDate(), a.getAppointmentDate()));
        }
        
        adapter.updateAppointments(filteredAppointments);
        tvEmpty.setVisibility(filteredAppointments.isEmpty() ? View.VISIBLE : View.GONE);
    }
    
    private void showAddAppointmentDialog(Appointment existingAppointment) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_appointment, null);
        
        EditText etDoctorName = dialogView.findViewById(R.id.etDoctorName);
        Spinner spinnerSpecialty = dialogView.findViewById(R.id.spinnerSpecialty);
        EditText etHospital = dialogView.findViewById(R.id.etHospital);
        EditText etAddress = dialogView.findViewById(R.id.etAddress);
        TextView tvSelectedDate = dialogView.findViewById(R.id.tvSelectedDate);
        TextView tvSelectedTime = dialogView.findViewById(R.id.tvSelectedTime);
        MaterialButton btnSelectDate = dialogView.findViewById(R.id.btnSelectDate);
        MaterialButton btnSelectTime = dialogView.findViewById(R.id.btnSelectTime);
        EditText etNotes = dialogView.findViewById(R.id.etNotes);
        CheckBox cbReminder = dialogView.findViewById(R.id.cbReminder);
        Spinner spinnerReminder = dialogView.findViewById(R.id.spinnerReminder);
        
        // Setup spinners
        ArrayAdapter<String> specialtyAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_dropdown_item, specialties);
        spinnerSpecialty.setAdapter(specialtyAdapter);
        
        ArrayAdapter<String> reminderAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_dropdown_item, reminderOptions);
        spinnerReminder.setAdapter(reminderAdapter);
        
        final Calendar selectedCalendar = Calendar.getInstance();
        final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault());
        final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        
        // Pre-fill if editing
        if (existingAppointment != null) {
            etDoctorName.setText(existingAppointment.getDoctorName());
            etHospital.setText(existingAppointment.getHospitalName());
            etAddress.setText(existingAppointment.getAddress());
            etNotes.setText(existingAppointment.getNotes());
            cbReminder.setChecked(existingAppointment.isReminderEnabled());
            
            selectedCalendar.setTimeInMillis(existingAppointment.getAppointmentDate());
            tvSelectedDate.setText(dateFormat.format(selectedCalendar.getTime()));
            tvSelectedTime.setText(existingAppointment.getAppointmentTime());
            
            // Find specialty position
            for (int i = 0; i < specialties.length; i++) {
                if (specialties[i].equals(existingAppointment.getSpecialty())) {
                    spinnerSpecialty.setSelection(i);
                    break;
                }
            }
            
            // Find reminder position
            for (int i = 0; i < reminderMinutes.length; i++) {
                if (reminderMinutes[i] == existingAppointment.getReminderMinutesBefore()) {
                    spinnerReminder.setSelection(i);
                    break;
                }
            }
        }
        
        // Date picker
        btnSelectDate.setOnClickListener(v -> {
            DatePickerDialog picker = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    selectedCalendar.set(year, month, dayOfMonth);
                    tvSelectedDate.setText(dateFormat.format(selectedCalendar.getTime()));
                },
                selectedCalendar.get(Calendar.YEAR),
                selectedCalendar.get(Calendar.MONTH),
                selectedCalendar.get(Calendar.DAY_OF_MONTH));
            picker.getDatePicker().setMinDate(System.currentTimeMillis());
            picker.show();
        });
        
        // Time picker
        btnSelectTime.setOnClickListener(v -> {
            new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedCalendar.set(Calendar.MINUTE, minute);
                    tvSelectedTime.setText(timeFormat.format(selectedCalendar.getTime()));
                },
                selectedCalendar.get(Calendar.HOUR_OF_DAY),
                selectedCalendar.get(Calendar.MINUTE),
                false).show();
        });
        
        AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle(existingAppointment == null ? "Schedule Appointment" : "Edit Appointment")
            .setView(dialogView)
            .setPositiveButton("Save", null)
            .setNegativeButton("Cancel", null)
            .create();
        
        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String doctorName = etDoctorName.getText().toString().trim();
                String hospital = etHospital.getText().toString().trim();
                String dateStr = tvSelectedDate.getText().toString();
                String timeStr = tvSelectedTime.getText().toString();
                
                if (doctorName.isEmpty() || hospital.isEmpty() || 
                    dateStr.equals("Select Date") || timeStr.equals("Select Time")) {
                    Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                Appointment appointment = existingAppointment != null ? existingAppointment : new Appointment();
                appointment.setDoctorName(doctorName);
                appointment.setSpecialty(specialties[spinnerSpecialty.getSelectedItemPosition()]);
                appointment.setHospitalName(hospital);
                appointment.setAddress(etAddress.getText().toString().trim());
                appointment.setAppointmentDate(selectedCalendar.getTimeInMillis());
                appointment.setAppointmentTime(timeStr);
                appointment.setNotes(etNotes.getText().toString().trim());
                appointment.setReminderEnabled(cbReminder.isChecked());
                appointment.setReminderMinutesBefore(reminderMinutes[spinnerReminder.getSelectedItemPosition()]);
                
                if (existingAppointment == null) {
                    appointments.add(appointment);
                }
                
                saveAppointments();
                filterAppointments();
                
                if (appointment.isReminderEnabled()) {
                    scheduleReminder(appointment);
                }
                
                Toast.makeText(this, "Appointment saved", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
        });
        
        dialog.show();
    }
    
    private void scheduleReminder(Appointment appointment) {
        long reminderTime = appointment.getAppointmentDate() - 
            (appointment.getReminderMinutesBefore() * 60 * 1000L);
        
        if (reminderTime > System.currentTimeMillis()) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, AppointmentReminderReceiver.class);
            intent.putExtra("doctor_name", appointment.getDoctorName());
            intent.putExtra("time", appointment.getAppointmentTime());
            intent.putExtra("hospital", appointment.getHospitalName());
            
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                appointment.getId().hashCode(), intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            
            if (alarmManager != null) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, 
                    reminderTime, pendingIntent);
            }
        }
    }
    
    @Override
    public void onEdit(Appointment appointment) {
        showAddAppointmentDialog(appointment);
    }
    
    @Override
    public void onDelete(Appointment appointment) {
        new AlertDialog.Builder(this)
            .setTitle("Delete Appointment")
            .setMessage("Are you sure you want to delete this appointment?")
            .setPositiveButton("Delete", (d, w) -> {
                appointments.remove(appointment);
                saveAppointments();
                filterAppointments();
                cancelReminder(appointment);
                Toast.makeText(this, "Appointment deleted", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
    
    @Override
    public void onStatusChange(Appointment appointment, String newStatus) {
        appointment.setStatus(newStatus);
        saveAppointments();
        filterAppointments();
        Toast.makeText(this, "Appointment marked as " + newStatus, Toast.LENGTH_SHORT).show();
    }
    
    private void cancelReminder(Appointment appointment) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AppointmentReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
            appointment.getId().hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}
