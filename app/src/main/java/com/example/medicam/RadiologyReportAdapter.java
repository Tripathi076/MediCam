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

import java.util.ArrayList;
import java.util.List;

public class RadiologyReportAdapter extends RecyclerView.Adapter<RadiologyReportAdapter.ReportViewHolder> {

    private Context context;
    private List<RadiologyReport> reports;
    private List<RadiologyReport> filteredReports;

    public RadiologyReportAdapter(Context context, List<RadiologyReport> reports) {
        this.context = context;
        this.reports = reports;
        this.filteredReports = new ArrayList<>(reports);
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_radiology_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        RadiologyReport report = filteredReports.get(position);

        holder.tvCenterName.setText(report.getCenterName());
        holder.tvScanType.setText(report.getScanType());
        holder.tvDate.setText(report.getScanDate());
        holder.tvDoctorName.setText(report.getDoctorName());

        // Load report image thumbnail
        if (report.getReportImageUri() != null && !report.getReportImageUri().isEmpty()) {
            try {
                Uri imageUri = Uri.parse(report.getReportImageUri());
                holder.ivReportThumbnail.setImageURI(imageUri);
            } catch (Exception e) {
                holder.ivReportThumbnail.setImageResource(R.drawable.radiology_illustration);
            }
        } else {
            holder.ivReportThumbnail.setImageResource(R.drawable.radiology_illustration);
        }

        // Click listener to open report detail
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RadiologyDetailActivity.class);
            intent.putExtra("CENTER_NAME", report.getCenterName());
            intent.putExtra("SCAN_TYPE", report.getScanType());
            intent.putExtra("SCAN_DATE", report.getScanDate());
            intent.putExtra("DOCTOR_NAME", report.getDoctorName());
            intent.putExtra("PATIENT_NAME", report.getPatientName());
            intent.putExtra("FILE_URI", report.getReportImageUri());
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
            for (RadiologyReport report : reports) {
                if (report.getCategory() != null && report.getCategory().equals(category)) {
                    filteredReports.add(report);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void updateReports(List<RadiologyReport> newReports) {
        this.reports = newReports;
        this.filteredReports = new ArrayList<>(newReports);
        notifyDataSetChanged();
    }

    public static class ReportViewHolder extends RecyclerView.ViewHolder {
        ImageView ivReportThumbnail;
        TextView tvCenterName, tvScanType, tvDate, tvDoctorName;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            ivReportThumbnail = itemView.findViewById(R.id.iconContainer);
            tvCenterName = itemView.findViewById(R.id.tvCenterName);
            tvScanType = itemView.findViewById(R.id.tvScanType);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
        }
    }
}
