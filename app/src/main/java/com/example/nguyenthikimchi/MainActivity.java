package com.example.nguyenthikimchi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.nguyenthikimchi.api.ApiService;
import com.example.nguyenthikimchi.api.RetrofitClient;
import com.example.nguyenthikimchi.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private CardView cardLogin, cardRegister;
    private TextView tvGoToLogin, tvGoToRegister;
    private MaterialButton btnRegister, btnLogin;
    private TextInputEditText etRegisterUsername, etRegisterPassword, etLoginUsername, etLoginPassword;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        // ✅ Chỉ cho phép vào MainActivity nếu được gọi từ ProfileActivity
        boolean isFromProfile = getIntent().getBooleanExtra("from_profile", false);
        if (!isFromProfile) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }

        initViews();
        setupListeners();
        setupGradientAndAnimation();
    }

    private void initViews() {
        cardLogin = findViewById(R.id.card_login);
        cardRegister = findViewById(R.id.card_register);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);
        etRegisterUsername = findViewById(R.id.etRegisterUsername);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        etLoginUsername = findViewById(R.id.etLoginUsername);
        etLoginPassword = findViewById(R.id.etLoginPassword);
    }

    private void setupListeners() {
        tvGoToLogin.setOnClickListener(v -> {
            cardRegister.setVisibility(View.GONE);
            cardLogin.setVisibility(View.VISIBLE);
        });

        tvGoToRegister.setOnClickListener(v -> {
            cardLogin.setVisibility(View.GONE);
            cardRegister.setVisibility(View.VISIBLE);
        });

        btnRegister.setOnClickListener(v -> {
            String username = etRegisterUsername.getText().toString().trim();
            String password = etRegisterPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            ApiService api = RetrofitClient.getRetrofit().create(ApiService.class);
            api.getUserByUsername(username).enqueue(new Callback<List<User>>() {
                @Override
                public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        Toast.makeText(MainActivity.this, "Tên tài khoản đã tồn tại", Toast.LENGTH_SHORT).show();
                    } else {
                        User newUser = new User(username, password);
                        api.register(newUser).enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "Đăng ký thành công. Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
                                    cardRegister.setVisibility(View.GONE);
                                    cardLogin.setVisibility(View.VISIBLE);
                                } else {
                                    Toast.makeText(MainActivity.this, "Lỗi khi đăng ký", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Lỗi kết nối đến server", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<List<User>> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Lỗi kiểm tra tài khoản: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnLogin.setOnClickListener(v -> {
            String username = etLoginUsername.getText().toString().trim();
            String password = etLoginPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ", Toast.LENGTH_SHORT).show();
                return;
            }

            ApiService api = RetrofitClient.getRetrofit().create(ApiService.class);
            Call<List<User>> call = api.login(username, password);

            call.enqueue(new Callback<List<User>>() {
                @Override
                public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        User user = response.body().get(0);

                        String oldUserId = sharedPreferences.getString("user_id", "defaultUser");
                        FirebaseDatabase.getInstance()
                                .getReference("cart")
                                .child(oldUserId)
                                .removeValue();

                        sharedPreferences.edit()
                                .putBoolean("is_logged_in", true)
                                .putString("username", user.getUsername())
                                .putString("user_id", user.getId())
                                .apply();

                        Toast.makeText(MainActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                        startHome(user.getUsername());
                    } else {
                        Toast.makeText(MainActivity.this, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<User>> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void setupGradientAndAnimation() {
        View root = findViewById(android.R.id.content);
        Drawable background = root.getBackground();
        if (background instanceof AnimationDrawable) {
            AnimationDrawable animationDrawable = (AnimationDrawable) background;
            animationDrawable.setEnterFadeDuration(2000);
            animationDrawable.setExitFadeDuration(4000);
            animationDrawable.start();
        }

        TextView tvRegisterTitle = findViewById(R.id.tvRegisterTitle);
        TextView tvLoginTitle = findViewById(R.id.tvLoginTitle);

        Shader shader = new LinearGradient(
                0, 0, 0, tvRegisterTitle.getTextSize(),
                new int[]{Color.parseColor("#21D4FD"), Color.parseColor("#B721FF")},
                null, Shader.TileMode.CLAMP
        );

        tvRegisterTitle.getPaint().setShader(shader);
        tvLoginTitle.getPaint().setShader(shader);
    }

    private void startHome(String username) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
        finish();
    }
}