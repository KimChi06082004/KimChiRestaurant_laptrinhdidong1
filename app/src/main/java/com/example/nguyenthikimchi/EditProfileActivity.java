package com.example.nguyenthikimchi;
import com.example.nguyenthikimchi.R;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.nguyenthikimchi.api.ApiService;
import com.example.nguyenthikimchi.api.RetrofitClient;
import com.example.nguyenthikimchi.models.User;

import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private EditText edtName, edtPassword, edtGender, edtBirth, edtPhone, edtEmail;
    private Button btnSave;
    private ImageView imgAvatar;

    private SharedPreferences prefs;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = findViewById(R.id.toolbarEdit);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        username = prefs.getString("username", "");

        // Ánh xạ view
        edtName = findViewById(R.id.edtName);
        edtPassword = findViewById(R.id.edtPassword); // Thêm dòng này
        edtGender = findViewById(R.id.edtGender);
        edtBirth = findViewById(R.id.edtBirth);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        btnSave = findViewById(R.id.btnSaveProfile);
        imgAvatar = findViewById(R.id.imgAvatarEdit);


        // Ngày sinh: mở DatePicker
        edtBirth.setOnClickListener(v -> showDatePicker());

        // Tải dữ liệu người dùng
        fetchUserInfo();

        // Cập nhật hồ sơ
        btnSave.setOnClickListener(v -> updateProfile());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, day) -> {
                    String date = String.format("%02d/%02d/%04d", day, month + 1, year);
                    edtBirth.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void fetchUserInfo() {
        ApiService api = RetrofitClient.getRetrofit().create(ApiService.class);
        api.getUserByUsername(username).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    User user = response.body().get(0); // lấy user đầu tiên từ danh sách
                    edtName.setText(user.getName());
                    edtPassword.setText(user.getPassword());
                    edtGender.setText(user.getGender());
                    edtBirth.setText(user.getBirthdate());
                    edtPhone.setText(user.getPhone());
                    edtEmail.setText(user.getEmail());
                } else {
                    Toast.makeText(EditProfileActivity.this, "Không tải được dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateProfile() {
        String name = edtName.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String gender = edtGender.getText().toString().trim();
        String birth = edtBirth.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();

        if (password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User(username, password, name, gender, birth, phone, email);

        ApiService api = RetrofitClient.getRetrofit().create(ApiService.class);
        api.updateUser(username, user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    finish(); // Quay về trang trước
                } else {
                    Toast.makeText(EditProfileActivity.this, "Lỗi khi cập nhật", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
