package com.example.medicam.models;

import java.util.UUID;

public class LabTest {
    private String id;
    private String testName;
    private String testCode;
    private String category; // Blood, Urine, Imaging, etc.
    private String description;
    private double price;
    private String preparationInstructions;
    private int reportTimeHours; // Typical time for results
    
    public LabTest() {
        this.id = UUID.randomUUID().toString();
    }
    
    public LabTest(String testName, String testCode, String category, String description, 
                   double price, String preparationInstructions, int reportTimeHours) {
        this();
        this.testName = testName;
        this.testCode = testCode;
        this.category = category;
        this.description = description;
        this.price = price;
        this.preparationInstructions = preparationInstructions;
        this.reportTimeHours = reportTimeHours;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTestName() { return testName; }
    public void setTestName(String testName) { this.testName = testName; }
    
    public String getTestCode() { return testCode; }
    public void setTestCode(String testCode) { this.testCode = testCode; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    
    public String getPreparationInstructions() { return preparationInstructions; }
    public void setPreparationInstructions(String prep) { this.preparationInstructions = prep; }
    
    public int getReportTimeHours() { return reportTimeHours; }
    public void setReportTimeHours(int reportTimeHours) { this.reportTimeHours = reportTimeHours; }
    
    public String getFormattedReportTime() {
        if (reportTimeHours < 24) {
            return reportTimeHours + " hours";
        } else {
            int days = reportTimeHours / 24;
            return days + " day" + (days > 1 ? "s" : "");
        }
    }
}
