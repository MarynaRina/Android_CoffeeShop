package com.example.coffee_shop.data.models;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class Coffee implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    // Поля для локалізованих назв і описів
    private Map<String, String> names;
    private Map<String, String> descriptions;
    // Поля для базової версії (якщо Map не використовується)
    private String name;
    private String description;
    // Інші поля
    private double price;
    private String imageUrl;
    private String category;

    // Порожній конструктор
    public Coffee() {
    }

    // Стандартний конструктор
    public Coffee(String name, double price, String imageUrl, String category, String description) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Метод для отримання локалізованого імені
    public String getName() {
        // Якщо є локалізовані імена, використовуємо їх
        if (names != null && !names.isEmpty()) {
            String currentLang = Locale.getDefault().getLanguage();
            return names.containsKey(currentLang) ? names.get(currentLang) : names.get("en");
        }
        // Інакше використовуємо базову версію
        return name;
    }

    // Метод для отримання локалізованого опису
    public String getDescription() {
        // Якщо є локалізовані описи, використовуємо їх
        if (descriptions != null && !descriptions.isEmpty()) {
            String currentLang = Locale.getDefault().getLanguage();
            return descriptions.containsKey(currentLang) ? descriptions.get(currentLang) : descriptions.get("en");
        }
        // Інакше використовуємо базову версію
        return description;
    }

    public String getFormattedPrice() {
        return String.format("$%.2f", price);
    }

    // Геттери і сеттери для всіх полів
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

    public void setDescription(String description) {
        this.description = description;
    }

    // Геттери і сеттери для Map з локалізованими даними
    public Map<String, String> getNames() {
        return names;
    }

    public void setNames(Map<String, String> names) {
        this.names = names;
    }

    public Map<String, String> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(Map<String, String> descriptions) {
        this.descriptions = descriptions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coffee)) return false;
        Coffee coffee = (Coffee) o;
        return Double.compare(coffee.price, price) == 0 &&
                Objects.equals(id, coffee.id) &&
                Objects.equals(name, coffee.name) &&
                Objects.equals(imageUrl, coffee.imageUrl) &&
                Objects.equals(category, coffee.category) &&
                Objects.equals(description, coffee.description);
    }
}