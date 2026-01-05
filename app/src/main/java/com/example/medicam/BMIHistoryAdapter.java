package com.example.medicam;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BMIHistoryAdapter extends RecyclerView.Adapter<BMIHistoryAdapter.ViewHolder> {
    private List<BMIRecord> bmiRecords;

    public BMIHistoryAdapter(List<BMIRecord> bmiRecords) {
        this.bmiRecords = bmiRecords;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bmi_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BMIRecord record = bmiRecords.get(position);
        
        holder.tvDate.setText("Date: " + record.getDate());
        holder.tvGender.setText("Gender: " + record.getGender());
        holder.tvWeight.setText("Weight: " + String.format("%.2f", record.getWeight()) + " kg");
        holder.tvHeight.setText("Height: " + String.format("%.2f", record.getHeight()) + " cm");
        holder.tvBMI.setText("BMI: " + String.format("%.2f", record.getBmiValue()));
        holder.tvCategory.setText("Category: " + record.getCategory());
    }

    @Override
    public int getItemCount() {
        return bmiRecords.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvGender, tvWeight, tvHeight, tvBMI, tvCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvGender = itemView.findViewById(R.id.tvGender);
            tvWeight = itemView.findViewById(R.id.tvWeight);
            tvHeight = itemView.findViewById(R.id.tvHeight);
            tvBMI = itemView.findViewById(R.id.tvBMI);
            tvCategory = itemView.findViewById(R.id.tvCategory);
        }
    }
}
