package com.example.medicam;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicam.adapters.FamilyMemberAdapter;
import com.example.medicam.models.FamilyMember;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FamilyHealthProfilesActivity extends AppCompatActivity implements FamilyMemberAdapter.OnMemberClickListener {
    
    private static final String PREFS_NAME = "family_health_prefs";
    private static final String KEY_MEMBERS = "family_members";
    
    private RecyclerView recyclerView;
    private FamilyMemberAdapter adapter;
    private List<FamilyMember> members = new ArrayList<>();
    private TextView tvEmpty;
    
    private SharedPreferences prefs;
    private Gson gson = new Gson();
    
    private final String[] relationships = { "Self", "Spouse", "Child", "Parent", "Sibling", "Grandparent", "Other" };
    private final String[] genders = { "Male", "Female", "Other" };
    private final String[] bloodTypes = { "Select", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-", "Unknown" };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_health_profiles);
        
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        
        initializeViews();
        loadMembers();
        setupRecyclerView();
    }
    
    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerMembers);
        tvEmpty = findViewById(R.id.tvEmpty);
        
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        
        FloatingActionButton fab = findViewById(R.id.fabAddMember);
        fab.setOnClickListener(v -> showAddMemberDialog(null));
    }
    
    private void setupRecyclerView() {
        adapter = new FamilyMemberAdapter(members, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
    
    private void loadMembers() {
        String json = prefs.getString(KEY_MEMBERS, "[]");
        Type type = new TypeToken<List<FamilyMember>>(){}.getType();
        members = gson.fromJson(json, type);
        if (members == null) members = new ArrayList<>();
        updateEmptyState();
    }
    
    private void saveMembers() {
        String json = gson.toJson(members);
        prefs.edit().putString(KEY_MEMBERS, json).apply();
    }
    
    private void updateEmptyState() {
        tvEmpty.setVisibility(members.isEmpty() ? View.VISIBLE : View.GONE);
        adapter.updateMembers(members);
    }
    
    private void showAddMemberDialog(FamilyMember existing) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_family_member, null);
        
        EditText etName = dialogView.findViewById(R.id.etName);
        Spinner spinnerRelationship = dialogView.findViewById(R.id.spinnerRelationship);
        EditText etAge = dialogView.findViewById(R.id.etAge);
        Spinner spinnerGender = dialogView.findViewById(R.id.spinnerGender);
        Spinner spinnerBloodType = dialogView.findViewById(R.id.spinnerBloodType);
        EditText etAllergies = dialogView.findViewById(R.id.etAllergies);
        EditText etConditions = dialogView.findViewById(R.id.etConditions);
        EditText etMedications = dialogView.findViewById(R.id.etMedications);
        EditText etEmergencyContact = dialogView.findViewById(R.id.etEmergencyContact);
        EditText etEmergencyPhone = dialogView.findViewById(R.id.etEmergencyPhone);
        EditText etNotes = dialogView.findViewById(R.id.etNotes);
        
        // Setup spinners
        ArrayAdapter<String> relationshipAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_dropdown_item, relationships);
        spinnerRelationship.setAdapter(relationshipAdapter);
        
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_dropdown_item, genders);
        spinnerGender.setAdapter(genderAdapter);
        
        ArrayAdapter<String> bloodAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_dropdown_item, bloodTypes);
        spinnerBloodType.setAdapter(bloodAdapter);
        
        // Pre-fill if editing
        if (existing != null) {
            etName.setText(existing.getName());
            etAge.setText(String.valueOf(existing.getAge()));
            etAllergies.setText(String.join(", ", existing.getAllergies()));
            etConditions.setText(String.join(", ", existing.getConditions()));
            etMedications.setText(String.join(", ", existing.getMedications()));
            etEmergencyContact.setText(existing.getEmergencyContact());
            etEmergencyPhone.setText(existing.getEmergencyPhone());
            etNotes.setText(existing.getNotes());
            
            for (int i = 0; i < relationships.length; i++) {
                if (relationships[i].equals(existing.getRelationship())) {
                    spinnerRelationship.setSelection(i);
                    break;
                }
            }
            
            for (int i = 0; i < genders.length; i++) {
                if (genders[i].equals(existing.getGender())) {
                    spinnerGender.setSelection(i);
                    break;
                }
            }
            
            for (int i = 0; i < bloodTypes.length; i++) {
                if (bloodTypes[i].equals(existing.getBloodType())) {
                    spinnerBloodType.setSelection(i);
                    break;
                }
            }
        }
        
        AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle(existing == null ? "Add Family Member" : "Edit Member")
            .setView(dialogView)
            .setPositiveButton("Save", null)
            .setNegativeButton("Cancel", null)
            .create();
        
        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String name = etName.getText().toString().trim();
                String ageStr = etAge.getText().toString().trim();
                
                if (name.isEmpty() || ageStr.isEmpty()) {
                    Toast.makeText(this, "Name and age are required", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                FamilyMember member = existing != null ? existing : new FamilyMember();
                member.setName(name);
                member.setRelationship(relationships[spinnerRelationship.getSelectedItemPosition()]);
                member.setAge(Integer.parseInt(ageStr));
                member.setGender(genders[spinnerGender.getSelectedItemPosition()]);
                
                int bloodIdx = spinnerBloodType.getSelectedItemPosition();
                member.setBloodType(bloodIdx > 0 ? bloodTypes[bloodIdx] : "");
                
                // Parse comma-separated lists
                String allergiesStr = etAllergies.getText().toString().trim();
                if (!allergiesStr.isEmpty()) {
                    member.setAllergies(Arrays.asList(allergiesStr.split("\\s*,\\s*")));
                }
                
                String conditionsStr = etConditions.getText().toString().trim();
                if (!conditionsStr.isEmpty()) {
                    member.setConditions(Arrays.asList(conditionsStr.split("\\s*,\\s*")));
                }
                
                String medicationsStr = etMedications.getText().toString().trim();
                if (!medicationsStr.isEmpty()) {
                    member.setMedications(Arrays.asList(medicationsStr.split("\\s*,\\s*")));
                }
                
                member.setEmergencyContact(etEmergencyContact.getText().toString().trim());
                member.setEmergencyPhone(etEmergencyPhone.getText().toString().trim());
                member.setNotes(etNotes.getText().toString().trim());
                
                if (existing == null) {
                    members.add(member);
                }
                
                saveMembers();
                updateEmptyState();
                Toast.makeText(this, "Member saved", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
        });
        
        dialog.show();
    }
    
    private void showMemberDetails(FamilyMember member) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_member_details, null);
        
        TextView tvName = dialogView.findViewById(R.id.tvName);
        TextView tvRelationship = dialogView.findViewById(R.id.tvRelationship);
        TextView tvAge = dialogView.findViewById(R.id.tvAge);
        TextView tvGender = dialogView.findViewById(R.id.tvGender);
        TextView tvBloodType = dialogView.findViewById(R.id.tvBloodType);
        TextView tvAllergies = dialogView.findViewById(R.id.tvAllergies);
        TextView tvConditions = dialogView.findViewById(R.id.tvConditions);
        TextView tvMedications = dialogView.findViewById(R.id.tvMedications);
        TextView tvEmergencyContact = dialogView.findViewById(R.id.tvEmergencyContact);
        TextView tvNotes = dialogView.findViewById(R.id.tvNotes);
        
        tvName.setText(member.getName());
        tvRelationship.setText(member.getRelationship());
        tvAge.setText(member.getAge() + " years");
        tvGender.setText(member.getGender());
        tvBloodType.setText(member.getBloodType() != null && !member.getBloodType().isEmpty() 
            ? member.getBloodType() : "Not specified");
        tvAllergies.setText(member.getAllergiesString());
        tvConditions.setText(member.getConditionsString());
        tvMedications.setText(member.getMedicationsString());
        tvEmergencyContact.setText(member.getEmergencyContact() != null && !member.getEmergencyContact().isEmpty()
            ? member.getEmergencyContact() + " (" + member.getEmergencyPhone() + ")" : "Not specified");
        tvNotes.setText(member.getNotes() != null && !member.getNotes().isEmpty() 
            ? member.getNotes() : "No notes");
        
        new AlertDialog.Builder(this)
            .setTitle("Member Details")
            .setView(dialogView)
            .setPositiveButton("Edit", (d, w) -> showAddMemberDialog(member))
            .setNegativeButton("Close", null)
            .show();
    }
    
    @Override
    public void onMemberClick(FamilyMember member) {
        showMemberDetails(member);
    }
    
    @Override
    public void onMemberLongClick(FamilyMember member) {
        new AlertDialog.Builder(this)
            .setTitle("Delete Member")
            .setMessage("Are you sure you want to remove " + member.getName() + " from family profiles?")
            .setPositiveButton("Delete", (d, w) -> {
                members.remove(member);
                saveMembers();
                updateEmptyState();
                Toast.makeText(this, "Member removed", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
}
