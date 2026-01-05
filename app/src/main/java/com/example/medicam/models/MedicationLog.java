package com.example.medicam.models;

import java.io.Serializable;
import java.util.UUID;

public class MedicationLog implements Serializable {
    private String id;
    private String medicationId;
    private String medicationName;
    private long scheduledTime;
    private long takenTime;
    private String status; // TAKEN, SKIPPED, MISSED, PENDING
    private String notes;

    public static final String STATUS_TAKEN = "TAKEN";
    public static final String STATUS_SKIPPED = "SKIPPED";
    public static final String STATUS_MISSED = "MISSED";
    public static final String STATUS_PENDING = "PENDING";

    public MedicationLog() {
        this.id = UUID.randomUUID().toString();
        this.status = STATUS_PENDING;
    }

    public MedicationLog(String medicationId, String medicationName, long scheduledTime) {
        this();
        this.medicationId = medicationId;
        this.medicationName = medicationName;
        this.scheduledTime = scheduledTime;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getMedicationId() { return medicationId; }
    public void setMedicationId(String medicationId) { this.medicationId = medicationId; }

    public String getMedicationName() { return medicationName; }
    public void setMedicationName(String medicationName) { this.medicationName = medicationName; }

    public long getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(long scheduledTime) { this.scheduledTime = scheduledTime; }

    public long getTakenTime() { return takenTime; }
    public void setTakenTime(long takenTime) { this.takenTime = takenTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public void markAsTaken() {
        this.status = STATUS_TAKEN;
        this.takenTime = System.currentTimeMillis();
    }

    public void markAsSkipped(String reason) {
        this.status = STATUS_SKIPPED;
        this.notes = reason;
    }
}
