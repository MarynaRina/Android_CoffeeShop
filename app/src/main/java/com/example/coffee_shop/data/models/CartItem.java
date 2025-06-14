package com.example.coffee_shop.data.models;

import java.io.Serializable;

public class CartItem implements Serializable {
    private final String coffeeId;
    private final String coffeeName;
    private final String coffeeImageUrl;
    private final String selectedSize;
    private final int quantity;
    private final double pricePerOne;
    private final double totalPrice;
    private boolean hasSugar;

    public CartItem(String coffeeId, String coffeeName, String coffeeImageUrl, String selectedSize, int quantity, double pricePerOne, boolean hasSugar) {
        this.coffeeId = coffeeId;
        this.coffeeName = coffeeName;
        this.coffeeImageUrl = coffeeImageUrl;
        this.selectedSize = selectedSize;
        this.quantity = quantity;
        this.pricePerOne = pricePerOne;
        this.totalPrice = pricePerOne * quantity;
        this.hasSugar = hasSugar;
    }
    public String getCoffeeId() { return coffeeId; }
    public String getCoffeeName() { return coffeeName; }
    public String getCoffeeImageUrl() { return coffeeImageUrl; }
    public String getSelectedSize() { return selectedSize; }
    public int getQuantity() { return quantity; }
    public double getPricePerOne() { return pricePerOne; }
    public double getTotalPrice() { return totalPrice; }
    public boolean isHasSugar() { return hasSugar; }
    public void setHasSugar(boolean hasSugar) { this.hasSugar = hasSugar; }

}