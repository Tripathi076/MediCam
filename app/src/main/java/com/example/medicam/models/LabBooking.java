package com.example.medicam.models;

import java.util.UUID;

public class LabBooking {
    private String id;
    private String testId;
    private String testName;
    private String patientName;
    private String labName;
    private long bookingDate; // Timestamp
    private String timeSlot; // e.g., "9:00 AM - 10:00 AM"
    private String status; // "pending", "confirmed", "completed", "cancelled"
    private boolean homeCollection;
    private String address;
    private double totalAmount;
    private String paymentStatus; // "pending", "paid"
    private String reportUrl; // URL or local path to report PDF
    
    public LabBooking() {
        this.id = UUID.randomUUID().toString();
        this.status = "pending";
        this.paymentStatus = "pending";
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTestId() { return testId; }
    public void setTestId(String testId) { this.testId = testId; }
    
    public String getTestName() { return testName; }
    public void setTestName(String testName) { this.testName = testName; }
    
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    
    public String getLabName() { return labName; }
    public void setLabName(String labName) { this.labName = labName; }
    
    public long getBookingDate() { return bookingDate; }
    public void setBookingDate(long bookingDate) { this.bookingDate = bookingDate; }
    
    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public boolean isHomeCollection() { return homeCollection; }
    public void setHomeCollection(boolean homeCollection) { this.homeCollection = homeCollection; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    
    public String getReportUrl() { return reportUrl; }
    public void setReportUrl(String reportUrl) { this.reportUrl = reportUrl; }
    
    public String getStatusDisplayText() {
        switch (status) {
            case "pending": return "Pending Confirmation";
            case "confirmed": return "Confirmed";
            case "completed": return "Completed";
            case "cancelled": return "Cancelled";
            default: return status;
        }
    }
}
