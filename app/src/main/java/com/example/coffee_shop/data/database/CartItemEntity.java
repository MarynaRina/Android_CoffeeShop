package com.example.coffee_shop.data.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(tableName = "cart_items", primaryKeys = {"coffeeId", "selectedSize"})
public class CartItemEntity {
    @NonNull
    private String coffeeId;
    private String coffeeName;
    private String coffeeImageUrl;
    @NonNull
    private String selectedSize;
    private int quantity;
    private double pricePerOne;
    private double totalPrice;
    private boolean hasSugar;


    public CartItemEntity(@NonNull String coffeeId, String coffeeName, String coffeeImageUrl,
                          @NonNull String selectedSize, int quantity, double pricePerOne, boolean hasSugar) {
        this.coffeeId = coffeeId;
        this.coffeeName = coffeeName;
        this.coffeeImageUrl = coffeeImageUrl;
        this.selectedSize = selectedSize;
        this.quantity = quantity;
        this.pricePerOne = pricePerOne;
        this.totalPrice = pricePerOne * quantity;
        this.hasSugar = hasSugar;
    }

    @NonNull
    public String getCoffeeId() { return coffeeId; }
    public void setCoffeeId(@NonNull String coffeeId) { this.coffeeId = coffeeId; }

    public String getCoffeeName() { return coffeeName; }
    public void setCoffeeName(String coffeeName) { this.coffeeName = coffeeName; }

    public String getCoffeeImageUrl() { return coffeeImageUrl; }
    public void setCoffeeImageUrl(String coffeeImageUrl) { this.coffeeImageUrl = coffeeImageUrl; }

    @NonNull
    public String getSelectedSize() { return selectedSize; }
    public void setSelectedSize(@NonNull String selectedSize) { this.selectedSize = selectedSize; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.totalPrice = pricePerOne * quantity;
    }

    public double getPricePerOne() { return pricePerOne; }
    public void setPricePerOne(double pricePerOne) {
        this.pricePerOne = pricePerOne;
        this.totalPrice = pricePerOne * quantity;
    }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public boolean isHasSugar() { return hasSugar; }
    public void setHasSugar(boolean hasSugar) { this.hasSugar = hasSugar; }
}