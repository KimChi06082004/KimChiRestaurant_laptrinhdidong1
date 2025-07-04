package com.example.nguyenthikimchi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.nguyenthikimchi.adapters.BannerAdapter;
import com.example.nguyenthikimchi.adapters.FoodAdapter;
import com.example.nguyenthikimchi.api.ApiService;
import com.example.nguyenthikimchi.api.RetrofitClient;
import com.example.nguyenthikimchi.models.FoodItem;
import com.example.nguyenthikimchi.utils.CartManager;
import com.example.nguyenthikimchi.utils.FavoriteManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private static final int CART_REQUEST_CODE = 100;

    private ViewPager2 bannerSlider;
    private RecyclerView foodRecyclerView;
    private EditText searchInput;
    private FoodAdapter foodAdapter;
    private final List<FoodItem> foodList = new ArrayList<>();
    private List<FoodItem> allFoods = new ArrayList<>();

    private Button btnKhaiVi, btnMonChinh, btnCanh, btnCom;
    private TextView badgeCart, badgeFavorite;
    private ImageView iconCart;
    private ActivityResultLauncher<Intent> productDetailLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Ánh xạ
        bannerSlider = findViewById(R.id.bannerSlider);
        foodRecyclerView = findViewById(R.id.foodRecyclerView);
        searchInput = findViewById(R.id.searchInput);
        badgeCart = findViewById(R.id.badgeCart);
        badgeFavorite = findViewById(R.id.badgeNotify);
        btnKhaiVi = findViewById(R.id.btnKhaiVi);
        btnMonChinh = findViewById(R.id.btnMonChinh);
        btnCanh = findViewById(R.id.btnCanh);
        btnCom = findViewById(R.id.btnCom);
        iconCart = findViewById(R.id.iconCart);

        badgeCart.setVisibility(View.GONE);
        badgeFavorite.setVisibility(View.GONE);

        // Hiển thị dạng lưới 2 cột
        foodRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        iconCart.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CartActivity.class);
            startActivityForResult(intent, CART_REQUEST_CODE);
        });

        productDetailLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        updateCartBadge();
                    }
                });

        foodAdapter = new FoodAdapter(this, foodList, new FoodAdapter.OnFoodActionListener() {
            @Override
            public void onFavoriteToggled(FoodItem item) {
                int favoriteCount = FavoriteManager.getFavorites().size();
                if (favoriteCount > 0) {
                    badgeFavorite.setVisibility(View.VISIBLE);
                    badgeFavorite.setText(String.valueOf(favoriteCount));
                } else {
                    badgeFavorite.setVisibility(View.GONE);
                }
            }
        });

        foodRecyclerView.setAdapter(foodAdapter);

        // Danh mục
        btnKhaiVi.setOnClickListener(v -> {
            filterByCategory("Khai vị");
            setActiveCategory(btnKhaiVi);
        });
        btnMonChinh.setOnClickListener(v -> {
            filterByCategory("Món chính");
            setActiveCategory(btnMonChinh);
        });
        btnCanh.setOnClickListener(v -> {
            filterByCategory("Canh");
            setActiveCategory(btnCanh);
        });
        btnCom.setOnClickListener(v -> {
            filterByCategory("Cơm");
            setActiveCategory(btnCom);
        });

        List<Integer> bannerImages = List.of(
                R.drawable.banner1,
                R.drawable.banner2,
                R.drawable.banner3
        );
        bannerSlider.setAdapter(new BannerAdapter(this, bannerImages));

        fetchAllFoods();

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_favorite) {
                startActivity(new Intent(this, FavoriteActivity.class));
                return true;
            } else if (id == R.id.nav_notify) {
                Toast.makeText(this, "Thông báo đang phát triển", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });

        updateCartBadge();
    }

    public void openProductDetail(String foodId) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra("foodId", foodId);
        productDetailLauncher.launch(intent);
    }

    private void fetchAllFoods() {
        ApiService apiService = RetrofitClient.getRetrofit().create(ApiService.class);
        Call<List<FoodItem>> call = apiService.getAllFoods(1, 100);

        call.enqueue(new Callback<List<FoodItem>>() {
            @Override
            public void onResponse(Call<List<FoodItem>> call, Response<List<FoodItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allFoods = response.body();
                    for (FoodItem item : allFoods) {
                        item.setFavorite(FavoriteManager.isFavorite(item.getId()));
                    }
                    foodList.clear();
                    foodList.addAll(allFoods);
                    foodAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(HomeActivity.this, "Không nhận được dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<FoodItem>> call, Throwable t) {
                Toast.makeText(HomeActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterByCategory(String categoryKey) {
        List<FoodItem> filtered = new ArrayList<>();
        for (FoodItem item : allFoods) {
            if (item.getCategory() != null && item.getCategory().equalsIgnoreCase(categoryKey)) {
                filtered.add(item);
            }
        }
        foodList.clear();
        foodList.addAll(filtered);
        foodAdapter.notifyDataSetChanged();
    }

    private void setActiveCategory(Button selectedButton) {
        btnKhaiVi.setBackgroundResource(R.drawable.chip_unselected);
        btnMonChinh.setBackgroundResource(R.drawable.chip_unselected);
        btnCanh.setBackgroundResource(R.drawable.chip_unselected);
        btnCom.setBackgroundResource(R.drawable.chip_unselected);
        selectedButton.setBackgroundResource(R.drawable.chip_selected);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CART_REQUEST_CODE && resultCode == RESULT_OK) {
            updateCartBadge();
        }
    }
}
