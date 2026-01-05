package com.example.medicam;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SymptomCheckerActivity extends AppCompatActivity {
    
    private static final String GEMINI_API_KEY = "AIzaSyByn7F2bu67Z1Hk37erTHbck2Qe7Td9cT8";
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + GEMINI_API_KEY;
    
    private ChipGroup chipGroupSymptoms;
    private EditText etAdditionalSymptoms;
    private EditText etDuration;
    private EditText etAge;
    private MaterialButton btnAnalyze;
    private ProgressBar progressBar;
    private LinearLayout layoutResults;
    private TextView tvDiagnosis;
    private TextView tvRecommendations;
    private TextView tvUrgency;
    private MaterialCardView cardResults;
    
    private OkHttpClient client;
    private List<String> selectedSymptoms = new ArrayList<>();
    
    // Common symptoms
    private final String[] commonSymptoms = {
        "Headache", "Fever", "Cough", "Fatigue", "Nausea",
        "Sore Throat", "Body Ache", "Chest Pain", "Shortness of Breath",
        "Dizziness", "Stomach Pain", "Diarrhea", "Constipation",
        "Runny Nose", "Sneezing", "Joint Pain", "Skin Rash",
        "Loss of Appetite", "Vomiting", "Insomnia"
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptom_checker);
        
        client = new OkHttpClient();
        
        initializeViews();
        setupSymptomChips();
        setupClickListeners();
    }
    
    private void initializeViews() {
        chipGroupSymptoms = findViewById(R.id.chipGroupSymptoms);
        etAdditionalSymptoms = findViewById(R.id.etAdditionalSymptoms);
        etDuration = findViewById(R.id.etDuration);
        etAge = findViewById(R.id.etAge);
        btnAnalyze = findViewById(R.id.btnAnalyze);
        progressBar = findViewById(R.id.progressBar);
        layoutResults = findViewById(R.id.layoutResults);
        tvDiagnosis = findViewById(R.id.tvDiagnosis);
        tvRecommendations = findViewById(R.id.tvRecommendations);
        tvUrgency = findViewById(R.id.tvUrgency);
        cardResults = findViewById(R.id.cardResults);
        
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }
    
    private void setupSymptomChips() {
        for (String symptom : commonSymptoms) {
            Chip chip = new Chip(this);
            chip.setText(symptom);
            chip.setCheckable(true);
            chip.setCheckedIconVisible(true);
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedSymptoms.add(symptom);
                } else {
                    selectedSymptoms.remove(symptom);
                }
            });
            chipGroupSymptoms.addView(chip);
        }
    }
    
    private void setupClickListeners() {
        btnAnalyze.setOnClickListener(v -> analyzeSymptoms());
    }
    
    private void analyzeSymptoms() {
        // Gather all symptoms
        List<String> allSymptoms = new ArrayList<>(selectedSymptoms);
        String additional = etAdditionalSymptoms.getText().toString().trim();
        if (!TextUtils.isEmpty(additional)) {
            allSymptoms.add(additional);
        }
        
        if (allSymptoms.isEmpty()) {
            Toast.makeText(this, "Please select or enter at least one symptom", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String duration = etDuration.getText().toString().trim();
        String age = etAge.getText().toString().trim();
        
        // Show loading
        progressBar.setVisibility(View.VISIBLE);
        btnAnalyze.setEnabled(false);
        cardResults.setVisibility(View.GONE);
        
        // Build prompt
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are a medical AI assistant. Analyze the following symptoms and provide:\n");
        prompt.append("1. Possible conditions (list top 3-5 most likely)\n");
        prompt.append("2. Recommended actions\n");
        prompt.append("3. Urgency level (Low/Medium/High/Emergency)\n\n");
        prompt.append("Symptoms: ").append(TextUtils.join(", ", allSymptoms)).append("\n");
        
        if (!TextUtils.isEmpty(duration)) {
            prompt.append("Duration: ").append(duration).append(" days\n");
        }
        if (!TextUtils.isEmpty(age)) {
            prompt.append("Patient Age: ").append(age).append(" years\n");
        }
        
        prompt.append("\nIMPORTANT DISCLAIMER: Include a note that this is not a medical diagnosis and the user should consult a healthcare professional.\n");
        prompt.append("\nFormat your response clearly with sections:\n");
        prompt.append("**Possible Conditions:**\n");
        prompt.append("**Recommendations:**\n");
        prompt.append("**Urgency Level:**\n");
        prompt.append("**Disclaimer:**");
        
        callGeminiAPI(prompt.toString());
    }
    
    private void callGeminiAPI(String prompt) {
        try {
            JSONObject textPart = new JSONObject();
            textPart.put("text", prompt);
            
            JSONArray partsArray = new JSONArray();
            partsArray.put(textPart);
            
            JSONObject contentObject = new JSONObject();
            contentObject.put("parts", partsArray);
            
            JSONArray contentsArray = new JSONArray();
            contentsArray.put(contentObject);
            
            JSONObject requestBody = new JSONObject();
            requestBody.put("contents", contentsArray);
            
            RequestBody body = RequestBody.create(
                requestBody.toString(),
                MediaType.get("application/json; charset=utf-8")
            );
            
            Request request = new Request.Builder()
                .url(GEMINI_API_URL)
                .post(body)
                .build();
            
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        btnAnalyze.setEnabled(true);
                        Toast.makeText(SymptomCheckerActivity.this, 
                            "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
                
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = response.body().string();
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        btnAnalyze.setEnabled(true);
                        parseAndDisplayResults(responseBody);
                    });
                }
            });
            
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            btnAnalyze.setEnabled(true);
            Toast.makeText(this, "Error building request", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void parseAndDisplayResults(String responseBody) {
        try {
            JSONObject jsonResponse = new JSONObject(responseBody);
            JSONArray candidates = jsonResponse.getJSONArray("candidates");
            if (candidates.length() > 0) {
                JSONObject firstCandidate = candidates.getJSONObject(0);
                JSONObject content = firstCandidate.getJSONObject("content");
                JSONArray parts = content.getJSONArray("parts");
                if (parts.length() > 0) {
                    String text = parts.getJSONObject(0).getString("text");
                    displayResults(text);
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void displayResults(String analysis) {
        cardResults.setVisibility(View.VISIBLE);
        
        // Parse sections
        String conditions = "";
        String recommendations = "";
        String urgency = "Medium";
        
        // Simple parsing
        String[] sections = analysis.split("\\*\\*");
        StringBuilder fullText = new StringBuilder();
        
        for (int i = 0; i < sections.length; i++) {
            String section = sections[i].trim();
            if (section.toLowerCase().contains("possible conditions")) {
                if (i + 1 < sections.length) {
                    conditions = sections[i + 1].trim();
                }
            } else if (section.toLowerCase().contains("recommendation")) {
                if (i + 1 < sections.length) {
                    recommendations = sections[i + 1].trim();
                }
            } else if (section.toLowerCase().contains("urgency")) {
                if (i + 1 < sections.length) {
                    String urgencyText = sections[i + 1].trim().toLowerCase();
                    if (urgencyText.contains("emergency") || urgencyText.contains("high")) {
                        urgency = "High";
                        tvUrgency.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    } else if (urgencyText.contains("medium") || urgencyText.contains("moderate")) {
                        urgency = "Medium";
                        tvUrgency.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
                    } else {
                        urgency = "Low";
                        tvUrgency.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                    }
                }
            }
        }
        
        // Display formatted results
        tvDiagnosis.setText(conditions.isEmpty() ? analysis : conditions);
        tvRecommendations.setText(recommendations.isEmpty() ? "Please consult a healthcare professional for personalized advice." : recommendations);
        tvUrgency.setText("Urgency: " + urgency);
    }
}
