package com.example.nguyenthikimchi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.nguyenthikimchi.models.CartItem;
import com.example.nguyenthikimchi.models.Order;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {

    private EditText edtEmail, edtName, edtPhone, edtAddress;
    private TextView txtRestaurantName, txtOrderSummary;
    private Button btnPlaceOrder;

    private String userId;
    private List<CartItem> selectedItems = new ArrayList<>();
    private double totalAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        Toolbar toolbar = findViewById(R.id.toolbarCheckout);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Thanh toán");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        // Ánh xạ view
        edtEmail = findViewById(R.id.edtEmail);
        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddress = findViewById(R.id.edtAddress);
        txtRestaurantName = findViewById(R.id.txtRestaurantName);
        txtOrderSummary = findViewById(R.id.txtOrderSummary);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        txtRestaurantName.setText("Kim Chi Restaurant");

        SharedPreferences prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
        userId = prefs.getString("userId", "defaultUser");

        // ✅ Nhận sản phẩm được chọn từ CartActivity
        Intent intent = getIntent();
        ArrayList<CartItem> items = (ArrayList<CartItem>) intent.getSerializableExtra("selectedItems");
        if (items != null) {
            selectedItems = items;
            calculateTotalAndSummary();
        }

        btnPlaceOrder.setOnClickListener(v -> {
            if (!validateInput()) return;
            placeOrder();
        });
    }

    private void calculateTotalAndSummary() {
        totalAmount = 0;
        int totalQuantity = 0;

        for (CartItem item : selectedItems) {
            totalAmount += item.getPrice() * item.getQuantity();
            totalQuantity += item.getQuantity();
        }

        txtOrderSummary.setText("Đơn hàng (" + totalQuantity + " sản phẩm)");
    }

    private boolean validateInput() {
        if (edtEmail.getText().toString().trim().isEmpty()
                || edtName.getText().toString().trim().isEmpty()
                || edtPhone.getText().toString().trim().isEmpty()
                || edtAddress.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (selectedItems.isEmpty()) {
            Toast.makeText(this, "Không có sản phẩm nào để đặt hàng", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void placeOrder() {
        DatabaseReference orderRef = FirebaseDatabase.getInstance()
                .getReference("orders")
                .child(userId)
                .push();

        String orderId = orderRef.getKey();

        Order order = new Order(orderId, userId, selectedItems, totalAmount, System.currentTimeMillis());

        orderRef.setValue(order).addOnSuccessListener(unused -> {
            Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, OrderHistoryActivity.class));
            finish();
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Lỗi khi đặt hàng", Toast.LENGTH_SHORT).show()
        );
    }
}
