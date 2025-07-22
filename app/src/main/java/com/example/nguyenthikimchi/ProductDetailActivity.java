package com.example.nguyenthikimchi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.nguyenthikimchi.api.ApiService;
import com.example.nguyenthikimchi.api.RetrofitClient;
import com.example.nguyenthikimchi.models.CartItem;
import com.example.nguyenthikimchi.models.FoodItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {

    ImageView imgFood, iconCart;
    TextView txtName, txtPrice, txtOriginalPrice, txtDesc, txtIngredients, txtPortion, txtCookingTime, txtQuantity, badgeCart;
    Button btnMinus, btnPlus, btnAddToCart, btnBookNow;
    Spinner spinnerSize, spinnerTopping;
    int quantity = 1;
    private FoodItem currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        Toolbar toolbar = findViewById(R.id.toolbarDetail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Xem chi tiết");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        imgFood = findViewById(R.id.imgDetailFood);
        txtName = findViewById(R.id.txtDetailName);
        txtPrice = findViewById(R.id.txtDetailPrice);
        txtOriginalPrice = findViewById(R.id.txtOriginalPrice);
        txtDesc = findViewById(R.id.txtDetailDesc);
        txtIngredients = findViewById(R.id.txtIngredients);
        txtPortion = findViewById(R.id.txtPortion);
        txtCookingTime = findViewById(R.id.txtCookingTime);
        txtQuantity = findViewById(R.id.txtQuantity);
        btnMinus = findViewById(R.id.btnMinus);
        btnPlus = findViewById(R.id.btnPlus);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnBookNow = findViewById(R.id.btnBookNow);
        iconCart = findViewById(R.id.iconCart);
        badgeCart = findViewById(R.id.badgeCart);
        spinnerSize = findViewById(R.id.spinnerSize);
        spinnerTopping = findViewById(R.id.spinnerTopping);

        String[] sizes = {"Nhỏ", "Vừa", "Lớn"};
        String[] toppings = {"Không", "Phô mai", "Trứng", "Xúc xích"};

        ArrayAdapter<String> sizeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sizes);
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSize.setAdapter(sizeAdapter);

        ArrayAdapter<String> toppingAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, toppings);
        toppingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTopping.setAdapter(toppingAdapter);


        txtQuantity.setText("1");
        txtOriginalPrice.setPaintFlags(txtOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        String foodId = getIntent().getStringExtra("foodId");
        if (foodId != null && !foodId.isEmpty()) {
            loadFoodDetail(foodId);
        } else {
            Toast.makeText(this, "Món ăn không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnPlus.setOnClickListener(v -> {
            quantity++;
            txtQuantity.setText(String.valueOf(quantity));
        });

        btnMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                txtQuantity.setText(String.valueOf(quantity));
            }
        });

        btnAddToCart.setOnClickListener(v -> {
            if (currentItem != null) {
                SharedPreferences prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
                String userId = prefs.getString("userId", "defaultUser");

                double price;
                try {
                    String rawPrice = currentItem.getPrice();
                    String cleaned = rawPrice.replace(".", "").replace(",", "");
                    price = Double.parseDouble(cleaned);
                } catch (Exception e) {
                    Toast.makeText(this, "Lỗi giá sản phẩm", Toast.LENGTH_SHORT).show();
                    return;
                }

                String selectedSize = spinnerSize.getSelectedItem().toString();
                String selectedTopping = spinnerTopping.getSelectedItem().toString();
                String key = currentItem.getId() + "_" + selectedSize + "_" + selectedTopping;

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("cart")
                        .child(userId).child(key);

                ref.get().addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        CartItem existing = snapshot.getValue(CartItem.class);
                        int newQty = existing.getQuantity() + quantity;
                        ref.child("quantity").setValue(newQty);
                    } else {
                        CartItem cartItem = new CartItem(
                                currentItem.getId(),
                                currentItem.getName(),
                                currentItem.getImageUrl(),
                                price,
                                quantity,
                                selectedSize,
                                selectedTopping
                        );

                        ref.setValue(cartItem);
                    }

                    Toast.makeText(this, "Đã thêm " + quantity + " sản phẩm vào giỏ", Toast.LENGTH_SHORT).show();
                    updateCartBadge();
                });
            }
        });

        btnBookNow.setOnClickListener(v -> {
            Intent intent = new Intent(ProductDetailActivity.this, BookingActivity.class);
            startActivity(intent);
        });

        iconCart.setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));
        updateCartBadge();
    }

    private void loadFoodDetail(String id) {
        ApiService api = RetrofitClient.getRetrofit().create(ApiService.class);
        api.getFoodById(id).enqueue(new Callback<FoodItem>() {
            @Override
            public void onResponse(Call<FoodItem> call, Response<FoodItem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    FoodItem item = response.body();
                    currentItem = item;

                    txtName.setText(item.getName());

                    try {
                        String cleaned = item.getPrice().replace(".", "").replace(",", "");
                        double price = Double.parseDouble(cleaned);
                        DecimalFormat formatter = new DecimalFormat("#,###");
                        txtPrice.setText(formatter.format(price) + "đ");
                    } catch (Exception e) {
                        txtPrice.setText(item.getPrice() + "đ");
                    }

                    txtOriginalPrice.setText(item.getOriginalPrice() + "đ");
                    txtDesc.setText(item.getDescription());
                    txtIngredients.setText("Thành phần: " + item.getIngredients());
                    txtPortion.setText("Khẩu phần: " + item.getPortion());
                    txtCookingTime.setText("Thời gian hoàn tất: " + item.getCookingTime());

                    Glide.with(ProductDetailActivity.this)
                            .load(item.getImageUrl())
                            .into(imgFood);
                } else {
                    Toast.makeText(ProductDetailActivity.this, "Không tìm thấy món ăn", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<FoodItem> call, Throwable t) {
                Toast.makeText(ProductDetailActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void updateCartBadge() {
        SharedPreferences prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
        String userId = prefs.getString("userId", "defaultUser");

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("cart")
                .child(userId);

        ref.get().addOnSuccessListener(snapshot -> {
            int total = 0;
            for (DataSnapshot itemSnap : snapshot.getChildren()) {
                CartItem item = itemSnap.getValue(CartItem.class);
                if (item != null) {
                    total += item.getQuantity();
                }
            }

            if (total > 0) {
                badgeCart.setText(total > 99 ? "99+" : String.valueOf(total));
                badgeCart.setVisibility(View.VISIBLE);
            } else {
                badgeCart.setVisibility(View.GONE);
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Lỗi tải giỏ hàng", Toast.LENGTH_SHORT).show();
        });
    }
}