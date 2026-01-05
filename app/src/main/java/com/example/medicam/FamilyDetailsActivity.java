package com.example.medicam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.example.medicam.models.FamilyMember;
import com.example.medicam.adapters.FamilyDetailsAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FamilyDetailsActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvTitle;
    private RecyclerView rvFamilyMembers;
    private MaterialButton btnAddMember;

    private SharedPreferences prefs;
    private Gson gson;
    private List<FamilyMember> familyMembers;
    private FamilyDetailsAdapter adapter;

    private static final String FAMILY_PREFS = "FamilyPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_details);

        prefs = getSharedPreferences(FAMILY_PREFS, MODE_PRIVATE);
        gson = new Gson();

        initViews();
        loadFamilyMembers();
        setupClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);
        rvFamilyMembers = findViewById(R.id.rvFamilyMembers);
        btnAddMember = findViewById(R.id.btnAddMember);
    }

    private void loadFamilyMembers() {
        String json = prefs.getString("family_members", null);
        
        if (json != null) {
            Type type = new TypeToken<ArrayList<FamilyMember>>(){}.getType();
            familyMembers = gson.fromJson(json, type);
        } else {
            familyMembers = new ArrayList<>();
        }

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        rvFamilyMembers.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FamilyDetailsAdapter(this, familyMembers, member -> {
            Intent intent = new Intent(this, FamilyMemberInfoActivity.class);
            intent.putExtra("member_id", member.getId());
            startActivity(intent);
        });
        rvFamilyMembers.setAdapter(adapter);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnAddMember.setOnClickListener(v -> {
            startActivity(new Intent(this, AddFamilyMemberActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFamilyMembers();
    }
}
