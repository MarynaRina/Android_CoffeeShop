package com.example.coffee_shop.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.coffee_shop.data.database.AppDatabase;
import com.example.coffee_shop.data.database.CartDao;
import com.example.coffee_shop.data.database.CartItemEntity;
import com.example.coffee_shop.data.models.CartItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CartRepository {
    private final CartDao cartDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    public void removeFromCart(CartItem item) {
        executor.execute(() -> {
            CartItemEntity entity = cartDao.getCartItem(item.getCoffeeId(), item.getSelectedSize());
            if (entity != null) {
                cartDao.deleteCartItem(entity);
            }
        });
    }
    public CartRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        cartDao = db.cartDao();
    }

    public LiveData<List<CartItem>> getAllCartItems() {
        return Transformations.map(cartDao.getAllCartItems(), entities -> {
            List<CartItem> items = new ArrayList<>();
            for (CartItemEntity entity : entities) {
                items.add(new CartItem(
                        entity.getCoffeeId(),
                        entity.getCoffeeName(),
                        entity.getCoffeeImageUrl(),
                        entity.getSelectedSize(),
                        entity.getQuantity(),
                        entity.getPricePerOne(),
                        entity.isHasSugar()

                ));
            }
            return items;
        });
    }

    public void addToCart(CartItem item) {
        executor.execute(() -> {
            CartItemEntity existingItem = cartDao.getCartItem(item.getCoffeeId(), item.getSelectedSize());
            if (existingItem != null) {
                int newQuantity = existingItem.getQuantity() + item.getQuantity();
                cartDao.updateQuantity(item.getCoffeeId(), item.getSelectedSize(), newQuantity);
            } else {
                cartDao.insertCartItem(new CartItemEntity(
                        item.getCoffeeId(),
                        item.getCoffeeName(),
                        item.getCoffeeImageUrl(),
                        item.getSelectedSize(),
                        item.getQuantity(),
                        item.getPricePerOne(),
                        item.isHasSugar()
                ));
            }
        });
    }

    public void clearCart() {
        executor.execute(cartDao::clearCart);
    }
}