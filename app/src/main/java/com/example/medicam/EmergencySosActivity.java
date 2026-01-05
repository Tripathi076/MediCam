package com.example.medicam;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicam.models.EmergencyContact;
import com.example.medicam.models.MedicalID;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class EmergencySosActivity extends AppCompatActivity {

    private RecyclerView rvContacts;
    private LinearLayout emptyContactsState;
    private TextView tvMedicalIDStatus;

    private List<EmergencyContact> contacts;
    private ContactsAdapter adapter;
    private MedicalID medicalID;

    private SharedPreferences sharedPreferences;
    private Gson gson;
    private FusedLocationProviderClient fusedLocationClient;

    private static final String PREF_NAME = "EmergencyPrefs";
    private static final String KEY_CONTACTS = "emergency_contacts";
    private static final String KEY_MEDICAL_ID = "medical_id";

    private static final int PERMISSION_REQUEST_CALL = 1001;
    private static final int PERMISSION_REQUEST_SMS = 1002;
    private static final int PERMISSION_REQUEST_LOCATION = 1003;

    private Handler sosHandler = new Handler(Looper.getMainLooper());
    private Runnable sosRunnable;
    private boolean sosPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_sos);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        gson = new Gson();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        initViews();
        loadData();
    }

    private void initViews() {
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // SOS Button
        MaterialButton btnSOS = findViewById(R.id.btnSOS);
        setupSOSButton(btnSOS);

        // Quick call buttons
        MaterialCardView cardCall112 = findViewById(R.id.cardCall112);
        cardCall112.setOnClickListener(v -> makeEmergencyCall("112"));

        MaterialCardView cardCall108 = findViewById(R.id.cardCall108);
        cardCall108.setOnClickListener(v -> makeEmergencyCall("108"));

        // Medical ID
        tvMedicalIDStatus = findViewById(R.id.tvMedicalIDStatus);
        MaterialCardView cardMedicalID = findViewById(R.id.cardMedicalID);
        cardMedicalID.setOnClickListener(v -> showMedicalIDDialog());

        // Contacts
        rvContacts = findViewById(R.id.rvContacts);
        emptyContactsState = findViewById(R.id.emptyContactsState);
        rvContacts.setLayoutManager(new LinearLayoutManager(this));

        MaterialButton btnAddContact = findViewById(R.id.btnAddContact);
        btnAddContact.setOnClickListener(v -> showAddContactDialog(null));

        // Share Location
        MaterialCardView cardShareLocation = findViewById(R.id.cardShareLocation);
        cardShareLocation.setOnClickListener(v -> shareLocation());
    }

    private void setupSOSButton(MaterialButton btnSOS) {
        btnSOS.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    sosPressed = true;
                    sosRunnable = () -> {
                        if (sosPressed) {
                            triggerSOS();
                        }
                    };
                    sosHandler.postDelayed(sosRunnable, 3000); // 3 seconds hold
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    sosPressed = false;
                    sosHandler.removeCallbacks(sosRunnable);
                    return true;
            }
            return false;
        });
    }

    private void triggerSOS() {
        // Vibrate
        // Call emergency number
        makeEmergencyCall("112");

        // Send SMS to emergency contacts
        sendEmergencySMS();

        Toast.makeText(this, "🚨 Emergency SOS Activated!", Toast.LENGTH_LONG).show();
    }

    private void loadData() {
        // Load contacts
        String contactsJson = sharedPreferences.getString(KEY_CONTACTS, null);
        if (contactsJson != null) {
            Type type = new TypeToken<List<EmergencyContact>>(){}.getType();
            contacts = gson.fromJson(contactsJson, type);
            if (contacts == null) contacts = new ArrayList<>();
        } else {
            contacts = new ArrayList<>();
        }

        adapter = new ContactsAdapter(contacts);
        rvContacts.setAdapter(adapter);
        updateEmptyState();

        // Load Medical ID
        String medicalJson = sharedPreferences.getString(KEY_MEDICAL_ID, null);
        if (medicalJson != null) {
            medicalID = gson.fromJson(medicalJson, MedicalID.class);
            tvMedicalIDStatus.setText("Tap to view or edit");
        } else {
            medicalID = new MedicalID();
        }
    }

    private void saveContacts() {
        String json = gson.toJson(contacts);
        sharedPreferences.edit().putString(KEY_CONTACTS, json).apply();
    }

    private void saveMedicalID() {
        String json = gson.toJson(medicalID);
        sharedPreferences.edit().putString(KEY_MEDICAL_ID, json).apply();
    }

    private void updateEmptyState() {
        if (contacts.isEmpty()) {
            emptyContactsState.setVisibility(View.VISIBLE);
            rvContacts.setVisibility(View.GONE);
        } else {
            emptyContactsState.setVisibility(View.GONE);
            rvContacts.setVisibility(View.VISIBLE);
        }
    }

    private void makeEmergencyCall(String number) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) 
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, 
                    new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_CALL);
            return;
        }

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + number));
        startActivity(callIntent);
    }

    private void sendEmergencySMS() {
        if (contacts.isEmpty()) return;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) 
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, 
                    new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_SMS);
            return;
        }

        // Get location and send SMS
        getLocationAndSendSMS();
    }

    private void getLocationAndSendSMS() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LOCATION);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            String message = "🚨 EMERGENCY SOS from MediCam!\n\n";
            message += "I need help urgently!\n\n";
            
            if (location != null) {
                message += "📍 Location: https://maps.google.com/?q=" + 
                        location.getLatitude() + "," + location.getLongitude();
            }

            if (medicalID != null && medicalID.getBloodType() != null) {
                message += "\n\nBlood Type: " + medicalID.getBloodType();
            }

            SmsManager smsManager = SmsManager.getDefault();
            for (EmergencyContact contact : contacts) {
                try {
                    ArrayList<String> parts = smsManager.divideMessage(message);
                    smsManager.sendMultipartTextMessage(contact.getPhone(), null, parts, null, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Toast.makeText(this, "Emergency SMS sent to contacts", Toast.LENGTH_SHORT).show();
        });
    }

    private void shareLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LOCATION);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                String locationUrl = "https://maps.google.com/?q=" + 
                        location.getLatitude() + "," + location.getLongitude();
                
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My Location - MediCam");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Here's my current location: " + locationUrl);
                startActivity(Intent.createChooser(shareIntent, "Share Location"));
            } else {
                Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddContactDialog(EmergencyContact editContact) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_contact, null);
        dialog.setContentView(view);

        EditText etName = view.findViewById(R.id.etContactName);
        EditText etPhone = view.findViewById(R.id.etContactPhone);
        EditText etRelationship = view.findViewById(R.id.etRelationship);

        if (editContact != null) {
            etName.setText(editContact.getName());
            etPhone.setText(editContact.getPhone());
            etRelationship.setText(editContact.getRelationship());
        }

        view.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());
        view.findViewById(R.id.btnSave).setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String relationship = etRelationship.getText().toString().trim();

            if (name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Name and phone are required", Toast.LENGTH_SHORT).show();
                return;
            }

            EmergencyContact contact = editContact != null ? editContact : new EmergencyContact();
            contact.setName(name);
            contact.setPhone(phone);
            contact.setRelationship(relationship);

            if (editContact == null) {
                contacts.add(contact);
            }

            saveContacts();
            adapter.notifyDataSetChanged();
            updateEmptyState();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showMedicalIDDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_medical_id, null);
        dialog.setContentView(view);

        EditText etName = view.findViewById(R.id.etFullName);
        EditText etDOB = view.findViewById(R.id.etDOB);
        EditText etBloodType = view.findViewById(R.id.etBloodType);
        EditText etAllergies = view.findViewById(R.id.etAllergies);
        EditText etMedications = view.findViewById(R.id.etMedications);
        EditText etConditions = view.findViewById(R.id.etConditions);
        EditText etNotes = view.findViewById(R.id.etEmergencyNotes);

        if (medicalID != null) {
            etName.setText(medicalID.getFullName());
            etDOB.setText(medicalID.getDateOfBirth());
            etBloodType.setText(medicalID.getBloodType());
            etAllergies.setText(medicalID.getAllergies());
            etMedications.setText(medicalID.getMedications());
            etConditions.setText(medicalID.getMedicalConditions());
            etNotes.setText(medicalID.getEmergencyNotes());
        }

        view.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());
        view.findViewById(R.id.btnSave).setOnClickListener(v -> {
            medicalID.setFullName(etName.getText().toString().trim());
            medicalID.setDateOfBirth(etDOB.getText().toString().trim());
            medicalID.setBloodType(etBloodType.getText().toString().trim());
            medicalID.setAllergies(etAllergies.getText().toString().trim());
            medicalID.setMedications(etMedications.getText().toString().trim());
            medicalID.setMedicalConditions(etConditions.getText().toString().trim());
            medicalID.setEmergencyNotes(etNotes.getText().toString().trim());

            saveMedicalID();
            tvMedicalIDStatus.setText("Tap to view or edit");
            Toast.makeText(this, "Medical ID saved", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case PERMISSION_REQUEST_CALL:
                    makeEmergencyCall("112");
                    break;
                case PERMISSION_REQUEST_SMS:
                    sendEmergencySMS();
                    break;
                case PERMISSION_REQUEST_LOCATION:
                    shareLocation();
                    break;
            }
        }
    }

    // Inner adapter class
    private class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
        private List<EmergencyContact> contacts;

        ContactsAdapter(List<EmergencyContact> contacts) {
            this.contacts = contacts;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_emergency_contact, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            EmergencyContact contact = contacts.get(position);
            holder.tvName.setText(contact.getName());
            holder.tvPhone.setText(contact.getPhone());
            holder.tvRelationship.setText(contact.getRelationship());

            holder.btnCall.setOnClickListener(v -> makeEmergencyCall(contact.getPhone()));
            holder.itemView.setOnClickListener(v -> showAddContactDialog(contact));
            holder.itemView.setOnLongClickListener(v -> {
                new AlertDialog.Builder(EmergencySosActivity.this)
                        .setTitle("Delete Contact")
                        .setMessage("Remove " + contact.getName() + " from emergency contacts?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            contacts.remove(contact);
                            saveContacts();
                            notifyDataSetChanged();
                            updateEmptyState();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                return true;
            });
        }

        @Override
        public int getItemCount() {
            return contacts.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvPhone, tvRelationship;
            ImageView btnCall;

            ViewHolder(View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvContactName);
                tvPhone = itemView.findViewById(R.id.tvContactPhone);
                tvRelationship = itemView.findViewById(R.id.tvRelationship);
                btnCall = itemView.findViewById(R.id.btnCall);
            }
        }
    }
}
