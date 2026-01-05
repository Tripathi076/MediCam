package com.example.medicam.models;

import java.io.Serializable;

public class UserProfile implements Serializable {
    private String id;
    private String fullName;
    private String dateOfBirth;
    private String gender;
    private String email;
    private String phone;
    private String bloodGroup;
    private float height; // in cm
    private float weight; // in kg
    private String aboutMe;
    private String profileImageUri;
    private String address;
    private String city;
    private String state;
    private String pincode;

    public UserProfile() {
        // Default constructor
    }

    public UserProfile(String fullName, String email) {
        this.fullName = fullName;
        this.email = email;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public String getProfileImageUri() {
        return profileImageUri;
    }

    public void setProfileImageUri(String profileImageUri) {
        this.profileImageUri = profileImageUri;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    // Calculate age from date of birth
    public int getAge() {
        if (dateOfBirth == null || dateOfBirth.isEmpty()) {
            return 0;
        }
        try {
            String[] parts = dateOfBirth.split("/");
            if (parts.length == 3) {
                int birthYear = Integer.parseInt(parts[2]);
                int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
                return currentYear - birthYear;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Calculate BMI
    public float getBMI() {
        if (height > 0 && weight > 0) {
            float heightInMeters = height / 100;
            return weight / (heightInMeters * heightInMeters);
        }
        return 0;
    }
}
