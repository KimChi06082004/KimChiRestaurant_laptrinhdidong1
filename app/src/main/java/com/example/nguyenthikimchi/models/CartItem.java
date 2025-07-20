package com.example.nguyenthikimchi.models;

import java.io.Serializable;

public class CartItem implements Serializable {
    private String id;
    private String name;
    private String imageUrl;
    private double price;
    private int quantity;
    private boolean selected;

    private String size;
    private String topping;

    // Constructor mặc định cho Firebase
    public CartItem() {
    }

    public CartItem(String id, String name, String imageUrl, double price, int quantity) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
        this.quantity = quantity;
        this.selected = false;
    }

    public CartItem(String id, String name, String imageUrl, double price, int quantity, String size, String topping) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
        this.quantity = quantity;
        this.size = size;
        this.topping = topping;
        this.selected = false;
    }

    public CartItem(String id, String name, double price, int quantity) {
        this(id, name, "", price, quantity);
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean isSelected() {
        return selected;
    }

    public String getSize() {
        return size;
    }

    public String getTopping() {
        return topping;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setTopping(String topping) {
        this.topping = topping;
    }
}
