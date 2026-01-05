package com.example.medicam.models;

public class MealItem {
    private String id;
    private String name;
    private String mealType; // breakfast, lunch, dinner, snack
    private int calories;
    private float protein; // grams
    private float carbs; // grams
    private float fat; // grams
    private float fiber; // grams
    private String servingSize;
    private long timestamp;
    private String date; // YYYY-MM-DD format

    public MealItem() {
        this.id = java.util.UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
    }

    public MealItem(String name, String mealType, int calories, float protein, float carbs, float fat, float fiber, String servingSize, String date) {
        this.id = java.util.UUID.randomUUID().toString();
        this.name = name;
        this.mealType = mealType;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
        this.fiber = fiber;
        this.servingSize = servingSize;
        this.timestamp = System.currentTimeMillis();
        this.date = date;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType; }

    public int getCalories() { return calories; }
    public void setCalories(int calories) { this.calories = calories; }

    public float getProtein() { return protein; }
    public void setProtein(float protein) { this.protein = protein; }

    public float getCarbs() { return carbs; }
    public void setCarbs(float carbs) { this.carbs = carbs; }

    public float getFat() { return fat; }
    public void setFat(float fat) { this.fat = fat; }

    public float getFiber() { return fiber; }
    public void setFiber(float fiber) { this.fiber = fiber; }

    public String getServingSize() { return servingSize; }
    public void setServingSize(String servingSize) { this.servingSize = servingSize; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
