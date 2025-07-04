package com.example.nguyenthikimchi.models;

public class FoodItem {
    private String id;
    private String name;
    private String price;
    private String originalPrice;
    private String description;
    private String imageUrl;
    private String ingredients;
    private String portion;
    private String cookingTime;
    private String category;
    private int discountPercent;
    private boolean favorite = false;

    public FoodItem() {}

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getPrice() { return price; }
    public String getOriginalPrice() { return originalPrice; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public String getIngredients() { return ingredients; }
    public String getPortion() { return portion; }
    public String getCookingTime() { return cookingTime; }
    public String getCategory() { return category; }
    public int getDiscountPercent() { return discountPercent; }
    public boolean isFavorite() { return favorite; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrice(String price) { this.price = price; }
    public void setOriginalPrice(String originalPrice) { this.originalPrice = originalPrice; }
    public void setDescription(String description) { this.description = description; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }
    public void setPortion(String portion) { this.portion = portion; }
    public void setCookingTime(String cookingTime) { this.cookingTime = cookingTime; }
    public void setCategory(String category) { this.category = category; }
    public void setDiscountPercent(int discountPercent) { this.discountPercent = discountPercent; }
    public void setFavorite(boolean favorite) { this.favorite = favorite; }
}
