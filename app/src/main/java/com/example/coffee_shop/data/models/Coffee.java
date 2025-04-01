package com.example.coffee_shop.data.models;

import java.io.Serializable;

public class Coffee implements Serializable {
    private static final long serialVersionUID = 1L; // Додаємо UID для коректного серіалізування
    private String id;  // Ідентифікатор кави

    private String name;
    private double price;
    private String imageUrl;
    private String category;
    private String description;

    public Coffee() {
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public Coffee(String name, double price, String imageUrl, String category, String description) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
        this.description = description;
    }

    public String getFormattedPrice() {
        return String.format("$%.2f", price);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}