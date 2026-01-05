package com.example.medicam.models;

import java.io.Serializable;
import java.util.UUID;

public class VitalRecord implements Serializable {
    private String id;
    private String type; // BLOOD_PRESSURE, HEART_RATE, TEMPERATURE, WEIGHT, SPO2, GLUCOSE
    private double value1; // Primary value (systolic for BP)
    private double value2; // Secondary value (diastolic for BP)
    private String unit;
    private long timestamp;
    private String notes;

    public static final String TYPE_BLOOD_PRESSURE = "BLOOD_PRESSURE";
    public static final String TYPE_HEART_RATE = "HEART_RATE";
    public static final String TYPE_TEMPERATURE = "TEMPERATURE";
    public static final String TYPE_WEIGHT = "WEIGHT";
    public static final String TYPE_SPO2 = "SPO2";
    public static final String TYPE_GLUCOSE = "GLUCOSE";

    public VitalRecord() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
    }

    public VitalRecord(String type, double value1, String unit) {
        this();
        this.type = type;
        this.value1 = value1;
        this.unit = unit;
    }

    public VitalRecord(String type, double value1, double value2, String unit) {
        this(type, value1, unit);
        this.value2 = value2;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getValue1() { return value1; }
    public void setValue1(double value1) { this.value1 = value1; }

    public double getValue2() { return value2; }
    public void setValue2(double value2) { this.value2 = value2; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getDisplayValue() {
        if (TYPE_BLOOD_PRESSURE.equals(type)) {
            return (int) value1 + "/" + (int) value2 + " " + unit;
        }
        if (value1 == (int) value1) {
            return (int) value1 + " " + unit;
        }
        return String.format("%.1f %s", value1, unit);
    }

    public String getStatus() {
        switch (type) {
            case TYPE_BLOOD_PRESSURE:
                if (value1 < 90 || value2 < 60) return "Low";
                if (value1 > 140 || value2 > 90) return "High";
                if (value1 > 120 || value2 > 80) return "Elevated";
                return "Normal";
            case TYPE_HEART_RATE:
                if (value1 < 60) return "Low";
                if (value1 > 100) return "High";
                return "Normal";
            case TYPE_TEMPERATURE:
                if (value1 < 36.1) return "Low";
                if (value1 > 37.2) return "Fever";
                return "Normal";
            case TYPE_SPO2:
                if (value1 < 95) return "Low";
                return "Normal";
            case TYPE_GLUCOSE:
                if (value1 < 70) return "Low";
                if (value1 > 140) return "High";
                return "Normal";
            default:
                return "N/A";
        }
    }

    public int getStatusColor() {
        String status = getStatus();
        switch (status) {
            case "Low": return 0xFFFF9800;
            case "High":
            case "Fever": return 0xFFF44336;
            case "Elevated": return 0xFFFF9800;
            default: return 0xFF4CAF50;
        }
    }
}
