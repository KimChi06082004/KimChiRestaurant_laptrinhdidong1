package com.example.nguyenthikimchi;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.nguyenthikimchi.api.ApiService;
import com.example.nguyenthikimchi.api.RetrofitClient;
import com.example.nguyenthikimchi.models.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.result.ActivityResult;

public class EditProfileActivity extends AppCompatActivity {

    private EditText edtName, edtPassword, edtGender, edtBirth, edtPhone, edtEmail;
    private Button btnSave;
    private ImageView imgAvatar;

    private SharedPreferences prefs;
    private String username;
    private Uri imageUri;

    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    imgAvatar.setImageURI(imageUri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = findViewById(R.id.toolbarEdit);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Sửa hồ sơ");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        username = prefs.getString("username", "");

        edtName = findViewById(R.id.edtName);
        edtPassword = findViewById(R.id.edtPassword);
        edtGender = findViewById(R.id.edtGender);
        edtBirth = findViewById(R.id.edtBirth);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        btnSave = findViewById(R.id.btnSaveProfile);
        imgAvatar = findViewById(R.id.imgAvatarEdit);

        edtBirth.setOnClickListener(v -> showDatePicker());

        imgAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(intent);
        });

        fetchUserInfo();

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
                    User user = response.body().get(0);
                    edtName.setText(user.getName());
                    edtPassword.setText(user.getPassword());
                    edtGender.setText(isEmpty(user.getGender()) ? "Chưa cập nhật" : user.getGender());
                    edtBirth.setText(isEmpty(user.getBirthdate()) ? "Chưa cập nhật" : user.getBirthdate());
                    edtPhone.setText(isEmpty(user.getPhone()) ? "Chưa cập nhật" : user.getPhone());
                    edtEmail.setText(isEmpty(user.getEmail()) ? "Chưa cập nhật" : user.getEmail());

                    String avatarUrl = prefs.getString("avatar_url", "");
                    if (!avatarUrl.isEmpty()) {
                        Glide.with(EditProfileActivity.this).load(avatarUrl).into(imgAvatar);
                    }
                } else {
                    Toast.makeText(EditProfileActivity.this, "Không tải được thông tin người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
                    if (imageUri != null) {
                        uploadAvatarToFirebase(imageUri);
                    }
                    prefs.edit().putString("user_name", name).apply();
                    Toast.makeText(EditProfileActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();

                } else {
                    Toast.makeText(EditProfileActivity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadAvatarToFirebase(Uri imageUri) {
        StorageReference storageRef = FirebaseStorage.getInstance()
                .getReference("avatars/" + username + ".jpg");

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            String downloadUrl = uri.toString();
                            prefs.edit().putString("avatar_url", downloadUrl).apply();
                            Glide.with(EditProfileActivity.this).load(downloadUrl).into(imgAvatar);
                        })
                        .addOnFailureListener(e -> Toast.makeText(this, "Lỗi URL", Toast.LENGTH_SHORT).show())
                )
                .addOnFailureListener(e -> Toast.makeText(this, "Upload thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
