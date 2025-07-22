package com.example.nguyenthikimchi.utils;

import com.example.nguyenthikimchi.models.FoodItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FavoriteManager {
    private static final HashMap<String, FoodItem> favoriteMap = new HashMap<>();

    // Thêm hoặc xóa khỏi yêu thích (toggle)
    public static void toggleFavorite(FoodItem item) {
        if (favoriteMap.containsKey(item.getId())) {
            favoriteMap.remove(item.getId());
        } else {
            favoriteMap.put(item.getId(), item);
        }
    }

    // ✅ Thêm mới: chỉ thêm
    public static void addFavorite(FoodItem item) {
        favoriteMap.put(item.getId(), item);
    }

    // ✅ Thêm mới: chỉ xóa
    public static void removeFavorite(String id) {
        favoriteMap.remove(id);
    }

    // Kiểm tra món có được yêu thích không
    public static boolean isFavorite(String id) {
        return favoriteMap.containsKey(id);
    }

    // Lấy danh sách món yêu thích
    public static List<FoodItem> getFavorites() {
        return new ArrayList<>(favoriteMap.values());
    }

    // Xóa tất cả
    public static void clearFavorites() {
        favoriteMap.clear();
    }
}
