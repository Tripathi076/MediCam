package com.example.medicam.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicam.R;
import com.example.medicam.models.FamilyMember;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class FamilyDetailsAdapter extends RecyclerView.Adapter<FamilyDetailsAdapter.ViewHolder> {

    private Context context;
    private List<FamilyMember> familyMembers;
    private OnFamilyMemberClickListener listener;

    public interface OnFamilyMemberClickListener {
        void onMemberClick(FamilyMember member);
    }

    public FamilyDetailsAdapter(Context context, List<FamilyMember> familyMembers, 
                                 OnFamilyMemberClickListener listener) {
        this.context = context;
        this.familyMembers = familyMembers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_family_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FamilyMember member = familyMembers.get(position);

        holder.tvName.setText(member.getName() != null ? member.getName() : "Unknown");
        holder.tvRelation.setText(member.getRelationship() != null ? member.getRelationship() : "Family");

        if (member.getProfileImageUri() != null && !member.getProfileImageUri().isEmpty()) {
            try {
                holder.imgProfile.setImageURI(Uri.parse(member.getProfileImageUri()));
            } catch (Exception e) {
                holder.imgProfile.setImageResource(R.drawable.ic_person);
            }
        } else {
            holder.imgProfile.setImageResource(R.drawable.ic_person);
        }

        holder.cardMember.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMemberClick(member);
            }
        });
    }

    @Override
    public int getItemCount() {
        return familyMembers != null ? familyMembers.size() : 0;
    }

    public void updateMembers(List<FamilyMember> newMembers) {
        this.familyMembers = newMembers;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardMember;
        ImageView imgProfile;
        TextView tvName, tvRelation;
        ImageView imgChevron;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardMember = itemView.findViewById(R.id.cardMember);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            tvName = itemView.findViewById(R.id.tvName);
            tvRelation = itemView.findViewById(R.id.tvRelation);
            imgChevron = itemView.findViewById(R.id.imgChevron);
        }
    }
}
