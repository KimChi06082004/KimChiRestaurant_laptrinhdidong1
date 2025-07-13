package com.example.nguyenthikimchi.utils;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.nguyenthikimchi.models.CartItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CartManagerFirebase {

    private static DatabaseReference getCartRef(String userId) {
        return FirebaseDatabase.getInstance().getReference("cart").child(userId);
    }

    public static void addToCart(Context context, String userId, CartItem item) {
        DatabaseReference cartRef = getCartRef(userId);

        cartRef.orderByChild("name").equalTo(item.getName())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean exists = false;
                        for (DataSnapshot data : snapshot.getChildren()) {
                            CartItem existing = data.getValue(CartItem.class);
                            if (existing != null && existing.getName().equals(item.getName())) {
                                int newQuantity = existing.getQuantity() + item.getQuantity();
                                data.getRef().child("quantity").setValue(newQuantity);
                                exists = true;
                                break;
                            }
                        }
                        if (!exists) {
                            String key = cartRef.push().getKey();
                            item.setId(key);
                            cartRef.child(key).setValue(item);
                        }
                        Toast.makeText(context, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "Lỗi khi thêm giỏ hàng", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static void getCartItems(String userId, Consumer<List<CartItem>> callback) {
        DatabaseReference cartRef = getCartRef(userId);
        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<CartItem> list = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    CartItem item = data.getValue(CartItem.class);
                    list.add(item);
                }
                callback.accept(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.accept(new ArrayList<>());
            }
        });
    }

    public static void removeItem(String userId, String itemId, Runnable onSuccess) {
        DatabaseReference cartRef = getCartRef(userId);
        cartRef.child(itemId).removeValue()
                .addOnSuccessListener(aVoid -> onSuccess.run());
    }

    public static void clearCart(String userId, Runnable onSuccess) {
        DatabaseReference cartRef = getCartRef(userId);
        cartRef.removeValue().addOnSuccessListener(aVoid -> onSuccess.run());
    }
}
