package com.example.coffee_shop.data.models;

import java.util.Map;

public class UserProfile {
    private String username;
    private String profileImage;
    private Map<String, String> shippingAddress;

    public UserProfile() {}

    public UserProfile(String username, String profileImage, Map<String, String> shippingAddress) {
        this.username = username;
        this.profileImage = profileImage;
        this.shippingAddress = shippingAddress;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }
    public Map<String, String> getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(Map<String, String> shippingAddress) { this.shippingAddress = shippingAddress; }
}