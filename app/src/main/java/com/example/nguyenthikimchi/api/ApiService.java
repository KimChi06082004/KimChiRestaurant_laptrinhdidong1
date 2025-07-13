package com.example.nguyenthikimchi.api;

import com.example.nguyenthikimchi.models.FoodItem;
import com.example.nguyenthikimchi.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // 🟩 Đăng ký tài khoản
    @POST("users")
    Call<User> register(@Body User user);

    // 🟦 Đăng nhập (lọc theo username và password)
    @GET("users")
    Call<List<User>> login(
            @Query("username") String username,
            @Query("password") String password
    );

    // ✅ Lấy user theo username
    @GET("users")
    Call<List<User>> getUserByUsername(@Query("username") String username);

    // ✅ Lấy user theo ID
    @GET("users/{id}")
    Call<User> getUserById(@Path("id") String id);

    // 🟨 Cập nhật thông tin người dùng
    @PUT("users/{id}")
    Call<User> updateUser(@Path("id") String id, @Body User user);

    // 🟥 Xóa tài khoản
    @DELETE("users/{id}")
    Call<Void> deleteUser(@Path("id") String id);

    // 🍽️ Danh sách món ăn
    @GET("foods")
    Call<List<FoodItem>> getAllFoods(
            @Query("page") int page,
            @Query("limit") int limit
    );

    // 🍲 Chi tiết món ăn
    @GET("foods/{id}")
    Call<FoodItem> getFoodById(@Path("id") String id);
    @GET("foods")  // Tùy URL bạn tạo trên mockAPI
    Call<List<FoodItem>> getFoodItems();
}
