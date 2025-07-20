package com.example.nguyenthikimchi;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.nguyenthikimchi.adapters.FoodAdapter;
import com.example.nguyenthikimchi.models.FoodItem;
import com.example.nguyenthikimchi.utils.FavoriteManager;

import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FoodAdapter foodAdapter;

    private SharedPreferences prefs;
    private ImageView imgAvatar;
    private TextView tvUsername;

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

        // ✅ Ánh xạ avatar và username
        prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        imgAvatar = findViewById(R.id.imgAvatar); // Cần có trong layout XML
        tvUsername = findViewById(R.id.tvUsername); // Cần có trong layout XML

        recyclerView = findViewById(R.id.recyclerViewFavorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<FoodItem> favoriteItems = FavoriteManager.getFavorites();
        foodAdapter = new FoodAdapter(this, favoriteItems, null);
        recyclerView.setAdapter(foodAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserInfo(); // Tải lại ảnh đại diện nếu vừa chỉnh sửa
    }

    private void loadUserInfo() {
        String username = prefs.getString("username", "");
        String avatarUrl = prefs.getString("avatar_url", "");
        String displayName = prefs.getString("user_name", username); // fallback nếu chưa cập nhật tên

        tvUsername.setText(displayName);

        if (!avatarUrl.isEmpty()) {
            Glide.with(this)
                    .load(avatarUrl)
                    .circleCrop()
                    .into(imgAvatar);
        } else {
            imgAvatar.setImageResource(R.drawable.ic_default_avatar); // avatar mặc định
        }
    }
}
