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

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

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

        initViews();
        setupListeners();

        // Kích hoạt hiệu ứng nền động
        View root = findViewById(android.R.id.content);
        Drawable background = root.getBackground();
        if (background instanceof AnimationDrawable) {
            AnimationDrawable animationDrawable = (AnimationDrawable) background;
            animationDrawable.setEnterFadeDuration(2000);
            animationDrawable.setExitFadeDuration(4000);
            animationDrawable.start();
        }

        // Gradient cho tiêu đề
        TextView tvRegisterTitle = findViewById(R.id.tvRegisterTitle);
        TextView tvLoginTitle = findViewById(R.id.tvLoginTitle);

        Shader shader = new LinearGradient(
                0, 0, 0, tvRegisterTitle.getTextSize(),
                new int[]{
                        Color.parseColor("#21D4FD"),
                        Color.parseColor("#B721FF")
                },
                null, Shader.TileMode.CLAMP);

        tvRegisterTitle.getPaint().setShader(shader);
        tvLoginTitle.getPaint().setShader(shader);
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

            // Lưu vào SharedPreferences
            sharedPreferences.edit()
                    .putString("username", username)
                    .putString("password", password)
                    .apply();

            Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
            cardRegister.setVisibility(View.GONE);
            cardLogin.setVisibility(View.VISIBLE);
        });

        btnLogin.setOnClickListener(v -> {
            String username = etLoginUsername.getText().toString().trim();
            String password = etLoginPassword.getText().toString().trim();

            // Lấy từ SharedPreferences
            String registeredUsername = sharedPreferences.getString("username", "");
            String registeredPassword = sharedPreferences.getString("password", "");

            if (username.equals(registeredUsername) && password.equals(registeredPassword)) {
                Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, HomeActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                // finish();
            } else {
                Toast.makeText(this, "Sai tên đăng nhập hoặc mật khẩu", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
