package com.example.nguyenthikimchi;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.nguyenthikimchi.adapters.BannerAdapter;
import com.example.nguyenthikimchi.adapters.FoodAdapter;
import com.example.nguyenthikimchi.models.FoodItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private ViewPager2 bannerSlider;
    private RecyclerView foodRecyclerView;
    private EditText searchInput;
    private Button btnSearch;
    private Button btnBookNow;

    private List<FoodItem> allFoodList;
    private FoodAdapter foodAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Ánh xạ view
        bannerSlider = findViewById(R.id.bannerSlider);
        foodRecyclerView = findViewById(R.id.foodRecyclerView);
        searchInput = findViewById(R.id.searchInput);
        btnSearch = findViewById(R.id.btnSearch);
        btnBookNow = findViewById(R.id.btnBookNow);

        // Dữ liệu banner
        List<Integer> bannerImages = Arrays.asList(
                R.drawable.banner1,
                R.drawable.banner2,
                R.drawable.banner3
        );
        BannerAdapter bannerAdapter = new BannerAdapter(this, bannerImages);
        bannerSlider.setAdapter(bannerAdapter);

        // Danh sách món ăn gốc
        allFoodList = Arrays.asList(
                new FoodItem("Pizza", "120.000đ", R.drawable.food1),
                new FoodItem("Ramen", "90.000đ", R.drawable.food2),
                new FoodItem("Pancake", "70.000đ", R.drawable.food3)
        );

        // Adapter hiển thị danh sách ban đầu
        foodAdapter = new FoodAdapter(this, new ArrayList<>(allFoodList));
        foodRecyclerView.setAdapter(foodAdapter);
        foodRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Tìm món ăn khi nhập
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterFoodList(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Tìm món ăn khi bấm nút "Tìm"
        btnSearch.setOnClickListener(v -> {
            String keyword = searchInput.getText().toString().trim();
            filterFoodList(keyword);
        });

        // Nút ĐẶT BÀN NGAY chuyển sang BookingActivity (nếu đã có)
        btnBookNow.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, BookingActivity.class);
            startActivity(intent);
        });

        // Xử lý Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) return true;
            else if (id == R.id.nav_cart) {
                // startActivity(new Intent(this, CartActivity.class));
                return true;
            } else if (id == R.id.nav_favorite) {
                // startActivity(new Intent(this, FavoriteActivity.class));
                return true;
            } else if (id == R.id.nav_notify) {
                // startActivity(new Intent(this, NotificationActivity.class));
                return true;
            }
            return false;
        });
    }

    // Hàm lọc món ăn theo từ khóa
    private void filterFoodList(String keyword) {
        List<FoodItem> filteredList = new ArrayList<>();
        for (FoodItem item : allFoodList) {
            if (item.getName().toLowerCase().contains(keyword.toLowerCase())) {
                filteredList.add(item);
            }
        }

        foodAdapter = new FoodAdapter(this, filteredList);
        foodRecyclerView.setAdapter(foodAdapter);
    }
}
