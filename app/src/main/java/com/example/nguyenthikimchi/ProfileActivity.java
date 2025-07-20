package com.example.nguyenthikimchi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private static final int EDIT_PROFILE_REQUEST = 100;

    private TextView tvUsername;
    private ImageView imgAvatar;
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
        boolean isLoggedIn = prefs.getBoolean("is_logged_in", false);

        if (!isLoggedIn || username.isEmpty()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        // Ánh xạ
        tvUsername = findViewById(R.id.tvUsername);
        imgAvatar = findViewById(R.id.imgAvatar);
        ListView listOptions = findViewById(R.id.listOptions);

        // Hiển thị tên và avatar ban đầu
        reloadUserInfo();

        // Mở trang sửa hồ sơ
        imgAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditProfileActivity.class);
            startActivityForResult(intent, EDIT_PROFILE_REQUEST);
        });

        tvUsername.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditProfileActivity.class);
            startActivityForResult(intent, EDIT_PROFILE_REQUEST);
        });

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
            imgAvatar.setImageResource(R.drawable.ic_person); // fallback
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
