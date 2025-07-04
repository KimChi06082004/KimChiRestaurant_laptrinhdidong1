package com.example.nguyenthikimchi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import androidx.recyclerview.widget.RecyclerView;

import com.example.nguyenthikimchi.R;
import com.example.nguyenthikimchi.models.FoodItem; // ✅ Thêm dòng này để fix lỗi

import java.util.List;

public class FoodLargeAdapter extends RecyclerView.Adapter<FoodLargeAdapter.ViewHolder> {
    private Context context;
    private List<FoodItem> foodList;

    public FoodLargeAdapter(Context context, List<FoodItem> foodList) {
        this.context = context;
        this.foodList = foodList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_food_large, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FoodItem food = foodList.get(position);
        holder.tvName.setText(food.getName());
        Glide.with(context)
                .load(food.getImageUrl())
                .into(holder.imgFood);

    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFood;
        TextView tvName;

        public ViewHolder(View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.imgFoodBig);
            tvName = itemView.findViewById(R.id.tvFoodNameBig);
        }
    }
}
