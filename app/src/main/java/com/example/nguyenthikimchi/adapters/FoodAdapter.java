package com.example.nguyenthikimchi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.nguyenthikimchi.HomeActivity;
import com.example.nguyenthikimchi.R;
import com.example.nguyenthikimchi.models.FoodItem;
import com.example.nguyenthikimchi.utils.FavoriteManager;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private final Context context;
    private List<FoodItem> foodList;
    private final OnFoodActionListener listener;

    public FoodAdapter(Context context, List<FoodItem> foodList, OnFoodActionListener listener) {
        this.context = context;
        this.foodList = foodList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_menu_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodItem item = foodList.get(position);

        holder.txtName.setText(item.getName());

        try {
            double price = Double.parseDouble(item.getPrice());
            holder.txtPrice.setText(String.format("%,.0fđ", price));
        } catch (NumberFormatException e) {
            holder.txtPrice.setText("N/A");
        }

        if (item.getOriginalPrice() != null && !item.getOriginalPrice().isEmpty()) {
            holder.txtOldPrice.setVisibility(View.VISIBLE);
            holder.txtOldPrice.setText(item.getOriginalPrice() + "đ");
        } else {
            holder.txtOldPrice.setVisibility(View.GONE);
        }

        if (item.getDiscountPercent() > 0) {
            holder.txtDiscount.setVisibility(View.VISIBLE);
            holder.txtDiscount.setText("-" + item.getDiscountPercent() + "%");
        } else {
            holder.txtDiscount.setVisibility(View.GONE);
        }

        Glide.with(context)
                .load(item.getImageUrl())
                .into(holder.imgFood);

        holder.btnDetail.setOnClickListener(v -> {
            if (item.getId() != null) {
                if (context instanceof HomeActivity) {
                    ((HomeActivity) context).openProductDetail(item.getId());
                } else {
                    Toast.makeText(context, "Không thể mở chi tiết (context không hợp lệ)", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Không thể mở chi tiết món ăn (ID rỗng)", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnFavorite.setImageResource(
                item.isFavorite() ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite
        );

        holder.btnFavorite.setOnClickListener(v -> {
            item.setFavorite(!item.isFavorite());
            FavoriteManager.toggleFavorite(item);
            notifyItemChanged(position);
            if (listener != null) {
                listener.onFavoriteToggled(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public void updateData(List<FoodItem> newList) {
        foodList.clear();
        foodList.addAll(newList);
        notifyDataSetChanged();
    }

    public void setData(List<FoodItem> newList) {
        this.foodList = newList;
        notifyDataSetChanged();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFood, btnFavorite;
        TextView txtName, txtPrice, txtOldPrice, txtDiscount;
        Button btnDetail;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.imgFood);
            txtName = itemView.findViewById(R.id.txtFoodName);
            txtPrice = itemView.findViewById(R.id.txtFoodPrice);
            txtOldPrice = itemView.findViewById(R.id.txtFoodOldPrice);
            txtDiscount = itemView.findViewById(R.id.txtDiscount);
            btnDetail = itemView.findViewById(R.id.btnDetail);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
        }
    }

    public interface OnFoodActionListener {
        void onFavoriteToggled(FoodItem item);
    }
}
