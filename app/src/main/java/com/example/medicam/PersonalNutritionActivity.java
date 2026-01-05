package com.example.medicam;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicam.adapters.FoodSearchAdapter;
import com.example.medicam.adapters.MealItemAdapter;
import com.example.medicam.models.FoodItem;
import com.example.medicam.models.MealItem;
import com.example.medicam.models.NutritionGoal;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class PersonalNutritionActivity extends AppCompatActivity implements MealItemAdapter.OnMealItemClickListener {

    private static final String PREFS_NAME = "NutritionPrefs";
    private static final String KEY_MEALS = "meals";
    private static final String KEY_GOAL = "nutrition_goal";

    // Views
    private ImageView btnBack;
    private TextView tvDate;
    private TextView tvCaloriesConsumed, tvCaloriesGoal, tvCaloriesRemaining;
    private ProgressBar progressCalories;
    private TextView tvProteinValue, tvCarbsValue, tvFatValue, tvFiberValue;
    private ProgressBar progressProtein, progressCarbs, progressFat, progressFiber;
    private TabLayout tabMealType;
    private RecyclerView rvMeals;
    private FloatingActionButton fabAddMeal;
    private MaterialCardView cardSetGoals;
    private MaterialCardView cardAiSuggestion;
    private TextView tvAiSuggestion;
    private LinearLayout layoutNoMeals;

    // Data
    private List<MealItem> allMeals = new ArrayList<>();
    private List<MealItem> filteredMeals = new ArrayList<>();
    private MealItemAdapter mealAdapter;
    private NutritionGoal nutritionGoal;
    private String currentDate;
    private String selectedMealType = "all";
    private List<FoodItem> foodDatabase = new ArrayList<>();

    private SharedPreferences prefs;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_nutrition);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        initializeViews();
        initializeFoodDatabase();
        loadData();
        setupListeners();
        updateUI();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        tvDate = findViewById(R.id.tvDate);
        
        tvCaloriesConsumed = findViewById(R.id.tvCaloriesConsumed);
        tvCaloriesGoal = findViewById(R.id.tvCaloriesGoal);
        tvCaloriesRemaining = findViewById(R.id.tvCaloriesRemaining);
        progressCalories = findViewById(R.id.progressCalories);
        
        tvProteinValue = findViewById(R.id.tvProteinValue);
        tvCarbsValue = findViewById(R.id.tvCarbsValue);
        tvFatValue = findViewById(R.id.tvFatValue);
        tvFiberValue = findViewById(R.id.tvFiberValue);
        
        progressProtein = findViewById(R.id.progressProtein);
        progressCarbs = findViewById(R.id.progressCarbs);
        progressFat = findViewById(R.id.progressFat);
        progressFiber = findViewById(R.id.progressFiber);
        
        tabMealType = findViewById(R.id.tabMealType);
        rvMeals = findViewById(R.id.rvMeals);
        fabAddMeal = findViewById(R.id.fabAddMeal);
        cardSetGoals = findViewById(R.id.cardSetGoals);
        cardAiSuggestion = findViewById(R.id.cardAiSuggestion);
        tvAiSuggestion = findViewById(R.id.tvAiSuggestion);
        layoutNoMeals = findViewById(R.id.layoutNoMeals);

        // Set current date
        String displayDate = new SimpleDateFormat("EEEE, MMM dd", Locale.getDefault()).format(new Date());
        tvDate.setText(displayDate);

        // Setup RecyclerView
        mealAdapter = new MealItemAdapter(filteredMeals, this);
        rvMeals.setLayoutManager(new LinearLayoutManager(this));
        rvMeals.setAdapter(mealAdapter);

        // Setup tabs
        tabMealType.addTab(tabMealType.newTab().setText("All"));
        tabMealType.addTab(tabMealType.newTab().setText("Breakfast"));
        tabMealType.addTab(tabMealType.newTab().setText("Lunch"));
        tabMealType.addTab(tabMealType.newTab().setText("Dinner"));
        tabMealType.addTab(tabMealType.newTab().setText("Snack"));
    }

    private void initializeFoodDatabase() {
        // Common Indian and international foods
        foodDatabase.add(new FoodItem("Rice (Cooked)", 130, 2.7f, 28f, 0.3f, 0.4f, "Grains"));
        foodDatabase.add(new FoodItem("Roti/Chapati", 120, 3.7f, 18f, 3.7f, 2f, "Bread"));
        foodDatabase.add(new FoodItem("Dal (Lentils)", 116, 9f, 20f, 0.4f, 8f, "Legumes"));
        foodDatabase.add(new FoodItem("Chicken Breast", 165, 31f, 0f, 3.6f, 0f, "Protein"));
        foodDatabase.add(new FoodItem("Egg (Boiled)", 155, 13f, 1.1f, 11f, 0f, "Protein"));
        foodDatabase.add(new FoodItem("Paneer", 265, 18f, 1.2f, 21f, 0f, "Dairy"));
        foodDatabase.add(new FoodItem("Milk (Full Fat)", 61, 3.2f, 4.8f, 3.3f, 0f, "Dairy"));
        foodDatabase.add(new FoodItem("Curd/Yogurt", 98, 11f, 3.4f, 5f, 0f, "Dairy"));
        foodDatabase.add(new FoodItem("Banana", 89, 1.1f, 23f, 0.3f, 2.6f, "Fruits"));
        foodDatabase.add(new FoodItem("Apple", 52, 0.3f, 14f, 0.2f, 2.4f, "Fruits"));
        foodDatabase.add(new FoodItem("Mango", 60, 0.8f, 15f, 0.4f, 1.6f, "Fruits"));
        foodDatabase.add(new FoodItem("Orange", 47, 0.9f, 12f, 0.1f, 2.4f, "Fruits"));
        foodDatabase.add(new FoodItem("Spinach (Palak)", 23, 2.9f, 3.6f, 0.4f, 2.2f, "Vegetables"));
        foodDatabase.add(new FoodItem("Potato", 77, 2f, 17f, 0.1f, 2.2f, "Vegetables"));
        foodDatabase.add(new FoodItem("Tomato", 18, 0.9f, 3.9f, 0.2f, 1.2f, "Vegetables"));
        foodDatabase.add(new FoodItem("Onion", 40, 1.1f, 9f, 0.1f, 1.7f, "Vegetables"));
        foodDatabase.add(new FoodItem("Carrot", 41, 0.9f, 10f, 0.2f, 2.8f, "Vegetables"));
        foodDatabase.add(new FoodItem("Cucumber", 15, 0.7f, 3.6f, 0.1f, 0.5f, "Vegetables"));
        foodDatabase.add(new FoodItem("Almonds", 579, 21f, 22f, 50f, 12f, "Nuts"));
        foodDatabase.add(new FoodItem("Peanuts", 567, 26f, 16f, 49f, 8.5f, "Nuts"));
        foodDatabase.add(new FoodItem("Oats", 389, 17f, 66f, 7f, 11f, "Grains"));
        foodDatabase.add(new FoodItem("Bread (White)", 265, 9f, 49f, 3.2f, 2.7f, "Bread"));
        foodDatabase.add(new FoodItem("Bread (Brown)", 247, 13f, 41f, 4.2f, 7f, "Bread"));
        foodDatabase.add(new FoodItem("Fish (Rohu)", 97, 17f, 0f, 2.8f, 0f, "Protein"));
        foodDatabase.add(new FoodItem("Mutton", 294, 25f, 0f, 21f, 0f, "Protein"));
        foodDatabase.add(new FoodItem("Samosa", 262, 4.3f, 24f, 17f, 1.5f, "Snacks"));
        foodDatabase.add(new FoodItem("Pakora", 200, 5f, 18f, 12f, 2f, "Snacks"));
        foodDatabase.add(new FoodItem("Idli (2 pieces)", 78, 2f, 16f, 0.4f, 0.8f, "Breakfast"));
        foodDatabase.add(new FoodItem("Dosa", 168, 4f, 28f, 4.5f, 1f, "Breakfast"));
        foodDatabase.add(new FoodItem("Upma", 161, 4f, 24f, 5f, 2f, "Breakfast"));
        foodDatabase.add(new FoodItem("Poha", 180, 3f, 32f, 4.5f, 2f, "Breakfast"));
        foodDatabase.add(new FoodItem("Paratha", 260, 5f, 30f, 13f, 2f, "Bread"));
        foodDatabase.add(new FoodItem("Biryani (Chicken)", 220, 12f, 25f, 8f, 1f, "Main Course"));
        foodDatabase.add(new FoodItem("Butter Chicken", 240, 15f, 8f, 17f, 1f, "Main Course"));
        foodDatabase.add(new FoodItem("Palak Paneer", 180, 9f, 8f, 13f, 2f, "Main Course"));
        foodDatabase.add(new FoodItem("Rajma (Kidney Beans)", 127, 9f, 23f, 0.5f, 7f, "Legumes"));
        foodDatabase.add(new FoodItem("Chole (Chickpeas)", 164, 9f, 27f, 3f, 8f, "Legumes"));
        foodDatabase.add(new FoodItem("Tea (with milk & sugar)", 37, 0.7f, 6f, 1f, 0f, "Beverages"));
        foodDatabase.add(new FoodItem("Coffee (with milk)", 30, 1f, 3f, 1.5f, 0f, "Beverages"));
        foodDatabase.add(new FoodItem("Lassi (Sweet)", 112, 3f, 18f, 3f, 0f, "Beverages"));
        foodDatabase.add(new FoodItem("Coconut Water", 19, 0.7f, 3.7f, 0.2f, 1.1f, "Beverages"));
    }

    private void loadData() {
        // Load nutrition goal
        String goalJson = prefs.getString(KEY_GOAL, null);
        if (goalJson != null) {
            nutritionGoal = gson.fromJson(goalJson, NutritionGoal.class);
        } else {
            nutritionGoal = new NutritionGoal();
        }

        // Load meals
        String mealsJson = prefs.getString(KEY_MEALS, null);
        if (mealsJson != null) {
            Type type = new TypeToken<List<MealItem>>(){}.getType();
            allMeals = gson.fromJson(mealsJson, type);
            if (allMeals == null) {
                allMeals = new ArrayList<>();
            }
        }

        filterMealsByDate();
    }

    private void saveData() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_MEALS, gson.toJson(allMeals));
        editor.putString(KEY_GOAL, gson.toJson(nutritionGoal));
        editor.apply();
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        fabAddMeal.setOnClickListener(v -> showAddMealDialog());

        cardSetGoals.setOnClickListener(v -> showSetGoalsDialog());

        tabMealType.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: selectedMealType = "all"; break;
                    case 1: selectedMealType = "breakfast"; break;
                    case 2: selectedMealType = "lunch"; break;
                    case 3: selectedMealType = "dinner"; break;
                    case 4: selectedMealType = "snack"; break;
                }
                filterMealsByType();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void filterMealsByDate() {
        filteredMeals = allMeals.stream()
                .filter(meal -> currentDate.equals(meal.getDate()))
                .collect(Collectors.toList());
        filterMealsByType();
    }

    private void filterMealsByType() {
        List<MealItem> todayMeals = allMeals.stream()
                .filter(meal -> currentDate.equals(meal.getDate()))
                .collect(Collectors.toList());

        if (selectedMealType.equals("all")) {
            filteredMeals = new ArrayList<>(todayMeals);
        } else {
            filteredMeals = todayMeals.stream()
                    .filter(meal -> selectedMealType.equalsIgnoreCase(meal.getMealType()))
                    .collect(Collectors.toList());
        }
        
        mealAdapter.updateData(filteredMeals);
        updateUI();
    }

    private void updateUI() {
        // Calculate totals for today
        List<MealItem> todayMeals = allMeals.stream()
                .filter(meal -> currentDate.equals(meal.getDate()))
                .collect(Collectors.toList());

        int totalCalories = 0;
        float totalProtein = 0, totalCarbs = 0, totalFat = 0, totalFiber = 0;

        for (MealItem meal : todayMeals) {
            totalCalories += meal.getCalories();
            totalProtein += meal.getProtein();
            totalCarbs += meal.getCarbs();
            totalFat += meal.getFat();
            totalFiber += meal.getFiber();
        }

        // Update calories
        tvCaloriesConsumed.setText(String.valueOf(totalCalories));
        tvCaloriesGoal.setText("/ " + nutritionGoal.getDailyCalories());
        int remaining = nutritionGoal.getDailyCalories() - totalCalories;
        tvCaloriesRemaining.setText(remaining + " cal remaining");
        if (remaining < 0) {
            tvCaloriesRemaining.setTextColor(Color.parseColor("#F44336"));
            tvCaloriesRemaining.setText(Math.abs(remaining) + " cal over");
        } else {
            tvCaloriesRemaining.setTextColor(Color.parseColor("#4CAF50"));
        }

        int calorieProgress = (int) ((totalCalories * 100f) / nutritionGoal.getDailyCalories());
        progressCalories.setProgress(Math.min(calorieProgress, 100));

        // Update macros
        tvProteinValue.setText(String.format("%.1f / %.0fg", totalProtein, nutritionGoal.getDailyProtein()));
        tvCarbsValue.setText(String.format("%.1f / %.0fg", totalCarbs, nutritionGoal.getDailyCarbs()));
        tvFatValue.setText(String.format("%.1f / %.0fg", totalFat, nutritionGoal.getDailyFat()));
        tvFiberValue.setText(String.format("%.1f / %.0fg", totalFiber, nutritionGoal.getDailyFiber()));

        progressProtein.setProgress((int) ((totalProtein * 100) / nutritionGoal.getDailyProtein()));
        progressCarbs.setProgress((int) ((totalCarbs * 100) / nutritionGoal.getDailyCarbs()));
        progressFat.setProgress((int) ((totalFat * 100) / nutritionGoal.getDailyFat()));
        progressFiber.setProgress((int) ((totalFiber * 100) / nutritionGoal.getDailyFiber()));

        // Show/hide empty state
        if (filteredMeals.isEmpty()) {
            layoutNoMeals.setVisibility(View.VISIBLE);
            rvMeals.setVisibility(View.GONE);
        } else {
            layoutNoMeals.setVisibility(View.GONE);
            rvMeals.setVisibility(View.VISIBLE);
        }

        // Update AI suggestion
        updateAiSuggestion(totalCalories, totalProtein, totalCarbs, totalFat);
    }

    private void updateAiSuggestion(int calories, float protein, float carbs, float fat) {
        StringBuilder suggestion = new StringBuilder();
        
        int remainingCal = nutritionGoal.getDailyCalories() - calories;
        float remainingProtein = nutritionGoal.getDailyProtein() - protein;

        if (calories == 0) {
            suggestion.append("🌅 Start your day right! Log your breakfast to track your nutrition.");
        } else if (remainingCal > 500) {
            suggestion.append("💪 You have ").append(remainingCal).append(" calories left. ");
            if (remainingProtein > 20) {
                suggestion.append("Consider adding protein-rich foods like eggs, paneer, or dal.");
            } else {
                suggestion.append("A balanced meal with vegetables would be great!");
            }
        } else if (remainingCal > 0 && remainingCal <= 500) {
            suggestion.append("🎯 Almost there! You have ").append(remainingCal).append(" calories left. A light snack like fruits or nuts would be perfect.");
        } else if (remainingCal < 0) {
            suggestion.append("⚠️ You've exceeded your daily goal by ").append(Math.abs(remainingCal)).append(" calories. Consider a light dinner or some physical activity.");
        }

        tvAiSuggestion.setText(suggestion.toString());
    }

    private void showAddMealDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_meal);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(
                (int) (getResources().getDisplayMetrics().widthPixels * 0.9),
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        EditText etSearchFood = dialog.findViewById(R.id.etSearchFood);
        RecyclerView rvFoodSearch = dialog.findViewById(R.id.rvFoodSearch);
        LinearLayout layoutManualEntry = dialog.findViewById(R.id.layoutManualEntry);
        TextView tvToggleManual = dialog.findViewById(R.id.tvToggleManual);
        
        EditText etFoodName = dialog.findViewById(R.id.etFoodName);
        EditText etCalories = dialog.findViewById(R.id.etCalories);
        EditText etProtein = dialog.findViewById(R.id.etProtein);
        EditText etCarbs = dialog.findViewById(R.id.etCarbs);
        EditText etFat = dialog.findViewById(R.id.etFat);
        EditText etFiber = dialog.findViewById(R.id.etFiber);
        EditText etServingSize = dialog.findViewById(R.id.etServingSize);
        Spinner spinnerMealType = dialog.findViewById(R.id.spinnerMealType);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnAdd = dialog.findViewById(R.id.btnAdd);

        // Setup meal type spinner
        String[] mealTypes = {"Breakfast", "Lunch", "Dinner", "Snack"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mealTypes);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMealType.setAdapter(spinnerAdapter);

        // Auto-select meal type based on time
        int hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY);
        if (hour >= 6 && hour < 11) {
            spinnerMealType.setSelection(0); // Breakfast
        } else if (hour >= 11 && hour < 15) {
            spinnerMealType.setSelection(1); // Lunch
        } else if (hour >= 15 && hour < 18) {
            spinnerMealType.setSelection(3); // Snack
        } else {
            spinnerMealType.setSelection(2); // Dinner
        }

        // Setup food search
        List<FoodItem> searchResults = new ArrayList<>(foodDatabase);
        FoodSearchAdapter searchAdapter = new FoodSearchAdapter(searchResults, foodItem -> {
            etFoodName.setText(foodItem.getName());
            etCalories.setText(String.valueOf(foodItem.getCaloriesPer100g()));
            etProtein.setText(String.valueOf(foodItem.getProteinPer100g()));
            etCarbs.setText(String.valueOf(foodItem.getCarbsPer100g()));
            etFat.setText(String.valueOf(foodItem.getFatPer100g()));
            etFiber.setText(String.valueOf(foodItem.getFiberPer100g()));
            etServingSize.setText("100g");
            layoutManualEntry.setVisibility(View.VISIBLE);
            rvFoodSearch.setVisibility(View.GONE);
        });
        
        rvFoodSearch.setLayoutManager(new LinearLayoutManager(this));
        rvFoodSearch.setAdapter(searchAdapter);

        etSearchFood.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().toLowerCase();
                if (query.isEmpty()) {
                    searchAdapter.updateData(foodDatabase);
                } else {
                    List<FoodItem> filtered = foodDatabase.stream()
                            .filter(food -> food.getName().toLowerCase().contains(query) ||
                                    food.getCategory().toLowerCase().contains(query))
                            .collect(Collectors.toList());
                    searchAdapter.updateData(filtered);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        tvToggleManual.setOnClickListener(v -> {
            if (layoutManualEntry.getVisibility() == View.VISIBLE) {
                layoutManualEntry.setVisibility(View.GONE);
                rvFoodSearch.setVisibility(View.VISIBLE);
                tvToggleManual.setText("Enter manually");
            } else {
                layoutManualEntry.setVisibility(View.VISIBLE);
                rvFoodSearch.setVisibility(View.GONE);
                tvToggleManual.setText("Search food");
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnAdd.setOnClickListener(v -> {
            String name = etFoodName.getText().toString().trim();
            String caloriesStr = etCalories.getText().toString().trim();
            String servingSize = etServingSize.getText().toString().trim();

            if (name.isEmpty() || caloriesStr.isEmpty()) {
                Toast.makeText(this, "Please enter food name and calories", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int calories = Integer.parseInt(caloriesStr);
                float protein = etProtein.getText().toString().isEmpty() ? 0 : Float.parseFloat(etProtein.getText().toString());
                float carbs = etCarbs.getText().toString().isEmpty() ? 0 : Float.parseFloat(etCarbs.getText().toString());
                float fat = etFat.getText().toString().isEmpty() ? 0 : Float.parseFloat(etFat.getText().toString());
                float fiber = etFiber.getText().toString().isEmpty() ? 0 : Float.parseFloat(etFiber.getText().toString());
                String mealType = spinnerMealType.getSelectedItem().toString();
                
                if (servingSize.isEmpty()) {
                    servingSize = "1 serving";
                }

                MealItem meal = new MealItem(name, mealType, calories, protein, carbs, fat, fiber, servingSize, currentDate);
                allMeals.add(meal);
                saveData();
                filterMealsByDate();
                
                Toast.makeText(this, "Meal added successfully", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void showSetGoalsDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_set_nutrition_goals);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(
                (int) (getResources().getDisplayMetrics().widthPixels * 0.9),
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        EditText etCalorieGoal = dialog.findViewById(R.id.etCalorieGoal);
        EditText etProteinGoal = dialog.findViewById(R.id.etProteinGoal);
        EditText etCarbsGoal = dialog.findViewById(R.id.etCarbsGoal);
        EditText etFatGoal = dialog.findViewById(R.id.etFatGoal);
        EditText etFiberGoal = dialog.findViewById(R.id.etFiberGoal);
        Spinner spinnerGoalType = dialog.findViewById(R.id.spinnerGoalType);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnSave = dialog.findViewById(R.id.btnSave);

        // Setup goal type spinner
        String[] goalTypes = {"Maintain Weight", "Lose Weight", "Gain Weight"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, goalTypes);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGoalType.setAdapter(spinnerAdapter);

        // Set current values
        etCalorieGoal.setText(String.valueOf(nutritionGoal.getDailyCalories()));
        etProteinGoal.setText(String.valueOf((int) nutritionGoal.getDailyProtein()));
        etCarbsGoal.setText(String.valueOf((int) nutritionGoal.getDailyCarbs()));
        etFatGoal.setText(String.valueOf((int) nutritionGoal.getDailyFat()));
        etFiberGoal.setText(String.valueOf((int) nutritionGoal.getDailyFiber()));

        switch (nutritionGoal.getGoalType()) {
            case "lose_weight": spinnerGoalType.setSelection(1); break;
            case "gain_weight": spinnerGoalType.setSelection(2); break;
            default: spinnerGoalType.setSelection(0); break;
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            try {
                nutritionGoal.setDailyCalories(Integer.parseInt(etCalorieGoal.getText().toString()));
                nutritionGoal.setDailyProtein(Float.parseFloat(etProteinGoal.getText().toString()));
                nutritionGoal.setDailyCarbs(Float.parseFloat(etCarbsGoal.getText().toString()));
                nutritionGoal.setDailyFat(Float.parseFloat(etFatGoal.getText().toString()));
                nutritionGoal.setDailyFiber(Float.parseFloat(etFiberGoal.getText().toString()));

                String selectedGoal = spinnerGoalType.getSelectedItem().toString();
                switch (selectedGoal) {
                    case "Lose Weight": nutritionGoal.setGoalType("lose_weight"); break;
                    case "Gain Weight": nutritionGoal.setGoalType("gain_weight"); break;
                    default: nutritionGoal.setGoalType("maintain"); break;
                }

                saveData();
                updateUI();
                Toast.makeText(this, "Goals updated successfully", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    @Override
    public void onDeleteClick(MealItem mealItem, int position) {
        allMeals.remove(mealItem);
        saveData();
        filterMealsByDate();
        Toast.makeText(this, "Meal removed", Toast.LENGTH_SHORT).show();
    }
}
