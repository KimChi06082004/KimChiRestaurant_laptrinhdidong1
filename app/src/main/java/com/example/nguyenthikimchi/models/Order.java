package com.example.nguyenthikimchi.models;

import java.util.List;

public class Order {
    private String orderId;
    private String userId;
    private List<CartItem> items;
    private double totalAmount;
    private long timestamp;

    public Order() {
    }

    public Order(String orderId, String userId, List<CartItem> items, double totalAmount, long timestamp) {
        this.orderId = orderId;
        this.userId = userId;
        this.items = items;
        this.totalAmount = totalAmount;
        this.timestamp = timestamp;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getUserId() {
        return userId;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
