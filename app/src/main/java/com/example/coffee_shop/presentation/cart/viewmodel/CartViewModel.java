package com.example.coffee_shop.presentation.cart.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop.data.models.CartItem;
import com.example.coffee_shop.data.models.Order;
import com.example.coffee_shop.data.models.ShippingAddress;
import com.example.coffee_shop.data.repository.CartRepository;
import com.example.coffee_shop.data.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.List;
import java.util.UUID;

public class CartViewModel extends AndroidViewModel {
    private final MutableLiveData<Integer> quantity = new MutableLiveData<>(1);
    private final MutableLiveData<String> selectedSize = new MutableLiveData<>("S");
    private final MutableLiveData<Boolean> isFavorite = new MutableLiveData<>(false);

    private final CartRepository cartRepository;

    public CartViewModel(@NonNull Application application) {
        super(application);
        cartRepository = new CartRepository(application);
        UserRepository userRepository = new UserRepository(application);
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

    public void placeOrder(double total, OrderCallback callback) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String orderId = UUID.randomUUID().toString();

        cartRepository.getAllCartItems().observeForever(cartItems -> {
            if (cartItems == null || cartItems.isEmpty()) {
                callback.onFailure("Cart is empty");
                return;
            }

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

                    Order order = new Order(
                            orderId,
                            uid,
                            System.currentTimeMillis(),
                            total,
                            cartItems,
                            address
                    );

                    DatabaseReference orderRef = FirebaseDatabase.getInstance()
                            .getReference("Orders")
                            .child(uid)
                            .child(orderId);

                    orderRef.setValue(order).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            clearCart();

                            String fullAddress = address.getAddress() + ", " + address.getCity() + ", " + address.getZip();
                            callback.onSuccess(orderId, address.getFullName(), address.getPhoneNumber(), fullAddress, address);                        } else {
                            callback.onFailure(task.getException() != null ? task.getException().getMessage() : "Unknown error");
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    callback.onFailure(error.getMessage());
                }
            });
        });
    }

    public interface OrderCallback {
        void onSuccess(String orderId, String fullName, String phoneNumber, String fullAddress, ShippingAddress address);
        void onFailure(String message);
    }
}