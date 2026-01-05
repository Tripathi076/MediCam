package com.example.medicam.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicam.R;
import com.example.medicam.models.FamilyMember;

import java.util.List;

public class FamilyMemberAdapter extends RecyclerView.Adapter<FamilyMemberAdapter.ViewHolder> {
    
    private List<FamilyMember> members;
    private OnMemberClickListener listener;
    
    public interface OnMemberClickListener {
        void onMemberClick(FamilyMember member);
        void onMemberLongClick(FamilyMember member);
    }
    
    public FamilyMemberAdapter(List<FamilyMember> members, OnMemberClickListener listener) {
        this.members = members;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_family_member, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FamilyMember member = members.get(position);
        
        holder.tvName.setText(member.getName());
        holder.tvRelationship.setText(member.getRelationship());
        holder.tvAge.setText(member.getAge() + " years, " + member.getGender());
        
        // Blood type badge
        if (member.getBloodType() != null && !member.getBloodType().isEmpty()) {
            holder.tvBloodType.setText(member.getBloodType());
            holder.tvBloodType.setVisibility(View.VISIBLE);
        } else {
            holder.tvBloodType.setVisibility(View.GONE);
        }
        
        // Medical info summary
        StringBuilder summary = new StringBuilder();
        if (member.getConditions() != null && !member.getConditions().isEmpty()) {
            summary.append(member.getConditions().size()).append(" condition(s)");
        }
        if (member.getAllergies() != null && !member.getAllergies().isEmpty()) {
            if (summary.length() > 0) summary.append(" • ");
            summary.append(member.getAllergies().size()).append(" allergy(ies)");
        }
        if (member.getMedications() != null && !member.getMedications().isEmpty()) {
            if (summary.length() > 0) summary.append(" • ");
            summary.append(member.getMedications().size()).append(" medication(s)");
        }
        holder.tvMedicalSummary.setText(summary.length() > 0 ? summary.toString() : "No medical info recorded");
        
        // Profile icon based on gender
        int iconRes = R.drawable.ic_person;
        if ("Male".equals(member.getGender())) {
            iconRes = R.drawable.ic_person_male;
        } else if ("Female".equals(member.getGender())) {
            iconRes = R.drawable.ic_person_female;
        }
        holder.ivProfile.setImageResource(iconRes);
        
        // Click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onMemberClick(member);
        });
        
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) listener.onMemberLongClick(member);
            return true;
        });
    }
    
    @Override
    public int getItemCount() {
        return members.size();
    }
    
    public void updateMembers(List<FamilyMember> newMembers) {
        this.members = newMembers;
        notifyDataSetChanged();
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfile;
        TextView tvName, tvRelationship, tvAge, tvBloodType, tvMedicalSummary;
        
        ViewHolder(View view) {
            super(view);
            ivProfile = view.findViewById(R.id.ivProfile);
            tvName = view.findViewById(R.id.tvName);
            tvRelationship = view.findViewById(R.id.tvRelationship);
            tvAge = view.findViewById(R.id.tvAge);
            tvBloodType = view.findViewById(R.id.tvBloodType);
            tvMedicalSummary = view.findViewById(R.id.tvMedicalSummary);
        }
    }
}
