package com.example.nguyenthikimchi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.nguyenthikimchi.models.CartItem;
import com.example.nguyenthikimchi.models.Order;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {

    TextView txtRestaurantName;
    Button btnPlaceOrder;

    private String userId;
    private List<CartItem> cartItems = new ArrayList<>();
    private double totalAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarCheckout);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Thanh toán");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        txtRestaurantName = findViewById(R.id.txtRestaurantName);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        txtRestaurantName.setText("Kim Chi Restaurant");

        // Lấy userId
        SharedPreferences prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
        userId = prefs.getString("userId", "defaultUser");

        // Tải sản phẩm trong giỏ hàng
        loadCartItems();

        btnPlaceOrder.setOnClickListener(v -> placeOrder());
    }

    private void loadCartItems() {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("cart")
                .child(userId);

        ref.get().addOnSuccessListener(snapshot -> {
            cartItems.clear();
            totalAmount = 0;

            for (DataSnapshot child : snapshot.getChildren()) {
                CartItem item = child.getValue(CartItem.class);
                if (item != null) {
                    cartItems.add(item);
                    totalAmount += item.getPrice() * item.getQuantity();
                }
            }
        });
    }

    private void placeOrder() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference orderRef = FirebaseDatabase.getInstance()
                .getReference("orders")
                .child(userId)
                .push();

        String orderId = orderRef.getKey();

        Order order = new Order(orderId, userId, cartItems, totalAmount, System.currentTimeMillis());

        orderRef.setValue(order).addOnSuccessListener(unused -> {
            // Xoá giỏ hàng
            FirebaseDatabase.getInstance().getReference("cart")
                    .child(userId).removeValue();

            Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();

            // Chuyển sang trang quản lý đơn hàng
            Intent intent = new Intent(CheckoutActivity.this, OrderHistoryActivity.class);
            startActivity(intent);
            finish();
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Lỗi khi đặt hàng", Toast.LENGTH_SHORT).show()
        );
    }
}
