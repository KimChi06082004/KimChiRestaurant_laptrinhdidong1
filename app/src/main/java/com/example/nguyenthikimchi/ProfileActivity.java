package com.example.nguyenthikimchi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.nguyenthikimchi.api.ApiService;
import com.example.nguyenthikimchi.api.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUsername;
    private ImageView imgAvatar;
    private Button btnLogout, btnEditProfile, btnOrderHistory, btnDeleteAccount;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarProfile);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Trang cá nhân");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // SharedPreferences
        prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);

        String username = prefs.getString("username", "");
        if (!prefs.getBoolean("is_logged_in", false) || username.isEmpty()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        // Ánh xạ View
        tvUsername = findViewById(R.id.tvUsername);
        imgAvatar = findViewById(R.id.imgAvatar);
        btnLogout = findViewById(R.id.btnLogout);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnOrderHistory = findViewById(R.id.btnOrderHistory);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);

        // Hiển thị tên người dùng
        tvUsername.setText(username);

        // Chuyển sang EditProfileActivity khi nhấn vào avatar hoặc tên
        imgAvatar.setOnClickListener(v -> {
            startActivity(new Intent(this, EditProfileActivity.class));
        });
        tvUsername.setOnClickListener(v -> {
            startActivity(new Intent(this, EditProfileActivity.class));
        });

        // Đăng xuất
        btnLogout.setOnClickListener(v -> {
            prefs.edit()
                    .putBoolean("is_logged_in", false)
                    .remove("username")
                    .remove("password")
                    .apply();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // Quản lý đơn hàng
        btnOrderHistory.setOnClickListener(v -> {
            startActivity(new Intent(this, OrderHistoryActivity.class));
        });

        // Xóa tài khoản
        btnDeleteAccount.setOnClickListener(v -> {
            String userId = prefs.getString("userId", ""); // cần lưu khi đăng nhập
            if (!userId.isEmpty()) {
                ApiService apiService = RetrofitClient.getRetrofit().create(ApiService.class);
                Call<Void> call = apiService.deleteUser(userId);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            prefs.edit().clear().apply();
                            startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                            finish();
                        } else {
                            // Xử lý lỗi nếu có
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        // Xử lý lỗi kết nối
                    }
                });
            }
        });
    }
}
