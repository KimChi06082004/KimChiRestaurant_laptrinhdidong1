package com.example.nguyenthikimchi;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nguyenthikimchi.adapters.OrderAdapter;
import com.example.nguyenthikimchi.models.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayout layoutEmptyOrder;
    private OrderAdapter adapter;
    private List<Order> orderList = new ArrayList<>();
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarDetail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Lịch sử đơn hàng");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Ánh xạ view
        recyclerView = findViewById(R.id.recyclerViewOrder);
        layoutEmptyOrder = findViewById(R.id.layoutEmptyOrder);

        SharedPreferences prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
        userId = prefs.getString("userId", "defaultUser");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderAdapter(this, orderList);
        recyclerView.setAdapter(adapter);

        loadOrders();
    }

    private void loadOrders() {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("orders")
                .child(userId);

        ref.get().addOnSuccessListener(snapshot -> {
            orderList.clear();
            for (DataSnapshot child : snapshot.getChildren()) {
                Order order = child.getValue(Order.class);
                if (order != null) {
                    orderList.add(order);
                }
            }

            if (orderList.isEmpty()) {
                layoutEmptyOrder.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                layoutEmptyOrder.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Không thể tải đơn hàng", Toast.LENGTH_SHORT).show();
        });
    }
}
