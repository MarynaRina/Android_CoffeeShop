package com.example.coffee_shop.data.models;

import java.util.List;

public class Order {
    private String id;
    private String userId;
    private long timestamp;
    private double totalPrice;
    private List<CartItem> items;
    private ShippingAddress shippingAddress;

    public Order() {}

    public Order(String id, String userId, long timestamp, double totalPrice,
                 List<CartItem> items, ShippingAddress shippingAddress) {
        this.id = id;
        this.userId = userId;
        this.timestamp = timestamp;
        this.totalPrice = totalPrice;
        this.items = items;
        this.shippingAddress = shippingAddress;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public ShippingAddress getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(ShippingAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

}
