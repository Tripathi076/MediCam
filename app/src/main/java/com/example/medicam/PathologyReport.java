package com.example.medicam;

public class PathologyReport {
    private String labName;
    private String testName;
    private String testType;
    private String collectionDate;
    private String reportDate;
    private String doctorName;
    private String patientName;
    private String reportImageUri;
    private String category; // "Pathology", "Radiology"

    public PathologyReport() {
    }

    public PathologyReport(String labName, String testName, String collectionDate, 
                          String doctorName, String patientName, String reportImageUri, String category) {
        this.labName = labName;
        this.testName = testName;
        this.collectionDate = collectionDate;
        this.doctorName = doctorName;
        this.patientName = patientName;
        this.reportImageUri = reportImageUri;
        this.category = category;
    }

    public String getLabName() {
        return labName;
    }

    public void setLabName(String labName) {
        this.labName = labName;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public String getCollectionDate() {
        return collectionDate;
    }

    public void setCollectionDate(String collectionDate) {
        this.collectionDate = collectionDate;
    }

    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getReportImageUri() {
        return reportImageUri;
    }

    public void setReportImageUri(String reportImageUri) {
        this.reportImageUri = reportImageUri;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
