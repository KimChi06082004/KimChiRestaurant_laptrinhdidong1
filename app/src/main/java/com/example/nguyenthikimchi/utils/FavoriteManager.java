package com.example.nguyenthikimchi.utils;

import com.example.nguyenthikimchi.models.FoodItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FavoriteManager {
    private static final HashMap<String, FoodItem> favoriteMap = new HashMap<>();

    public static void toggleFavorite(FoodItem item) {
        if (favoriteMap.containsKey(item.getId())) {
            favoriteMap.remove(item.getId());
        } else {
            favoriteMap.put(item.getId(), item);
        }
    }

    public static boolean isFavorite(String id) {
        return favoriteMap.containsKey(id);
    }

    public static List<FoodItem> getFavoriteItems() {
        return new ArrayList<>(favoriteMap.values());
    }

    // ✅ Thêm phương thức này để tránh lỗi
    public static List<FoodItem> getFavorites() {
        return new ArrayList<>(favoriteMap.values());
    }

    public static void clearFavorites() {
        favoriteMap.clear();
    }
}
