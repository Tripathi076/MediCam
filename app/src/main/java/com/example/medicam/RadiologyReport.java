package com.example.medicam;

public class RadiologyReport {
    private String centerName;
    private String scanType;
    private String scanDate;
    private String doctorName;
    private String patientName;
    private String reportImageUri;
    private String category; // "CT Scan", "MRI Scan", "X-Ray", "Ultrasound", "Others"

    public RadiologyReport() {
    }

    public RadiologyReport(String centerName, String scanType, String scanDate,
                          String doctorName, String patientName, String reportImageUri, String category) {
        this.centerName = centerName;
        this.scanType = scanType;
        this.scanDate = scanDate;
        this.doctorName = doctorName;
        this.patientName = patientName;
        this.reportImageUri = reportImageUri;
        this.category = category;
    }

    public String getCenterName() {
        return centerName;
    }

    public void setCenterName(String centerName) {
        this.centerName = centerName;
    }

    public String getScanType() {
        return scanType;
    }

    public void setScanType(String scanType) {
        this.scanType = scanType;
    }

    public String getScanDate() {
        return scanDate;
    }

    public void setScanDate(String scanDate) {
        this.scanDate = scanDate;
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
