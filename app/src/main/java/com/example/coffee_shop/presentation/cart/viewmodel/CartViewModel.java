package com.example.coffee_shop.presentation.cart.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop.data.models.CartItem;
import com.example.coffee_shop.data.repository.CartRepository;

import java.util.List;

public class CartViewModel extends AndroidViewModel {
    private final MutableLiveData<Integer> quantity = new MutableLiveData<>(1);
    private final MutableLiveData<String> selectedSize = new MutableLiveData<>("S");
    private final MutableLiveData<Boolean> isFavorite = new MutableLiveData<>(false);

    private final CartRepository cartRepository;

    public CartViewModel(Application application) {
        super(application);
        cartRepository = new CartRepository(application);
    }

    public LiveData<List<CartItem>> getCartItems() {
        return cartRepository.getAllCartItems();
    }

    public void addToCart(CartItem item) {
        cartRepository.addToCart(item);
    }

    public void clearCart() {
        cartRepository.clearCart();
    }

    public void deleteCartItem(CartItem item) {
        cartRepository.removeFromCart(item);
    }
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