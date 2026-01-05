package com.example.medicam;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicam.models.LabBooking;
import com.example.medicam.models.LabTest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class LabTestBookingActivity extends AppCompatActivity {
    
    private static final String PREFS_NAME = "lab_booking_prefs";
    private static final String KEY_BOOKINGS = "lab_bookings";
    
    private TabLayout tabLayout;
    private LinearLayout layoutTests;
    private RecyclerView recyclerBookings;
    private TextView tvEmptyBookings;
    
    private List<LabTest> availableTests = new ArrayList<>();
    private List<LabBooking> bookings = new ArrayList<>();
    private String selectedCategory = "All";
    
    private SharedPreferences prefs;
    private Gson gson = new Gson();
    
    private final String[] timeSlots = {
        "7:00 AM - 8:00 AM", "8:00 AM - 9:00 AM", "9:00 AM - 10:00 AM",
        "10:00 AM - 11:00 AM", "11:00 AM - 12:00 PM", "2:00 PM - 3:00 PM",
        "3:00 PM - 4:00 PM", "4:00 PM - 5:00 PM"
    };
    
    private final String[] labNames = {
        "City Diagnostics", "HealthFirst Labs", "MedScan Center", 
        "PathCare Laboratory", "DiagnoPlus"
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab_test_booking);
        
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        
        initializeViews();
        initializeTestCatalog();
        loadBookings();
        setupTabs();
        displayTests();
    }
    
    private void initializeViews() {
        tabLayout = findViewById(R.id.tabLayout);
        layoutTests = findViewById(R.id.layoutTests);
        recyclerBookings = findViewById(R.id.recyclerBookings);
        tvEmptyBookings = findViewById(R.id.tvEmptyBookings);
        
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        
        ChipGroup chipGroup = findViewById(R.id.chipGroupCategories);
        String[] categories = {"All", "Blood", "Urine", "Imaging", "Cardiac", "Diabetes"};
        
        for (String cat : categories) {
            Chip chip = new Chip(this);
            chip.setText(cat);
            chip.setCheckable(true);
            if (cat.equals("All")) chip.setChecked(true);
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedCategory = cat;
                    displayTests();
                }
            });
            chipGroup.addView(chip);
        }
    }
    
    private void initializeTestCatalog() {
        availableTests.clear();
        
        // Blood Tests
        availableTests.add(new LabTest("Complete Blood Count (CBC)", "CBC001", "Blood",
            "Measures red blood cells, white blood cells, and platelets", 350, 
            "No special preparation required", 4));
        availableTests.add(new LabTest("Lipid Profile", "LIP001", "Blood",
            "Measures cholesterol and triglycerides", 600,
            "Fasting for 10-12 hours required", 6));
        availableTests.add(new LabTest("Liver Function Test (LFT)", "LFT001", "Blood",
            "Assesses liver health and function", 750,
            "Fasting for 8-10 hours recommended", 6));
        availableTests.add(new LabTest("Kidney Function Test (KFT)", "KFT001", "Blood",
            "Evaluates kidney health", 650,
            "No special preparation required", 6));
        availableTests.add(new LabTest("Thyroid Profile (T3, T4, TSH)", "THY001", "Blood",
            "Measures thyroid hormone levels", 800,
            "No fasting required", 8));
        availableTests.add(new LabTest("Vitamin D Test", "VIT001", "Blood",
            "Measures Vitamin D levels", 1200,
            "No fasting required", 24));
        availableTests.add(new LabTest("Vitamin B12 Test", "VIT002", "Blood",
            "Measures Vitamin B12 levels", 900,
            "No fasting required", 24));
        
        // Urine Tests
        availableTests.add(new LabTest("Urine Routine & Microscopy", "URI001", "Urine",
            "Basic urine analysis", 200,
            "Collect midstream urine sample", 4));
        availableTests.add(new LabTest("Urine Culture", "URI002", "Urine",
            "Detects bacterial infections", 500,
            "Collect early morning sample", 48));
        
        // Diabetes Tests
        availableTests.add(new LabTest("Fasting Blood Sugar (FBS)", "DIA001", "Diabetes",
            "Measures blood sugar after fasting", 120,
            "Fasting for 8-10 hours required", 2));
        availableTests.add(new LabTest("HbA1c (Glycated Hemoglobin)", "DIA002", "Diabetes",
            "Average blood sugar over 3 months", 550,
            "No fasting required", 6));
        availableTests.add(new LabTest("Post Prandial Blood Sugar", "DIA003", "Diabetes",
            "Blood sugar 2 hours after meal", 150,
            "Test 2 hours after eating", 2));
        
        // Cardiac Tests
        availableTests.add(new LabTest("ECG (Electrocardiogram)", "CAR001", "Cardiac",
            "Records heart's electrical activity", 300,
            "No special preparation", 1));
        availableTests.add(new LabTest("Cardiac Markers (Troponin)", "CAR002", "Cardiac",
            "Detects heart muscle damage", 1500,
            "No special preparation", 4));
        
        // Imaging
        availableTests.add(new LabTest("Chest X-Ray", "IMG001", "Imaging",
            "Imaging of chest and lungs", 400,
            "Remove metal objects", 2));
        availableTests.add(new LabTest("Ultrasound Abdomen", "IMG002", "Imaging",
            "Imaging of abdominal organs", 1200,
            "Fasting for 6 hours, full bladder", 24));
    }
    
    private void loadBookings() {
        String json = prefs.getString(KEY_BOOKINGS, "[]");
        Type type = new TypeToken<List<LabBooking>>(){}.getType();
        bookings = gson.fromJson(json, type);
        if (bookings == null) bookings = new ArrayList<>();
    }
    
    private void saveBookings() {
        String json = gson.toJson(bookings);
        prefs.edit().putString(KEY_BOOKINGS, json).apply();
    }
    
    private void setupTabs() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    layoutTests.setVisibility(View.VISIBLE);
                    recyclerBookings.setVisibility(View.GONE);
                    tvEmptyBookings.setVisibility(View.GONE);
                    displayTests();
                } else {
                    layoutTests.setVisibility(View.GONE);
                    displayBookings();
                }
            }
            
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }
    
    private void displayTests() {
        layoutTests.removeAllViews();
        
        List<LabTest> filteredTests = selectedCategory.equals("All") 
            ? availableTests 
            : availableTests.stream()
                .filter(t -> t.getCategory().equals(selectedCategory))
                .collect(Collectors.toList());
        
        for (LabTest test : filteredTests) {
            View testCard = LayoutInflater.from(this).inflate(R.layout.item_lab_test, layoutTests, false);
            
            TextView tvTestName = testCard.findViewById(R.id.tvTestName);
            TextView tvCategory = testCard.findViewById(R.id.tvCategory);
            TextView tvDescription = testCard.findViewById(R.id.tvDescription);
            TextView tvPrice = testCard.findViewById(R.id.tvPrice);
            TextView tvReportTime = testCard.findViewById(R.id.tvReportTime);
            MaterialButton btnBook = testCard.findViewById(R.id.btnBook);
            
            tvTestName.setText(test.getTestName());
            tvCategory.setText(test.getCategory());
            tvDescription.setText(test.getDescription());
            tvPrice.setText("₹" + (int)test.getPrice());
            tvReportTime.setText("Report in " + test.getFormattedReportTime());
            
            btnBook.setOnClickListener(v -> showBookingDialog(test));
            testCard.setOnClickListener(v -> showTestDetails(test));
            
            layoutTests.addView(testCard);
        }
    }
    
    private void displayBookings() {
        if (bookings.isEmpty()) {
            recyclerBookings.setVisibility(View.GONE);
            tvEmptyBookings.setVisibility(View.VISIBLE);
        } else {
            recyclerBookings.setVisibility(View.VISIBLE);
            tvEmptyBookings.setVisibility(View.GONE);
            
            // Simple list display
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerBookings.setLayoutManager(layoutManager);
            
            // Use a simple adapter for bookings
            recyclerBookings.setAdapter(new BookingAdapter());
        }
    }
    
    private void showTestDetails(LabTest test) {
        String details = "Test Code: " + test.getTestCode() + "\n\n" +
            "Category: " + test.getCategory() + "\n\n" +
            "Description:\n" + test.getDescription() + "\n\n" +
            "Preparation:\n" + test.getPreparationInstructions() + "\n\n" +
            "Report Time: " + test.getFormattedReportTime() + "\n\n" +
            "Price: ₹" + (int)test.getPrice();
        
        new AlertDialog.Builder(this)
            .setTitle(test.getTestName())
            .setMessage(details)
            .setPositiveButton("Book Now", (d, w) -> showBookingDialog(test))
            .setNegativeButton("Close", null)
            .show();
    }
    
    private void showBookingDialog(LabTest test) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_book_lab_test, null);
        
        TextView tvTestName = dialogView.findViewById(R.id.tvTestName);
        TextView tvPrice = dialogView.findViewById(R.id.tvPrice);
        EditText etPatientName = dialogView.findViewById(R.id.etPatientName);
        Spinner spinnerLab = dialogView.findViewById(R.id.spinnerLab);
        TextView tvSelectedDate = dialogView.findViewById(R.id.tvSelectedDate);
        MaterialButton btnSelectDate = dialogView.findViewById(R.id.btnSelectDate);
        Spinner spinnerTimeSlot = dialogView.findViewById(R.id.spinnerTimeSlot);
        CheckBox cbHomeCollection = dialogView.findViewById(R.id.cbHomeCollection);
        EditText etAddress = dialogView.findViewById(R.id.etAddress);
        LinearLayout layoutAddress = dialogView.findViewById(R.id.layoutAddress);
        
        tvTestName.setText(test.getTestName());
        tvPrice.setText("₹" + (int)test.getPrice());
        
        ArrayAdapter<String> labAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_dropdown_item, labNames);
        spinnerLab.setAdapter(labAdapter);
        
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_dropdown_item, timeSlots);
        spinnerTimeSlot.setAdapter(timeAdapter);
        
        final Calendar selectedDate = Calendar.getInstance();
        selectedDate.add(Calendar.DAY_OF_MONTH, 1); // Default to tomorrow
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault());
        tvSelectedDate.setText(dateFormat.format(selectedDate.getTime()));
        
        btnSelectDate.setOnClickListener(v -> {
            DatePickerDialog picker = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(year, month, dayOfMonth);
                    tvSelectedDate.setText(dateFormat.format(selectedDate.getTime()));
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH));
            picker.getDatePicker().setMinDate(System.currentTimeMillis() + 86400000); // Tomorrow
            picker.show();
        });
        
        cbHomeCollection.setOnCheckedChangeListener((buttonView, isChecked) -> {
            layoutAddress.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });
        
        new AlertDialog.Builder(this)
            .setTitle("Book Test")
            .setView(dialogView)
            .setPositiveButton("Confirm Booking", (d, w) -> {
                String patientName = etPatientName.getText().toString().trim();
                if (patientName.isEmpty()) {
                    Toast.makeText(this, "Please enter patient name", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                LabBooking booking = new LabBooking();
                booking.setTestId(test.getId());
                booking.setTestName(test.getTestName());
                booking.setPatientName(patientName);
                booking.setLabName(labNames[spinnerLab.getSelectedItemPosition()]);
                booking.setBookingDate(selectedDate.getTimeInMillis());
                booking.setTimeSlot(timeSlots[spinnerTimeSlot.getSelectedItemPosition()]);
                booking.setHomeCollection(cbHomeCollection.isChecked());
                booking.setAddress(etAddress.getText().toString().trim());
                booking.setTotalAmount(test.getPrice());
                booking.setStatus("confirmed");
                
                bookings.add(0, booking);
                saveBookings();
                
                Toast.makeText(this, "Booking confirmed!", Toast.LENGTH_SHORT).show();
                
                // Switch to bookings tab
                tabLayout.selectTab(tabLayout.getTabAt(1));
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
    
    // Simple RecyclerView Adapter for bookings
    private class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {
        
        @Override
        public ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lab_booking, parent, false);
            return new ViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            LabBooking booking = bookings.get(position);
            
            holder.tvTestName.setText(booking.getTestName());
            holder.tvLabName.setText(booking.getLabName());
            holder.tvPatient.setText("Patient: " + booking.getPatientName());
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            holder.tvDate.setText(dateFormat.format(booking.getBookingDate()) + " • " + booking.getTimeSlot());
            
            holder.tvStatus.setText(booking.getStatusDisplayText());
            int statusColor = booking.getStatus().equals("completed") ? 0xFF4CAF50 :
                             booking.getStatus().equals("cancelled") ? 0xFFF44336 : 0xFF2196F3;
            holder.tvStatus.setTextColor(statusColor);
            
            if (booking.isHomeCollection()) {
                holder.tvCollection.setText("🏠 Home Collection");
                holder.tvCollection.setVisibility(View.VISIBLE);
            } else {
                holder.tvCollection.setText("🏥 Lab Visit");
                holder.tvCollection.setVisibility(View.VISIBLE);
            }
            
            holder.itemView.setOnLongClickListener(v -> {
                showBookingOptions(booking, position);
                return true;
            });
        }
        
        @Override
        public int getItemCount() {
            return bookings.size();
        }
        
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTestName, tvLabName, tvPatient, tvDate, tvStatus, tvCollection;
            
            ViewHolder(View view) {
                super(view);
                tvTestName = view.findViewById(R.id.tvTestName);
                tvLabName = view.findViewById(R.id.tvLabName);
                tvPatient = view.findViewById(R.id.tvPatient);
                tvDate = view.findViewById(R.id.tvDate);
                tvStatus = view.findViewById(R.id.tvStatus);
                tvCollection = view.findViewById(R.id.tvCollection);
            }
        }
    }
    
    private void showBookingOptions(LabBooking booking, int position) {
        String[] options = {"Mark as Completed", "Cancel Booking", "Delete"};
        
        new AlertDialog.Builder(this)
            .setTitle(booking.getTestName())
            .setItems(options, (d, which) -> {
                switch (which) {
                    case 0:
                        booking.setStatus("completed");
                        saveBookings();
                        displayBookings();
                        Toast.makeText(this, "Marked as completed", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        booking.setStatus("cancelled");
                        saveBookings();
                        displayBookings();
                        Toast.makeText(this, "Booking cancelled", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        bookings.remove(position);
                        saveBookings();
                        displayBookings();
                        Toast.makeText(this, "Booking deleted", Toast.LENGTH_SHORT).show();
                        break;
                }
            })
            .show();
    }
}
