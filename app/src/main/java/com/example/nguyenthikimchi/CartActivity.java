package com.example.nguyenthikimchi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nguyenthikimchi.adapters.CartAdapter;
import com.example.nguyenthikimchi.models.CartItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView txtTotalPrice;
    private Button btnCheckout;
    private CheckBox checkboxSelectAll;
    private LinearLayout layoutEmptyCart;

    private List<CartItem> cartList = new ArrayList<>();
    private CartAdapter adapter;

    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.recyclerViewCart);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);
        btnCheckout = findViewById(R.id.btnCheckout);
        checkboxSelectAll = findViewById(R.id.checkboxSelectAll);
        layoutEmptyCart = findViewById(R.id.layoutEmptyCart);

        SharedPreferences prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
        userId = prefs.getString("userId", "defaultUser");

        loadCartFromFirebase();

        checkboxSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (cartList == null) return;
            for (CartItem item : cartList) {
                item.setSelected(isChecked);
            }
            adapter.notifyDataSetChanged();
            updateTotal();
        });

        btnCheckout.setOnClickListener(v -> {
            if (cartList == null) return;

            List<CartItem> selected = new ArrayList<>();
            for (CartItem item : cartList) {
                if (item.isSelected()) {
                    selected.add(item);
                }
            }

            if (selected.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn sản phẩm!", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadCartFromFirebase() {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("cart")
                .child(userId);

        ref.get().addOnSuccessListener(snapshot -> {
            cartList.clear();
            for (DataSnapshot child : snapshot.getChildren()) {
                CartItem item = child.getValue(CartItem.class);
                if (item != null) cartList.add(item);
            }

            if (cartList.isEmpty()) {
                showEmptyCart();
            } else {
                layoutEmptyCart.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                btnCheckout.setEnabled(true);
                checkboxSelectAll.setEnabled(true);

                adapter = new CartAdapter(this, cartList, () -> {
                    updateTotal();
                    if (cartList.isEmpty()) showEmptyCart();
                });

                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(adapter);
                updateTotal();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Lỗi khi tải giỏ hàng", Toast.LENGTH_SHORT).show();
        });
    }

    private void showEmptyCart() {
        recyclerView.setVisibility(View.GONE);
        layoutEmptyCart.setVisibility(View.VISIBLE);
        btnCheckout.setEnabled(false);
        checkboxSelectAll.setEnabled(false);
        txtTotalPrice.setText("₫0");
        btnCheckout.setText("Mua hàng (0)");
    }

    private void updateTotal() {
        if (cartList == null) return;
        double total = 0;
        int count = 0;
        for (CartItem item : cartList) {
            if (item.isSelected()) {
                total += item.getPrice() * item.getQuantity();
                count++;
            }
        }
        txtTotalPrice.setText(String.format("₫%.0f", total));
        btnCheckout.setText("Mua hàng (" + count + ")");
    }
}
