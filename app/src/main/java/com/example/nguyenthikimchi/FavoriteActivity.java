package com.example.nguyenthikimchi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nguyenthikimchi.adapters.FoodAdapter;
import com.example.nguyenthikimchi.models.FoodItem;
import com.example.nguyenthikimchi.utils.FavoriteManager;

import java.util.List;


public class FavoriteActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FoodAdapter foodAdapter;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        // ✅ Toolbar với mũi tên quay lại
        Toolbar toolbar = findViewById(R.id.toolbarDetail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Món yêu thích");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // ✅ Khởi tạo SharedPreferences
        prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);

        // ✅ Hiển thị danh sách yêu thích
        recyclerView = findViewById(R.id.recyclerViewFavorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<FoodItem> favoriteItems = FavoriteManager.getFavorites();

        foodAdapter = new FoodAdapter(this, favoriteItems,
                item -> {
                    // 👉 Khi nhấn "Xem chi tiết"
                    Intent intent = new Intent(FavoriteActivity.this, ProductDetailActivity.class);
                    intent.putExtra("foodId", item.getId());
                    startActivity(intent);
                },
                item -> {
                    // 👉 Khi nhấn trái tim toggle yêu thích
                    Toast.makeText(this, "Đã cập nhật yêu thích", Toast.LENGTH_SHORT).show();
                }
        );

        recyclerView.setAdapter(foodAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserInfo(); // (Nếu bạn vẫn muốn giữ lại hàm này)
    }

    private void loadUserInfo() {
        String username = prefs.getString("username", "");
        String avatarUrl = prefs.getString("avatar_url", "");
        String displayName = prefs.getString("user_name", username);
        // (Không cần dùng nếu đã bỏ avatar và tvUsername)
    }
}
