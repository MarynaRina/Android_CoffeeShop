package com.example.coffee_shop.models;

public class Coffee {
    private String name;
    private double price; // Змінено на double
    private String imageUrl;
    private String category;

    public Coffee() {
    }

    public Coffee(String name, double price, String imageUrl, String category) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    public String getFormattedPrice() {
        return String.format("$%.2f", price); // Форматування до двох знаків після крапки
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() { // Змінено тип на double
        return price;
    }

    public void setPrice(double price) { // Змінено тип на double
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
}
