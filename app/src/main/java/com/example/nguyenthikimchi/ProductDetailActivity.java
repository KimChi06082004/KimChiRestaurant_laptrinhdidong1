package com.example.nguyenthikimchi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import android.graphics.Paint;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.nguyenthikimchi.api.ApiService;
import com.example.nguyenthikimchi.api.RetrofitClient;
import com.example.nguyenthikimchi.models.FoodItem;
import com.example.nguyenthikimchi.utils.CartManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.view.View;

public class ProductDetailActivity extends AppCompatActivity {

    ImageView imgFood, iconCart;
    TextView txtName, txtPrice, txtOriginalPrice, txtDesc, txtIngredients, txtPortion, txtCookingTime, txtQuantity, badgeCart;
    Button btnMinus, btnPlus, btnAddToCart, btnBookNow;
    int quantity = 1;
    private FoodItem currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // ✅ Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarDetail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        // ✅ Ánh xạ View
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

        txtQuantity.setText("1");
        txtOriginalPrice.setPaintFlags(txtOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        // ✅ Nhận dữ liệu
        String foodId = getIntent().getStringExtra("foodId");
        if (foodId != null && !foodId.isEmpty()) {
            loadFoodDetail(foodId);
        } else {
            Toast.makeText(this, "Món ăn không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // ✅ Số lượng
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

        // ✅ Thêm vào giỏ hàng
        btnAddToCart.setOnClickListener(v -> {
            if (currentItem != null) {
                double price = 0;
                try {
                    price = Double.parseDouble(currentItem.getPrice());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                CartManager.addToCart(
                        currentItem.getId(),
                        currentItem.getName(),
                        price,
                        quantity
                );

                updateCartBadge();
                Toast.makeText(this, "Đã thêm " + quantity + " vào giỏ", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
            }
        });

        // ✅ Đặt bàn
        btnBookNow.setOnClickListener(v -> {
            Toast.makeText(this, "Chuyển sang đặt bàn...", Toast.LENGTH_SHORT).show();
            // TODO: Mở Activity liên hệ/đặt bàn nếu có
        });

        // ✅ Click iconCart mở giỏ hàng
        iconCart.setOnClickListener(v -> {
            startActivity(new Intent(this, CartActivity.class));
        });

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
                    txtPrice.setText(item.getPrice() + "đ");
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
        int count = CartManager.getTotalQuantity();
        if (count > 0) {
            badgeCart.setVisibility(View.VISIBLE);
            badgeCart.setText(String.valueOf(count));
        } else {
            badgeCart.setVisibility(View.GONE);
        }
    }
}
