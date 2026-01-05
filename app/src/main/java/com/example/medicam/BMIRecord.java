package com.example.medicam;

public class BMIRecord {
    private double weight;
    private double height;
    private double bmiValue;
    private String category;
    private String date;
    private String gender;

    // No-argument constructor for Gson deserialization
    public BMIRecord() {
    }

    public BMIRecord(double weight, double height, double bmiValue, String category, String date, String gender) {
        this.weight = weight;
        this.height = height;
        this.bmiValue = bmiValue;
        this.category = category;
        this.date = date;
        this.gender = gender;
    }

    // Getters and Setters
    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getBmiValue() {
        return bmiValue;
    }

    public void setBmiValue(double bmiValue) {
        this.bmiValue = bmiValue;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
