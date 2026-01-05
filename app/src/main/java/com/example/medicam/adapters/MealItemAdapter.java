package com.example.medicam.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicam.R;
import com.example.medicam.models.MealItem;

import java.util.List;

public class MealItemAdapter extends RecyclerView.Adapter<MealItemAdapter.ViewHolder> {

    private List<MealItem> mealItems;
    private OnMealItemClickListener listener;

    public interface OnMealItemClickListener {
        void onDeleteClick(MealItem mealItem, int position);
    }

    public MealItemAdapter(List<MealItem> mealItems, OnMealItemClickListener listener) {
        this.mealItems = mealItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MealItem item = mealItems.get(position);
        
        holder.tvFoodName.setText(item.getName());
        holder.tvServing.setText(item.getServingSize());
        holder.tvCalories.setText(item.getCalories() + " cal");
        
        String macros = String.format("P: %.1fg | C: %.1fg | F: %.1fg", 
                item.getProtein(), item.getCarbs(), item.getFat());
        holder.tvMacros.setText(macros);

        // Set meal type icon
        switch (item.getMealType().toLowerCase()) {
            case "breakfast":
                holder.ivMealIcon.setImageResource(R.drawable.ic_breakfast);
                break;
            case "lunch":
                holder.ivMealIcon.setImageResource(R.drawable.ic_lunch);
                break;
            case "dinner":
                holder.ivMealIcon.setImageResource(R.drawable.ic_dinner);
                break;
            case "snack":
                holder.ivMealIcon.setImageResource(R.drawable.ic_snack);
                break;
            default:
                holder.ivMealIcon.setImageResource(R.drawable.ic_food);
                break;
        }

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(item, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mealItems.size();
    }

    public void updateData(List<MealItem> newMealItems) {
        this.mealItems = newMealItems;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivMealIcon;
        TextView tvFoodName;
        TextView tvServing;
        TextView tvCalories;
        TextView tvMacros;
        ImageView btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMealIcon = itemView.findViewById(R.id.ivMealIcon);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvServing = itemView.findViewById(R.id.tvServing);
            tvCalories = itemView.findViewById(R.id.tvCalories);
            tvMacros = itemView.findViewById(R.id.tvMacros);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
