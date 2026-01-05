package com.example.medicam.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicam.R;
import com.example.medicam.models.Appointment;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {
    
    private List<Appointment> appointments;
    private OnAppointmentClickListener listener;
    
    public interface OnAppointmentClickListener {
        void onEdit(Appointment appointment);
        void onDelete(Appointment appointment);
        void onStatusChange(Appointment appointment, String newStatus);
    }
    
    public AppointmentAdapter(List<Appointment> appointments, OnAppointmentClickListener listener) {
        this.appointments = appointments;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_appointment, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);
        
        holder.tvDoctorName.setText("Dr. " + appointment.getDoctorName());
        holder.tvSpecialty.setText(appointment.getSpecialty());
        holder.tvHospital.setText(appointment.getHospitalName());
        
        // Format date
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault());
        holder.tvDate.setText(dateFormat.format(new Date(appointment.getAppointmentDate())));
        holder.tvTime.setText(appointment.getAppointmentTime());
        
        // Status indicator
        switch (appointment.getStatus()) {
            case "completed":
                holder.cardAppointment.setStrokeColor(0xFF4CAF50);
                holder.tvStatus.setText("✓ Completed");
                holder.tvStatus.setTextColor(0xFF4CAF50);
                break;
            case "cancelled":
                holder.cardAppointment.setStrokeColor(0xFFF44336);
                holder.tvStatus.setText("✗ Cancelled");
                holder.tvStatus.setTextColor(0xFFF44336);
                break;
            default:
                holder.cardAppointment.setStrokeColor(0xFF2196F3);
                holder.tvStatus.setText("● Scheduled");
                holder.tvStatus.setTextColor(0xFF2196F3);
        }
        
        // Reminder icon
        holder.ivReminder.setVisibility(appointment.isReminderEnabled() ? View.VISIBLE : View.GONE);
        
        // Click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(appointment);
            }
        });
        
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onDelete(appointment);
            }
            return true;
        });
        
        holder.tvStatus.setOnClickListener(v -> {
            if (listener != null && appointment.getStatus().equals("scheduled")) {
                listener.onStatusChange(appointment, "completed");
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return appointments.size();
    }
    
    public void updateAppointments(List<Appointment> newAppointments) {
        this.appointments = newAppointments;
        notifyDataSetChanged();
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardAppointment;
        TextView tvDoctorName, tvSpecialty, tvHospital, tvDate, tvTime, tvStatus;
        ImageView ivReminder;
        
        ViewHolder(View view) {
            super(view);
            cardAppointment = view.findViewById(R.id.cardAppointment);
            tvDoctorName = view.findViewById(R.id.tvDoctorName);
            tvSpecialty = view.findViewById(R.id.tvSpecialty);
            tvHospital = view.findViewById(R.id.tvHospital);
            tvDate = view.findViewById(R.id.tvDate);
            tvTime = view.findViewById(R.id.tvTime);
            tvStatus = view.findViewById(R.id.tvStatus);
            ivReminder = view.findViewById(R.id.ivReminder);
        }
    }
}
