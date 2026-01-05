package com.example.medicam.models;

public class FoodItem {
    private String name;
    private int caloriesPer100g;
    private float proteinPer100g;
    private float carbsPer100g;
    private float fatPer100g;
    private float fiberPer100g;
    private String category;

    public FoodItem(String name, int caloriesPer100g, float proteinPer100g, float carbsPer100g, float fatPer100g, float fiberPer100g, String category) {
        this.name = name;
        this.caloriesPer100g = caloriesPer100g;
        this.proteinPer100g = proteinPer100g;
        this.carbsPer100g = carbsPer100g;
        this.fatPer100g = fatPer100g;
        this.fiberPer100g = fiberPer100g;
        this.category = category;
    }

    // Getters
    public String getName() { return name; }
    public int getCaloriesPer100g() { return caloriesPer100g; }
    public float getProteinPer100g() { return proteinPer100g; }
    public float getCarbsPer100g() { return carbsPer100g; }
    public float getFatPer100g() { return fatPer100g; }
    public float getFiberPer100g() { return fiberPer100g; }
    public String getCategory() { return category; }

    // Calculate nutrition for given serving size in grams
    public int getCaloriesForServing(float grams) {
        return Math.round((caloriesPer100g * grams) / 100);
    }

    public float getProteinForServing(float grams) {
        return (proteinPer100g * grams) / 100;
    }

    public float getCarbsForServing(float grams) {
        return (carbsPer100g * grams) / 100;
    }

    public float getFatForServing(float grams) {
        return (fatPer100g * grams) / 100;
    }

    public float getFiberForServing(float grams) {
        return (fiberPer100g * grams) / 100;
    }
}
