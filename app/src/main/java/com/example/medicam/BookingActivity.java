package com.example.medicam;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

public class BookingActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        
        // Doctor Appointment Card
        MaterialCardView cardDoctorAppointment = findViewById(R.id.cardDoctorAppointment);
        cardDoctorAppointment.setOnClickListener(v -> {
            startActivity(new Intent(BookingActivity.this, AppointmentSchedulerActivity.class));
        });
        
        // Lab Test Booking Card
        MaterialCardView cardLabTest = findViewById(R.id.cardLabTest);
        cardLabTest.setOnClickListener(v -> {
            startActivity(new Intent(BookingActivity.this, LabTestBookingActivity.class));
        });
    }
}
