package com.example.coffee_shop.data.models;

import java.io.Serializable;
import java.util.Locale;

public class Coffee implements Serializable {
    private String id;
    private String imageUrl;
    private String category;
    private double price;
    private LocalizedText name;
    private LocalizedText description;

    public Coffee() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalizedText getName() {
        return name;
    }

    public void setName(LocalizedText name) {
        this.name = name;
    }

    public LocalizedText getDescription() {
        return description;
    }

    public void setDescription(LocalizedText description) {
        this.description = description;
    }

    public String getLocalizedName() {
        return name != null ? getCurrentLangText(name) : "";
    }

    public String getLocalizedDescription() {
        return description != null ? getCurrentLangText(description) : "";
    }

    public String getFormattedPrice() {
        return String.format(Locale.getDefault(), "$%.2f", price);
    }

    private String getCurrentLangText(LocalizedText text) {
        String lang = Locale.getDefault().getLanguage();
        switch (lang) {
            case "uk":
                return text.getUk() != null ? text.getUk() : text.getEn();
            case "pl":
                return text.getPl() != null ? text.getPl() : text.getEn();
            default:
                return text.getEn();
        }
    }
}