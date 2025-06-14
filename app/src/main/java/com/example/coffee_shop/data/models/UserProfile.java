package com.example.coffee_shop.data.models;

import androidx.annotation.NonNull;

public class UserProfile {
    private String username;
    private String email;
    private String profileImage;
    private String phoneNumber;
    private String shippingAddress;

    // Необхідний публічний порожній конструктор для Firebase
    public UserProfile() {}

    // Конструктор з усіма полями (не обов'язковий, можна видалити)
    public UserProfile(String username, String email, String profileImage, String shippingAddress) {
        this.username = username;
        this.email = email;
        this.profileImage = profileImage;
        this.shippingAddress = shippingAddress;
    }

    // Геттери та сеттери (обов’язково для Firebase)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    @NonNull
    @Override
    public String toString() {
        return "UserProfile{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", profileImage='" + profileImage + '\'' +
                ", shippingAddress='" + shippingAddress + '\'' +
                '}';
    }
}