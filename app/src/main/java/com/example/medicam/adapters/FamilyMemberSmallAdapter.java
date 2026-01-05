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

public class FamilyMemberSmallAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_MEMBER = 0;
    private static final int TYPE_ADD_NEW = 1;

    private Context context;
    private List<FamilyMember> familyMembers;
    private OnMemberClickListener listener;
    private boolean showAddNew;

    public interface OnMemberClickListener {
        void onMemberClick(FamilyMember member);
        void onAddNewClick();
    }

    public FamilyMemberSmallAdapter(Context context, List<FamilyMember> familyMembers, 
                                     boolean showAddNew, OnMemberClickListener listener) {
        this.context = context;
        this.familyMembers = familyMembers;
        this.showAddNew = showAddNew;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (showAddNew && position == familyMembers.size()) {
            return TYPE_ADD_NEW;
        }
        return TYPE_MEMBER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ADD_NEW) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_family_member_add, parent, false);
            return new AddNewViewHolder(view);
        }
        View view = LayoutInflater.from(context).inflate(R.layout.item_family_member_small, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MemberViewHolder) {
            FamilyMember member = familyMembers.get(position);
            MemberViewHolder memberHolder = (MemberViewHolder) holder;

            memberHolder.tvName.setText(member.getName());

            if (member.getProfileImageUri() != null && !member.getProfileImageUri().isEmpty()) {
                try {
                    memberHolder.imgProfile.setImageURI(Uri.parse(member.getProfileImageUri()));
                } catch (Exception e) {
                    memberHolder.imgProfile.setImageResource(R.drawable.ic_person);
                }
            } else {
                memberHolder.imgProfile.setImageResource(R.drawable.ic_person);
            }

            memberHolder.cardMember.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMemberClick(member);
                }
            });
        } else if (holder instanceof AddNewViewHolder) {
            AddNewViewHolder addHolder = (AddNewViewHolder) holder;
            addHolder.cardAddNew.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddNewClick();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        int count = familyMembers != null ? familyMembers.size() : 0;
        if (showAddNew) count++;
        return count;
    }

    public void updateMembers(List<FamilyMember> newMembers) {
        this.familyMembers = newMembers;
        notifyDataSetChanged();
    }

    static class MemberViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardMember;
        ImageView imgProfile;
        TextView tvName;

        MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            cardMember = itemView.findViewById(R.id.cardMember);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            tvName = itemView.findViewById(R.id.tvName);
        }
    }

    static class AddNewViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardAddNew;

        AddNewViewHolder(@NonNull View itemView) {
            super(itemView);
            cardAddNew = itemView.findViewById(R.id.cardAddNew);
        }
    }
}
