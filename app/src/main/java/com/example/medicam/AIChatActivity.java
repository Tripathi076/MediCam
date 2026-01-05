package com.example.medicam;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AIChatActivity extends AppCompatActivity {

    private RecyclerView rvChat;
    private EditText etMessage;
    private FloatingActionButton fabSend;
    private ProgressBar progressLoading;
    private LinearLayout typingIndicator;
    
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    
    private ExecutorService executorService;
    private Handler mainHandler;
    
    // API Configuration
    private static final String GEMINI_API_KEY = "AIzaSyByn7F2bu67Z1Hk37erTHbck2Qe7Td9cT8";
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent";
    
    // System prompt for health assistant
    private static final String SYSTEM_PROMPT = "You are MediCam AI, a helpful health assistant. " +
            "You provide general health information and guidance. " +
            "Always remind users to consult with healthcare professionals for medical advice. " +
            "Be empathetic, clear, and concise in your responses. " +
            "If asked about emergencies, advise calling emergency services immediately.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_chat);

        initViews();
        setupRecyclerView();
        setupClickListeners();
        
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
        
        // Add welcome message
        addWelcomeMessage();
    }

    private void initViews() {
        rvChat = findViewById(R.id.rvChat);
        etMessage = findViewById(R.id.etMessage);
        fabSend = findViewById(R.id.fabSend);
        progressLoading = findViewById(R.id.progressLoading);
        typingIndicator = findViewById(R.id.typingIndicator);
        
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
    }

    private void setupRecyclerView() {
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, chatMessages);
        
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvChat.setLayoutManager(layoutManager);
        rvChat.setAdapter(chatAdapter);
    }

    private void setupClickListeners() {
        fabSend.setOnClickListener(v -> sendMessage());
        
        etMessage.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage();
                return true;
            }
            return false;
        });
    }

    private void addWelcomeMessage() {
        String welcomeMessage = "👋 Hello! I'm MediCam AI, your health assistant.\n\n" +
                "I can help you with:\n" +
                "• Understanding health reports\n" +
                "• General health information\n" +
                "• Medication queries\n" +
                "• Wellness tips\n\n" +
                "How can I assist you today?\n\n" +
                "⚠️ Note: I provide general information only. Always consult a healthcare professional for medical advice.";
        
        chatMessages.add(new ChatMessage(welcomeMessage, ChatMessage.TYPE_AI));
        chatAdapter.notifyItemInserted(0);
    }

    private void sendMessage() {
        String message = etMessage.getText().toString().trim();
        
        if (message.isEmpty()) {
            return;
        }
        
        // Add user message
        chatAdapter.addMessage(new ChatMessage(message, ChatMessage.TYPE_USER));
        etMessage.setText("");
        scrollToBottom();
        
        // Show typing indicator
        showTypingIndicator(true);
        
        // Get AI response
        if (GEMINI_API_KEY.isEmpty()) {
            // Use demo mode if no API key
            getDemoResponse(message);
        } else {
            getAIResponse(message);
        }
    }

    private void getAIResponse(String userMessage) {
        executorService.execute(() -> {
            try {
                String response = callGeminiAPI(userMessage);
                mainHandler.post(() -> {
                    showTypingIndicator(false);
                    chatAdapter.addMessage(new ChatMessage(response, ChatMessage.TYPE_AI));
                    scrollToBottom();
                });
            } catch (Exception e) {
                mainHandler.post(() -> {
                    showTypingIndicator(false);
                    // Fall back to demo response on error
                    getDemoResponse(userMessage);
                });
            }
        });
    }

    private String callGeminiAPI(String userMessage) throws Exception {
        URL url = new URL(GEMINI_API_URL + "?key=" + GEMINI_API_KEY);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(30000);

        // Build request body
        JSONObject requestBody = new JSONObject();
        JSONArray contents = new JSONArray();
        
        // Add system context
        JSONObject systemContent = new JSONObject();
        systemContent.put("role", "user");
        JSONArray systemParts = new JSONArray();
        JSONObject systemPart = new JSONObject();
        systemPart.put("text", SYSTEM_PROMPT + "\n\nUser question: " + userMessage);
        systemParts.put(systemPart);
        systemContent.put("parts", systemParts);
        contents.put(systemContent);
        
        requestBody.put("contents", contents);
        
        // Safety settings
        JSONArray safetySettings = new JSONArray();
        String[] categories = {"HARM_CATEGORY_HARASSMENT", "HARM_CATEGORY_HATE_SPEECH", 
                "HARM_CATEGORY_SEXUALLY_EXPLICIT", "HARM_CATEGORY_DANGEROUS_CONTENT"};
        for (String category : categories) {
            JSONObject setting = new JSONObject();
            setting.put("category", category);
            setting.put("threshold", "BLOCK_MEDIUM_AND_ABOVE");
            safetySettings.put(setting);
        }
        requestBody.put("safetySettings", safetySettings);

        // Send request
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = requestBody.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Read response
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            br.close();
            
            // Parse response
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray candidates = jsonResponse.getJSONArray("candidates");
            if (candidates.length() > 0) {
                JSONObject content = candidates.getJSONObject(0).getJSONObject("content");
                JSONArray parts = content.getJSONArray("parts");
                if (parts.length() > 0) {
                    return parts.getJSONObject(0).getString("text");
                }
            }
        }
        
        throw new Exception("API call failed");
    }

    private void getDemoResponse(String userMessage) {
        // Simulate typing delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            showTypingIndicator(false);
            String response = generateDemoResponse(userMessage);
            chatAdapter.addMessage(new ChatMessage(response, ChatMessage.TYPE_AI));
            scrollToBottom();
        }, 1500);
    }

    private String generateDemoResponse(String userMessage) {
        String messageLower = userMessage.toLowerCase();
        
        // Health-related responses
        if (messageLower.contains("headache") || messageLower.contains("head pain")) {
            return "🤕 Headaches can have many causes including:\n\n" +
                    "• Tension or stress\n" +
                    "• Dehydration\n" +
                    "• Lack of sleep\n" +
                    "• Eye strain\n" +
                    "• Caffeine withdrawal\n\n" +
                    "**Tips:**\n" +
                    "• Rest in a quiet, dark room\n" +
                    "• Stay hydrated\n" +
                    "• Try a cold or warm compress\n\n" +
                    "⚠️ If headaches are severe, sudden, or recurring, please consult a doctor.";
        }
        
        if (messageLower.contains("fever") || messageLower.contains("temperature")) {
            return "🌡️ Fever is usually a sign your body is fighting an infection.\n\n" +
                    "**What to do:**\n" +
                    "• Rest and stay hydrated\n" +
                    "• Take fever-reducing medication (as directed)\n" +
                    "• Use a light blanket\n" +
                    "• Monitor temperature regularly\n\n" +
                    "🚨 **Seek immediate care if:**\n" +
                    "• Temperature exceeds 103°F (39.4°C)\n" +
                    "• Fever lasts more than 3 days\n" +
                    "• You experience confusion or difficulty breathing";
        }
        
        if (messageLower.contains("cold") || messageLower.contains("cough") || messageLower.contains("flu")) {
            return "🤧 Common cold and flu symptoms can be managed at home:\n\n" +
                    "**Remedies:**\n" +
                    "• Get plenty of rest\n" +
                    "• Drink warm fluids (tea, soup, water)\n" +
                    "• Use a humidifier\n" +
                    "• Gargle with salt water for sore throat\n" +
                    "• Honey for cough (adults only)\n\n" +
                    "Most colds resolve in 7-10 days. See a doctor if symptoms worsen or don't improve.";
        }
        
        if (messageLower.contains("blood pressure") || messageLower.contains("bp")) {
            return "💓 Blood Pressure Information:\n\n" +
                    "**Normal ranges:**\n" +
                    "• Normal: Less than 120/80 mmHg\n" +
                    "• Elevated: 120-129/<80 mmHg\n" +
                    "• High (Stage 1): 130-139/80-89 mmHg\n" +
                    "• High (Stage 2): 140+/90+ mmHg\n\n" +
                    "**Tips to maintain healthy BP:**\n" +
                    "• Reduce sodium intake\n" +
                    "• Exercise regularly\n" +
                    "• Maintain healthy weight\n" +
                    "• Limit alcohol\n" +
                    "• Manage stress";
        }
        
        if (messageLower.contains("diabetes") || messageLower.contains("sugar") || messageLower.contains("glucose")) {
            return "🩸 Diabetes Information:\n\n" +
                    "**Blood Sugar Levels:**\n" +
                    "• Normal fasting: 70-100 mg/dL\n" +
                    "• Prediabetes: 100-125 mg/dL\n" +
                    "• Diabetes: 126+ mg/dL\n\n" +
                    "**Management tips:**\n" +
                    "• Monitor blood sugar regularly\n" +
                    "• Follow a balanced diet\n" +
                    "• Stay physically active\n" +
                    "• Take medications as prescribed\n" +
                    "• Regular check-ups with your doctor";
        }
        
        if (messageLower.contains("bmi") || messageLower.contains("weight") || messageLower.contains("obesity")) {
            return "⚖️ BMI Categories:\n\n" +
                    "• Underweight: Below 18.5\n" +
                    "• Normal: 18.5 - 24.9\n" +
                    "• Overweight: 25 - 29.9\n" +
                    "• Obese: 30 and above\n\n" +
                    "**Healthy weight tips:**\n" +
                    "• Balanced diet with proper portions\n" +
                    "• Regular physical activity\n" +
                    "• Stay hydrated\n" +
                    "• Get adequate sleep\n\n" +
                    "Use our BMI Calculator feature for your personalized calculation!";
        }
        
        if (messageLower.contains("sleep") || messageLower.contains("insomnia")) {
            return "😴 Sleep Health Tips:\n\n" +
                    "**Recommended sleep:**\n" +
                    "• Adults: 7-9 hours\n" +
                    "• Teenagers: 8-10 hours\n\n" +
                    "**Better sleep habits:**\n" +
                    "• Maintain a consistent sleep schedule\n" +
                    "• Create a dark, cool sleeping environment\n" +
                    "• Avoid screens 1 hour before bed\n" +
                    "• Limit caffeine after 2 PM\n" +
                    "• Exercise regularly (not close to bedtime)\n" +
                    "• Practice relaxation techniques";
        }
        
        if (messageLower.contains("stress") || messageLower.contains("anxiety") || messageLower.contains("mental")) {
            return "🧠 Mental Health & Stress Management:\n\n" +
                    "**Coping strategies:**\n" +
                    "• Practice deep breathing exercises\n" +
                    "• Try meditation or mindfulness\n" +
                    "• Stay physically active\n" +
                    "• Connect with friends and family\n" +
                    "• Limit news and social media\n" +
                    "• Maintain a routine\n\n" +
                    "💚 Your mental health matters. If you're struggling, please reach out to a mental health professional or helpline.";
        }
        
        if (messageLower.contains("emergency") || messageLower.contains("urgent")) {
            return "🚨 **EMERGENCY GUIDANCE**\n\n" +
                    "If this is a medical emergency, please:\n\n" +
                    "1. **Call Emergency Services immediately**\n" +
                    "   • India: 112 or 108\n" +
                    "   • Ambulance: 102\n\n" +
                    "2. **Stay calm and provide clear information**\n\n" +
                    "3. **Don't delay seeking help**\n\n" +
                    "⚠️ I'm an AI assistant and cannot provide emergency medical care. Please contact emergency services immediately.";
        }
        
        if (messageLower.contains("report") || messageLower.contains("test") || messageLower.contains("pathology")) {
            return "📋 Understanding Medical Reports:\n\n" +
                    "I can help you understand general aspects of medical reports. For specific interpretation:\n\n" +
                    "• **Upload your reports** in the Pathology or Radiology sections\n" +
                    "• **Keep records organized** for doctor visits\n" +
                    "• **Track trends** over time\n\n" +
                    "Would you like me to explain any specific test or value? Share the details and I'll help explain what they generally mean.\n\n" +
                    "⚠️ Always discuss results with your healthcare provider.";
        }
        
        if (messageLower.contains("hello") || messageLower.contains("hi") || messageLower.contains("hey")) {
            return "👋 Hello! How can I help you with your health questions today?\n\n" +
                    "You can ask me about:\n" +
                    "• Symptoms and general health info\n" +
                    "• Understanding medical terms\n" +
                    "• Healthy lifestyle tips\n" +
                    "• Using MediCam features";
        }
        
        if (messageLower.contains("thank")) {
            return "You're welcome! 😊\n\n" +
                    "Remember, I'm here to provide general health information. For personalized medical advice, please consult with a healthcare professional.\n\n" +
                    "Is there anything else I can help you with?";
        }
        
        // Default response
        return "Thank you for your question! 🤔\n\n" +
                "I'm MediCam AI, designed to help with general health information. I can assist with:\n\n" +
                "• Common symptoms and remedies\n" +
                "• Understanding health metrics\n" +
                "• Wellness and lifestyle tips\n" +
                "• Navigating MediCam features\n\n" +
                "Could you please provide more details about your health question?\n\n" +
                "⚠️ For specific medical concerns, please consult a healthcare professional.";
    }

    private void showTypingIndicator(boolean show) {
        if (typingIndicator != null) {
            typingIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (progressLoading != null) {
            progressLoading.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void scrollToBottom() {
        if (chatAdapter.getItemCount() > 0) {
            rvChat.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
