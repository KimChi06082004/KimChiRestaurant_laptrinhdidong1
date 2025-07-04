package com.example.nguyenthikimchi.utils;

import com.example.nguyenthikimchi.models.CartItem;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static final List<CartItem> cartItems = new ArrayList<>();

    public static void addToCart(String id, String name, double price, int quantity) {
        for (CartItem item : cartItems) {
            if (item.getId().equals(id)) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        cartItems.add(new CartItem(id, name, price, quantity));
    }


    public static List<CartItem> getCartItems() {
        return cartItems;
    }

    public static double getTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getPrice() * item.getQuantity();
        }
        return total;
    }

    public static int getTotalQuantity() {
        int total = 0;
        for (CartItem item : cartItems) {
            total += item.getQuantity();
        }
        return total;
    }

    public static void clearCart() {
        cartItems.clear();
    }
}
