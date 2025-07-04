package com.example.coffee_shop.data.models;

import android.content.Context;
import android.util.Log;

import com.example.coffee_shop.R;

import java.io.Serializable;

public class CartItem implements Serializable {
    private final String coffeeId;
    private final String selectedSize;
    private final int quantity;
    private final double pricePerOne;
    private final double totalPrice;
    private final boolean hasSugar;
    private final Coffee coffee;

    public CartItem(String coffeeId, String selectedSize, int quantity, double pricePerOne, boolean hasSugar, Coffee coffee) {
        this.coffeeId = coffeeId;
        this.selectedSize = selectedSize;
        this.quantity = quantity;
        this.pricePerOne = pricePerOne;
        this.totalPrice = pricePerOne * quantity;
        this.hasSugar = hasSugar;
        this.coffee = coffee;
    }

    public String getCoffeeId() {
        return coffeeId;
    }

    public String getSelectedSize() {
        return selectedSize;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPricePerOne() {
        return pricePerOne;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public boolean isHasSugar() {
        return hasSugar;
    }

    public Coffee getCoffee() {
        return coffee;
    }

    public String getLocalizedName() {
        return coffee != null ? coffee.getLocalizedName() : "";
    }

    public String getLocalizedDescription() {
        return coffee != null ? coffee.getLocalizedDescription() : "";
    }

    public String getImageUrl() {
        return coffee != null ? coffee.getImageUrl() : "";
    }

    public String getFormattedTotalPrice() {
        return String.format("$%.2f", totalPrice);
    }

    public String getFormattedPricePerOne() {
        return String.format("$%.2f", pricePerOne);
    }

    public String getLocalizedSizeText(Context context) {
        return context.getString(R.string.size_format, selectedSize);
    }

    public String getLocalizedSugarText(Context context) {
        return context.getString(hasSugar ? R.string.with_sugar : R.string.without_sugar);
    }
}