package com.example.nguyenthikimchi.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.nguyenthikimchi.ProductDetailActivity;
import com.example.nguyenthikimchi.R;
import com.example.nguyenthikimchi.models.CartItem;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;

import java.text.DecimalFormat;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<CartItem> cartItems;
    private OnCartItemChangeListener listener;

    public interface OnCartItemChangeListener {
        void onCartChanged();
    }

    public CartAdapter(Context context, List<CartItem> cartItems, OnCartItemChangeListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        holder.txtProductName.setText(item.getName());

        // ✅ Không còn xử lý chuỗi nữa, vì price là double
        double price = item.getPrice();
        DecimalFormat formatter = new DecimalFormat("#,###");
        holder.txtProductPrice.setText("₫" + formatter.format(price));

        holder.txtQuantity.setText(String.valueOf(item.getQuantity()));
        holder.checkboxItem.setChecked(item.isSelected());

        holder.txtSize.setText("Size: " + item.getSize());
        holder.txtTopping.setText("Topping: " + item.getTopping());

        Glide.with(context).load(item.getImageUrl()).into(holder.imgProduct);

        holder.checkboxItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setSelected(isChecked);
            listener.onCartChanged();
        });


        holder.btnPlus.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            notifyItemChanged(position);
            listener.onCartChanged();
        });

        holder.btnMinus.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                notifyItemChanged(position);
                listener.onCartChanged();
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            SharedPreferences prefs = context.getSharedPreferences("MyApp", Context.MODE_PRIVATE);
            String userId = prefs.getString("userId", "defaultUser");

            String key = item.getId();
            if (item.getSize() != null && item.getTopping() != null) {
                key += "_" + item.getSize() + "_" + item.getTopping();
            }

            DatabaseReference cartRef = FirebaseDatabase.getInstance()
                    .getReference("cart")
                    .child(userId)
                    .child(key);

            cartRef.removeValue().addOnSuccessListener(unused -> {
                cartItems.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, cartItems.size());
                listener.onCartChanged();
                Toast.makeText(context, "Đã xoá sản phẩm khỏi giỏ", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                Toast.makeText(context, "Lỗi khi xoá", Toast.LENGTH_SHORT).show();
            });
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("foodId", item.getId());
            intent.putExtra("size", item.getSize());
            intent.putExtra("topping", item.getTopping());
            intent.putExtra("quantity", item.getQuantity());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkboxItem;
        ImageView imgProduct;
        TextView txtProductName, txtProductPrice, txtQuantity;
        TextView txtSize, txtTopping;
        Button btnPlus, btnMinus, btnDelete;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            checkboxItem = itemView.findViewById(R.id.checkboxItem);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtProductPrice = itemView.findViewById(R.id.txtProductPrice);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            txtSize = itemView.findViewById(R.id.txtSize);
            txtTopping = itemView.findViewById(R.id.txtTopping);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
