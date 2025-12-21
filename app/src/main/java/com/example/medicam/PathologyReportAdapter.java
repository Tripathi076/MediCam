package com.example.medicam;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class PathologyReportAdapter extends RecyclerView.Adapter<PathologyReportAdapter.ReportViewHolder> {

    private Context context;
    private List<PathologyReport> reports;
    private List<PathologyReport> filteredReports;

    public PathologyReportAdapter(Context context, List<PathologyReport> reports) {
        this.context = context;
        this.reports = reports;
        this.filteredReports = new ArrayList<>(reports);
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pathology_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        PathologyReport report = filteredReports.get(position);
        
        holder.tvLabName.setText(report.getLabName());
        holder.tvTestName.setText(report.getTestName());
        holder.tvDate.setText(report.getCollectionDate());
        holder.tvDoctorName.setText(report.getDoctorName());
        
        // Load report image thumbnail
        if (report.getReportImageUri() != null && !report.getReportImageUri().isEmpty()) {
            try {
                Uri imageUri = Uri.parse(report.getReportImageUri());
                holder.ivReportThumbnail.setImageURI(imageUri);
            } catch (Exception e) {
                holder.ivReportThumbnail.setImageResource(R.drawable.ic_pathology_illustration);
            }
        } else {
            holder.ivReportThumbnail.setImageResource(R.drawable.ic_pathology_illustration);
        }
        
        // Click listener to open report detail
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ReportDetailActivity.class);
            intent.putExtra("LAB_NAME", report.getLabName());
            intent.putExtra("TEST_NAME", report.getTestName());
            intent.putExtra("COLLECTION_DATE", report.getCollectionDate());
            intent.putExtra("DOCTOR_NAME", report.getDoctorName());
            intent.putExtra("PATIENT_NAME", report.getPatientName());
            intent.putExtra("REPORT_IMAGE_URI", report.getReportImageUri());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return filteredReports.size();
    }

    public void filter(String category) {
        filteredReports.clear();
        if (category.equals("All")) {
            filteredReports.addAll(reports);
        } else {
            for (PathologyReport report : reports) {
                if (report.getCategory() != null && report.getCategory().equals(category)) {
                    filteredReports.add(report);
                }
            }
        }
        notifyDataSetChanged();
    }

    public static class ReportViewHolder extends RecyclerView.ViewHolder {
        ImageView ivReportThumbnail;
        TextView tvLabName, tvTestName, tvDate, tvDoctorName;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            ivReportThumbnail = itemView.findViewById(R.id.iconContainer);
            tvLabName = itemView.findViewById(R.id.tvLabName);
            tvTestName = itemView.findViewById(R.id.tvTestType);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
        }
    }
}
