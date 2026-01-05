package com.example.medicam;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        
        holder.tvLabName.setText(report.getLabName() != null ? report.getLabName() : "");
        holder.tvTestName.setText(report.getTestName() != null ? report.getTestName() : "");
        holder.tvDate.setText(report.getCollectionDate() != null ? report.getCollectionDate() : 
                             (report.getReportDate() != null ? report.getReportDate() : ""));
        holder.tvDoctorName.setText(report.getDoctorName() != null ? report.getDoctorName() : "");
        
        // Set test type (category indicator)
        if (holder.tvTestType != null) {
            String category = report.getCategory();
            if (category != null && category.equals("Radiology")) {
                holder.tvTestType.setText(report.getTestType() != null ? report.getTestType() : "Radiology");
            } else {
                holder.tvTestType.setText("Pathology");
            }
        }
        
        // Click listener to open report detail
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ReportDetailActivity.class);
            intent.putExtra("LAB_NAME", report.getLabName());
            intent.putExtra("TEST_NAME", report.getTestName());
            intent.putExtra("COLLECTION_DATE", report.getCollectionDate() != null ? report.getCollectionDate() : report.getReportDate());
            intent.putExtra("DOCTOR_NAME", report.getDoctorName());
            intent.putExtra("PATIENT_NAME", report.getPatientName());
            intent.putExtra("REPORT_IMAGE_URI", report.getReportImageUri());
            intent.putExtra("CATEGORY", report.getCategory());
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
        View iconContainer;
        TextView tvLabName, tvTestName, tvDate, tvDoctorName, tvTestType;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            iconContainer = itemView.findViewById(R.id.iconContainer);
            tvLabName = itemView.findViewById(R.id.tvLabName);
            tvTestName = itemView.findViewById(R.id.tvTestName);
            tvTestType = itemView.findViewById(R.id.tvTestType);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
        }
    }
}
