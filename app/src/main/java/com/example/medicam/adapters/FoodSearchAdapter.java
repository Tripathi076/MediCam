package com.example.medicam.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicam.R;
import com.example.medicam.models.FoodItem;

import java.util.List;

public class FoodSearchAdapter extends RecyclerView.Adapter<FoodSearchAdapter.ViewHolder> {

    private List<FoodItem> foodItems;
    private OnFoodItemClickListener listener;

    public interface OnFoodItemClickListener {
        void onFoodItemClick(FoodItem foodItem);
    }

    public FoodSearchAdapter(List<FoodItem> foodItems, OnFoodItemClickListener listener) {
        this.foodItems = foodItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodItem item = foodItems.get(position);
        
        holder.tvFoodName.setText(item.getName());
        holder.tvCategory.setText(item.getCategory());
        holder.tvCalories.setText(item.getCaloriesPer100g() + " cal/100g");
        
        String macros = String.format("P: %.1fg | C: %.1fg | F: %.1fg", 
                item.getProteinPer100g(), item.getCarbsPer100g(), item.getFatPer100g());
        holder.tvMacros.setText(macros);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFoodItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodItems.size();
    }

    public void updateData(List<FoodItem> newFoodItems) {
        this.foodItems = newFoodItems;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFoodName;
        TextView tvCategory;
        TextView tvCalories;
        TextView tvMacros;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvCalories = itemView.findViewById(R.id.tvCalories);
            tvMacros = itemView.findViewById(R.id.tvMacros);
        }
    }
}
