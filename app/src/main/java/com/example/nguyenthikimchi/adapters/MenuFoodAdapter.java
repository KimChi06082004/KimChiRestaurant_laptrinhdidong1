package com.example.nguyenthikimchi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nguyenthikimchi.R;
import com.example.nguyenthikimchi.models.FoodItem;

import java.util.List;

public class MenuFoodAdapter extends RecyclerView.Adapter<MenuFoodAdapter.MenuViewHolder> {

    public interface OnFoodClickListener {
        void onFoodClick(FoodItem foodItem);
    }

    private final Context context;
    private final List<FoodItem> foodList;
    private final OnFoodClickListener listener;

    public MenuFoodAdapter(Context context, List<FoodItem> foodList, OnFoodClickListener listener) {
        this.context = context;
        this.foodList = foodList;
        this.listener = listener;
    }

    // ✅ Phân trang - thêm mới vào danh sách thay vì replace toàn bộ
    public void updateData(List<FoodItem> newList) {
        int startPos = foodList.size();
        foodList.addAll(newList);
        notifyItemRangeInserted(startPos, newList.size());
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_menu_food, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        FoodItem item = foodList.get(position);
        holder.name.setText(item.getName());
        holder.price.setText(item.getPrice());

        Glide.with(context)
                .load(item.getImageUrl())
                .into(holder.image);

        holder.btnDetail.setOnClickListener(v -> listener.onFoodClick(item));
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class MenuViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, price;
        Button btnDetail;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imgFood); // ✅ Sửa từ foodImage → imgFood
            name = itemView.findViewById(R.id.txtFoodName);
            price = itemView.findViewById(R.id.txtFoodPrice);
            btnDetail = itemView.findViewById(R.id.btnDetail);
        }
    }

}
