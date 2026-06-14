package com.example.fastfit.model;

/** A purchasable nutrition item shown on the nutrition screen. */
public class NutritionItem {
    private String id;
    private String name;
    private double price;
    private int calories;
    private int imageRes;     // local drawable
    private int quantity;     // selected qty (UI state)

    public NutritionItem() { }

    public NutritionItem(String id, String name, double price, int calories, int imageRes) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.calories = calories;
        this.imageRes = imageRes;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getCalories() { return calories; }
    public void setCalories(int calories) { this.calories = calories; }

    public int getImageRes() { return imageRes; }
    public void setImageRes(int imageRes) { this.imageRes = imageRes; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
