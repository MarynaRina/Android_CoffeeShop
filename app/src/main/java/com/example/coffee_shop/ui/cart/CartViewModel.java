package com.example.coffee_shop.ui.cart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
public class CartViewModel extends ViewModel {
    private final MutableLiveData<Integer> quantity = new MutableLiveData<>(1);
    private final MutableLiveData<String> selectedSize = new MutableLiveData<>("S");
    private final MutableLiveData<Boolean> isFavorite = new MutableLiveData<>(false);

    public LiveData<Integer> getQuantity() {
        return quantity;
    }

    public LiveData<String> getSelectedSize() {
        return selectedSize;
    }

    public LiveData<Boolean> getIsFavorite() {
        return isFavorite;
    }

    public void increaseQuantity() {
        quantity.setValue((quantity.getValue() != null ? quantity.getValue() : 1) + 1);
    }

    public void decreaseQuantity() {
        int current = quantity.getValue() != null ? quantity.getValue() : 1;
        if (current > 1) {
            quantity.setValue(current - 1);
        }
    }

    public void selectSize(String size) {
        selectedSize.setValue(size);
    }

    public void toggleFavorite() {
        isFavorite.setValue(!(isFavorite.getValue() != null && isFavorite.getValue()));
    }

    public void reset() {
        quantity.setValue(1);
        selectedSize.setValue("S");
        isFavorite.setValue(false);
    }
}