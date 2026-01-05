package com.example.medicam.models;

import java.util.UUID;

public class Appointment {
    private String id;
    private String doctorName;
    private String specialty;
    private String hospitalName;
    private String address;
    private long appointmentDate; // Timestamp
    private String appointmentTime;
    private String notes;
    private String status; // "scheduled", "completed", "cancelled"
    private boolean reminderEnabled;
    private int reminderMinutesBefore; // 30, 60, 120, 1440 (1 day)
    
    public Appointment() {
        this.id = UUID.randomUUID().toString();
        this.status = "scheduled";
        this.reminderEnabled = true;
        this.reminderMinutesBefore = 60;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    
    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    
    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public long getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(long appointmentDate) { this.appointmentDate = appointmentDate; }
    
    public String getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(String appointmentTime) { this.appointmentTime = appointmentTime; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public boolean isReminderEnabled() { return reminderEnabled; }
    public void setReminderEnabled(boolean reminderEnabled) { this.reminderEnabled = reminderEnabled; }
    
    public int getReminderMinutesBefore() { return reminderMinutesBefore; }
    public void setReminderMinutesBefore(int reminderMinutesBefore) { this.reminderMinutesBefore = reminderMinutesBefore; }
    
    public String getFormattedReminder() {
        if (reminderMinutesBefore == 30) return "30 minutes before";
        if (reminderMinutesBefore == 60) return "1 hour before";
        if (reminderMinutesBefore == 120) return "2 hours before";
        if (reminderMinutesBefore == 1440) return "1 day before";
        return reminderMinutesBefore + " minutes before";
    }
}
