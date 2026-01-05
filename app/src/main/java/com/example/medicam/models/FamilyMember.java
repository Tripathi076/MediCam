package com.example.medicam.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FamilyMember {
    private String id;
    private String name;
    private String relationship; // Self, Spouse, Child, Parent, Sibling, Other
    private int age;
    private String gender; // Male, Female, Other
    private String bloodType;
    private String profileImageUri; // Local URI for profile photo
    private String dateOfBirth;
    private float height; // in cm
    private float weight; // in kg
    
    // Medical Information
    private List<String> allergies;
    private List<String> conditions; // Chronic conditions
    private List<String> medications;
    private String emergencyContact;
    private String emergencyPhone;
    
    // Health records
    private String lastCheckupDate;
    private String notes;
    
    public FamilyMember() {
        this.id = UUID.randomUUID().toString();
        this.allergies = new ArrayList<>();
        this.conditions = new ArrayList<>();
        this.medications = new ArrayList<>();
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getRelationship() { return relationship; }
    public void setRelationship(String relationship) { this.relationship = relationship; }
    
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    
    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }
    
    public String getProfileImageUri() { return profileImageUri; }
    public void setProfileImageUri(String profileImageUri) { this.profileImageUri = profileImageUri; }
    
    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    
    public float getHeight() { return height; }
    public void setHeight(float height) { this.height = height; }
    
    public float getWeight() { return weight; }
    public void setWeight(float weight) { this.weight = weight; }
    
    public float getBMI() {
        if (height > 0 && weight > 0) {
            float heightInMeters = height / 100f;
            return weight / (heightInMeters * heightInMeters);
        }
        return 0;
    }
    
    public List<String> getAllergies() { return allergies; }
    public void setAllergies(List<String> allergies) { this.allergies = allergies; }
    
    public List<String> getConditions() { return conditions; }
    public void setConditions(List<String> conditions) { this.conditions = conditions; }
    
    public List<String> getMedications() { return medications; }
    public void setMedications(List<String> medications) { this.medications = medications; }
    
    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }
    
    public String getEmergencyPhone() { return emergencyPhone; }
    public void setEmergencyPhone(String emergencyPhone) { this.emergencyPhone = emergencyPhone; }
    
    public String getLastCheckupDate() { return lastCheckupDate; }
    public void setLastCheckupDate(String lastCheckupDate) { this.lastCheckupDate = lastCheckupDate; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public String getAllergiesString() {
        if (allergies == null || allergies.isEmpty()) return "None";
        return String.join(", ", allergies);
    }
    
    public String getConditionsString() {
        if (conditions == null || conditions.isEmpty()) return "None";
        return String.join(", ", conditions);
    }
    
    public String getMedicationsString() {
        if (medications == null || medications.isEmpty()) return "None";
        return String.join(", ", medications);
    }
}
