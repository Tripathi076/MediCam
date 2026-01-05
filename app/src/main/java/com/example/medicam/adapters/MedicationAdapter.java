package com.example.medicam.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicam.R;
import com.example.medicam.models.Medication;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;

import java.util.List;

public class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.ViewHolder> {
    
    private Context context;
    private List<Medication> medications;
    private OnMedicationClickListener listener;
    
    public interface OnMedicationClickListener {
        void onMedicationClick(Medication medication);
        void onTakeMedication(Medication medication);
        void onEditMedication(Medication medication);
    }

    public MedicationAdapter(Context context, List<Medication> medications, OnMedicationClickListener listener) {
        this.context = context;
        this.medications = medications;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_medication, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Medication medication = medications.get(position);
        
        holder.tvMedicationName.setText(medication.getName());
        holder.tvDosage.setText(medication.getDosageDisplay());
        holder.tvFrequency.setText(medication.getFrequency());
        
        // Show reminder times
        if (medication.getReminderTimes() != null && !medication.getReminderTimes().isEmpty()) {
            StringBuilder times = new StringBuilder();
            for (int i = 0; i < medication.getReminderTimes().size(); i++) {
                if (i > 0) times.append(", ");
                times.append(medication.getReminderTimes().get(i));
            }
            holder.tvReminderTimes.setText(times.toString());
            holder.tvReminderTimes.setVisibility(View.VISIBLE);
        } else {
            holder.tvReminderTimes.setVisibility(View.GONE);
        }
        
        // Show instructions
        if (medication.getInstructions() != null && !medication.getInstructions().isEmpty()) {
            holder.tvInstructions.setText(medication.getInstructions());
            holder.tvInstructions.setVisibility(View.VISIBLE);
        } else {
            holder.tvInstructions.setVisibility(View.GONE);
        }
        
        // Show refill warning
        if (medication.needsRefill()) {
            holder.chipRefill.setVisibility(View.VISIBLE);
            holder.chipRefill.setText("Refill needed: " + medication.getPillsRemaining() + " left");
        } else {
            holder.chipRefill.setVisibility(View.GONE);
        }
        
        // Set card color indicator
        if (medication.getColor() != 0) {
            holder.colorIndicator.setBackgroundColor(medication.getColor());
        }
        
        // Click listeners
        holder.cardMedication.setOnClickListener(v -> {
            if (listener != null) listener.onMedicationClick(medication);
        });
        
        holder.btnTake.setOnClickListener(v -> {
            if (listener != null) listener.onTakeMedication(medication);
        });
        
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditMedication(medication);
        });
    }

    @Override
    public int getItemCount() {
        return medications != null ? medications.size() : 0;
    }

    public void updateMedications(List<Medication> newMedications) {
        this.medications = newMedications;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardMedication;
        View colorIndicator;
        TextView tvMedicationName, tvDosage, tvFrequency, tvReminderTimes, tvInstructions;
        Chip chipRefill;
        ImageView btnTake, btnEdit;

        ViewHolder(View itemView) {
            super(itemView);
            cardMedication = itemView.findViewById(R.id.cardMedication);
            colorIndicator = itemView.findViewById(R.id.colorIndicator);
            tvMedicationName = itemView.findViewById(R.id.tvMedicationName);
            tvDosage = itemView.findViewById(R.id.tvDosage);
            tvFrequency = itemView.findViewById(R.id.tvFrequency);
            tvReminderTimes = itemView.findViewById(R.id.tvReminderTimes);
            tvInstructions = itemView.findViewById(R.id.tvInstructions);
            chipRefill = itemView.findViewById(R.id.chipRefill);
            btnTake = itemView.findViewById(R.id.btnTake);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}
