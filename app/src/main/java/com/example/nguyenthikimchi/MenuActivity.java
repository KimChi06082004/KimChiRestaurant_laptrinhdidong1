package com.example.nguyenthikimchi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nguyenthikimchi.adapters.MenuFoodAdapter;
import com.example.nguyenthikimchi.api.ApiService;
import com.example.nguyenthikimchi.api.RetrofitClient;
import com.example.nguyenthikimchi.models.FoodItem;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuActivity extends AppCompatActivity {

    private RecyclerView menuRecyclerView;
    private MenuFoodAdapter adapter;
    private final List<FoodItem> menuItems = new ArrayList<>();
    private boolean isLoading = false;
    private int currentPage = 1;
    private final int pageSize = 10;

    // Danh sách toàn bộ món (đã lấy hết một lần từ API)
    private List<FoodItem> allFoods = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        menuRecyclerView = findViewById(R.id.menuRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        menuRecyclerView.setLayoutManager(layoutManager);

        adapter = new MenuFoodAdapter(this, menuItems, foodItem -> {
            Intent intent = new Intent(MenuActivity.this, ProductDetailActivity.class);
            intent.putExtra("foodId", foodItem.getId());
            startActivity(intent);
        });
        menuRecyclerView.setAdapter(adapter);

        // Gọi API để lấy tất cả dữ liệu 1 lần
        loadAllFoods();

        // Xử lý load thêm khi cuộn
        menuRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (!isLoading && layoutManager.findLastCompletelyVisibleItemPosition() >= menuItems.size() - 1) {
                    isLoading = true;
                    loadNextPage();
                }
            }
        });
    }

    // Gọi API chỉ một lần để lấy toàn bộ dữ liệu
    private void loadAllFoods() {
        ApiService api = RetrofitClient.getRetrofit().create(ApiService.class);
        api.getAllFoods(1, 100).enqueue(new Callback<List<FoodItem>>() {
            @Override
            public void onResponse(Call<List<FoodItem>> call, Response<List<FoodItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allFoods = response.body();
                    currentPage = 1;
                    loadNextPage();
                } else {
                    Toast.makeText(MenuActivity.this, "Không thể tải dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<FoodItem>> call, Throwable t) {
                Toast.makeText(MenuActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    // Load từng trang từ dữ liệu đã lấy
    private void loadNextPage() {
        int fromIndex = (currentPage - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, allFoods.size());

        if (fromIndex < allFoods.size()) {
            menuItems.addAll(allFoods.subList(fromIndex, toIndex));
            adapter.notifyDataSetChanged();
            currentPage++;
        } else {
            Toast.makeText(MenuActivity.this, "Không còn dữ liệu", Toast.LENGTH_SHORT).show();
        }

        isLoading = false;
    }
}
