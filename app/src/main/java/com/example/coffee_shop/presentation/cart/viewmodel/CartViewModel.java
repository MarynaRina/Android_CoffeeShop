package com.example.coffee_shop.presentation.cart.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.coffee_shop.data.models.CartItem;
import com.example.coffee_shop.data.models.Order;
import com.example.coffee_shop.data.models.ShippingAddress;
import com.example.coffee_shop.data.repository.CartRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CartViewModel extends AndroidViewModel {
    private final MutableLiveData<Integer> quantity = new MutableLiveData<>(1);
    private final MutableLiveData<String> selectedSize = new MutableLiveData<>("S");
    private final MutableLiveData<Boolean> isFavorite = new MutableLiveData<>(false);

    private final CartRepository cartRepository;
    private final FirebaseAuth firebaseAuth;

    public CartViewModel(@NonNull Application application) {
        super(application);
        this.cartRepository = new CartRepository(application);
        this.firebaseAuth = FirebaseAuth.getInstance();
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
        int current = quantity.getValue() != null ? quantity.getValue() : 1;
        quantity.setValue(current + 1);
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
        boolean current = isFavorite.getValue() != null && isFavorite.getValue();
        isFavorite.setValue(!current);
    }

    public void reset() {
        quantity.setValue(1);
        selectedSize.setValue("S");
        isFavorite.setValue(false);
    }

    public void resetUIState() {
        // Скидаємо тільки UI стан, не корзину
        quantity.setValue(1);
        selectedSize.setValue("S");
        isFavorite.setValue(false);
    }

    public void resetOrderState() {
        // Очищаємо стан після замовлення
        reset();
    }

    public void placeOrder(double total, OrderCallback callback) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            callback.onFailure("User not logged in");
            return;
        }

        LiveData<List<CartItem>> cartItems = getCartItems();

        Observer<List<CartItem>> observer = new Observer<List<CartItem>>() {
            @Override
            public void onChanged(List<CartItem> items) {
                // ВАЖЛИВО: видаляємо observer одразу після отримання даних
                cartItems.removeObserver(this);

                if (items == null || items.isEmpty()) {
                    callback.onFailure("Cart empty");
                    return;
                }

                List<CartItem> itemsCopy = new ArrayList<>(items);

                fetchShippingAddress(currentUser.getUid(), new AddressCallback() {
                    @Override
                    public void onSuccess(ShippingAddress address) {
                        createOrderInDatabase(currentUser.getUid(), itemsCopy, address, total, callback);
                    }

                    @Override
                    public void onFailure(String error) {
                        callback.onFailure(error);
                    }
                });
            }
        };

        cartItems.observeForever(observer);
    }

    private void fetchShippingAddress(String uid, AddressCallback callback) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(uid)
                .child("shippingAddress");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    callback.onFailure("Shipping address not found");
                    return;
                }

                ShippingAddress address = snapshot.getValue(ShippingAddress.class);
                if (address == null) {
                    callback.onFailure("Invalid shipping address data");
                    return;
                }

                callback.onSuccess(address);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    private void createOrderInDatabase(String uid, List<CartItem> cartItems, ShippingAddress address, double total, OrderCallback callback) {
        String orderId = UUID.randomUUID().toString();

        Order order = new Order(
                orderId,
                uid,
                System.currentTimeMillis(),
                total,
                cartItems,
                address,
                "pending"
        );

        DatabaseReference orderRef = FirebaseDatabase.getInstance()
                .getReference("Orders")
                .child(uid)
                .child(orderId);

        orderRef.setValue(order).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // ТІЛЬКИ ПІСЛЯ успішного збереження очищаємо корзину
                clearCart();
                String fullAddress = address.getAddress() + ", " + address.getCity() + ", " + address.getZip();
                callback.onSuccess(orderId, address.getFullName(), address.getPhoneNumber(), fullAddress, address);
            } else {
                String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                callback.onFailure("Failed to save order: " + errorMessage);
            }
        });
    }

    private interface AddressCallback {
        void onSuccess(ShippingAddress address);
        void onFailure(String error);
    }

    public interface OrderCallback {
        void onSuccess(String orderId, String fullName, String phoneNumber, String fullAddress, ShippingAddress address);
        void onFailure(String message);
    }
}