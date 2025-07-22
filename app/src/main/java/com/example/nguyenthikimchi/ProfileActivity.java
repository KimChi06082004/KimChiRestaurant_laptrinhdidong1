package com.example.nguyenthikimchi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.nguyenthikimchi.api.ApiService;

import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private static final int EDIT_PROFILE_REQUEST = 100;

    private TextView tvUsername, tvDeleteAccountStyled;
    private ImageView imgAvatar;
    private SharedPreferences prefs;
    private Button btnLoginLogout;
    private boolean isLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbarProfile);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Trang cá nhân");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        isLoggedIn = prefs.getBoolean("is_logged_in", false);

        tvUsername = findViewById(R.id.tvUsername);
        imgAvatar = findViewById(R.id.imgAvatar);
        ListView listOptions = findViewById(R.id.listOptions);
        btnLoginLogout = findViewById(R.id.btnLoginLogout);
        tvDeleteAccountStyled = findViewById(R.id.tvDeleteAccountStyled);

        // Danh sách chức năng
        String[] titles = {"Món yêu thích", "Thanh toán", "Địa chỉ", "Mời bạn bè"};
        int[] icons = {
                R.drawable.ic_favorite_outline,
                R.drawable.ic_payment_card,
                R.drawable.ic_location_pin,
                R.drawable.ic_share_friend
        };

        List<Map<String, Object>> data = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("title", titles[i]);
            item.put("icon", icons[i]);
            data.add(item);
        }

        SimpleAdapter adapter = new SimpleAdapter(
                this,
                data,
                R.layout.profile_item,
                new String[]{"title", "icon"},
                new int[]{R.id.txtOption, R.id.imgOption}
        );
        listOptions.setAdapter(adapter);

        listOptions.setOnItemClickListener((parent, view, position, id) -> {
            if (position == 0) {
                startActivity(new Intent(ProfileActivity.this, FavoriteActivity.class));
            } else {
                Toast.makeText(this, "Tính năng đang phát triển", Toast.LENGTH_SHORT).show();
            }
        });

        if (isLoggedIn) {
            btnLoginLogout.setText("Đăng xuất");
            tvDeleteAccountStyled.setVisibility(View.VISIBLE);
            reloadUserInfo();
        } else {
            tvUsername.setText("Khách chưa đăng nhập");
            imgAvatar.setImageResource(R.drawable.ic_person);
            btnLoginLogout.setText("Đăng ký / Đăng nhập");
            tvDeleteAccountStyled.setVisibility(View.VISIBLE);
        }

        imgAvatar.setOnClickListener(v -> {
            if (isLoggedIn)
                startActivityForResult(new Intent(this, EditProfileActivity.class), EDIT_PROFILE_REQUEST);
        });

        tvUsername.setOnClickListener(v -> {
            if (isLoggedIn)
                startActivityForResult(new Intent(this, EditProfileActivity.class), EDIT_PROFILE_REQUEST);
        });

        // Xử lý nút đăng nhập / đăng xuất
        btnLoginLogout.setOnClickListener(v -> {
            if (isLoggedIn) {
                // ✅ Hiển thị loading khi đăng xuất
                ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
                progressDialog.setMessage("Đang đăng xuất...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                new android.os.Handler().postDelayed(() -> {
                    prefs.edit().clear().apply();
                    progressDialog.dismiss();
                    Toast.makeText(ProfileActivity.this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }, 1500);

            } else {
                new AlertDialog.Builder(ProfileActivity.this)
                        .setTitle("Đăng ký / Đăng nhập")
                        .setMessage("Bạn cần đăng ký hoặc đăng nhập để sử dụng đầy đủ tính năng.")
                        .setPositiveButton("Tiếp tục", (dialog, which) -> {
                            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                            intent.putExtra("from_profile", true);
                            startActivity(intent);
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            }
        });

        // Xử lý xóa tài khoản
        tvDeleteAccountStyled.setOnClickListener(v -> {
            tvDeleteAccountStyled.setPaintFlags(tvDeleteAccountStyled.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            if (!isLoggedIn) {
                Toast.makeText(this, "Bạn chưa có tài khoản", Toast.LENGTH_SHORT).show();
                return;
            }

            new AlertDialog.Builder(ProfileActivity.this)
                    .setTitle("Xác nhận xóa tài khoản")
                    .setMessage("Bạn có chắc muốn xóa tài khoản không?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        String userId = prefs.getString("user_id", "");
                        if (!userId.isEmpty()) {
                            ApiService.api.deleteUser(userId).enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if (response.isSuccessful()) {
                                        prefs.edit().clear().apply();
                                        Toast.makeText(ProfileActivity.this, "Tài khoản đã bị xóa", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(ProfileActivity.this, "Không thể xóa tài khoản", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    Toast.makeText(ProfileActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }

    private void reloadUserInfo() {
        String username = prefs.getString("username", "");
        String displayName = prefs.getString("user_name", username);
        String avatarUrl = prefs.getString("avatar_url", "");

        tvUsername.setText(displayName);

        if (!avatarUrl.isEmpty()) {
            Glide.with(this)
                    .load(avatarUrl)
                    .centerCrop()
                    .placeholder(R.drawable.ic_person)
                    .into(imgAvatar);
        } else {
            imgAvatar.setImageResource(R.drawable.ic_person);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_PROFILE_REQUEST && resultCode == RESULT_OK) {
            reloadUserInfo();
        }
    }
}
