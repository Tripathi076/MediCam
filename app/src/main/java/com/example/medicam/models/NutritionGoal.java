package com.example.medicam.models;

public class NutritionGoal {
    private int dailyCalories;
    private float dailyProtein; // grams
    private float dailyCarbs; // grams
    private float dailyFat; // grams
    private float dailyFiber; // grams
    private String goalType; // lose_weight, maintain, gain_weight

    public NutritionGoal() {
        // Default values based on 2000 calorie diet
        this.dailyCalories = 2000;
        this.dailyProtein = 50;
        this.dailyCarbs = 250;
        this.dailyFat = 65;
        this.dailyFiber = 25;
        this.goalType = "maintain";
    }

    public NutritionGoal(int dailyCalories, float dailyProtein, float dailyCarbs, float dailyFat, float dailyFiber, String goalType) {
        this.dailyCalories = dailyCalories;
        this.dailyProtein = dailyProtein;
        this.dailyCarbs = dailyCarbs;
        this.dailyFat = dailyFat;
        this.dailyFiber = dailyFiber;
        this.goalType = goalType;
    }

    // Getters and Setters
    public int getDailyCalories() { return dailyCalories; }
    public void setDailyCalories(int dailyCalories) { this.dailyCalories = dailyCalories; }

    public float getDailyProtein() { return dailyProtein; }
    public void setDailyProtein(float dailyProtein) { this.dailyProtein = dailyProtein; }

    public float getDailyCarbs() { return dailyCarbs; }
    public void setDailyCarbs(float dailyCarbs) { this.dailyCarbs = dailyCarbs; }

    public float getDailyFat() { return dailyFat; }
    public void setDailyFat(float dailyFat) { this.dailyFat = dailyFat; }

    public float getDailyFiber() { return dailyFiber; }
    public void setDailyFiber(float dailyFiber) { this.dailyFiber = dailyFiber; }

    public String getGoalType() { return goalType; }
    public void setGoalType(String goalType) { this.goalType = goalType; }
}
