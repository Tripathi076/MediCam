package com.example.medicam.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Medication implements Serializable {
    private String id;
    private String name;
    private String dosage;
    private String unit; // mg, ml, tablets, etc.
    private String frequency; // Daily, Weekly, As needed
    private List<String> reminderTimes; // List of times like "08:00", "20:00"
    private String instructions; // Before meal, After meal, etc.
    private int pillsRemaining;
    private int pillsPerRefill;
    private int refillReminderAt; // Remind when pills reach this count
    private long startDate;
    private long endDate; // 0 if ongoing
    private boolean isActive;
    private String notes;
    private int color; // For UI identification

    public Medication() {
        this.id = UUID.randomUUID().toString();
        this.reminderTimes = new ArrayList<>();
        this.isActive = true;
        this.startDate = System.currentTimeMillis();
    }

    public Medication(String name, String dosage, String unit) {
        this();
        this.name = name;
        this.dosage = dosage;
        this.unit = unit;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public List<String> getReminderTimes() { return reminderTimes; }
    public void setReminderTimes(List<String> reminderTimes) { this.reminderTimes = reminderTimes; }
    public void addReminderTime(String time) { this.reminderTimes.add(time); }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public int getPillsRemaining() { return pillsRemaining; }
    public void setPillsRemaining(int pillsRemaining) { this.pillsRemaining = pillsRemaining; }

    public int getPillsPerRefill() { return pillsPerRefill; }
    public void setPillsPerRefill(int pillsPerRefill) { this.pillsPerRefill = pillsPerRefill; }

    public int getRefillReminderAt() { return refillReminderAt; }
    public void setRefillReminderAt(int refillReminderAt) { this.refillReminderAt = refillReminderAt; }

    public long getStartDate() { return startDate; }
    public void setStartDate(long startDate) { this.startDate = startDate; }

    public long getEndDate() { return endDate; }
    public void setEndDate(long endDate) { this.endDate = endDate; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public int getColor() { return color; }
    public void setColor(int color) { this.color = color; }

    public String getDosageDisplay() {
        return dosage + " " + unit;
    }

    public boolean needsRefill() {
        return pillsRemaining > 0 && pillsRemaining <= refillReminderAt;
    }
}
