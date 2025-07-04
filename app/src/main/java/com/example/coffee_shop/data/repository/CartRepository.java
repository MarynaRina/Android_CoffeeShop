package com.example.coffee_shop.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;

import com.example.coffee_shop.data.database.AppDatabase;
import com.example.coffee_shop.data.database.CartDao;
import com.example.coffee_shop.data.database.CartItemEntity;
import com.example.coffee_shop.data.models.CartItem;
import com.example.coffee_shop.data.models.Coffee;
import com.example.coffee_shop.data.repository.CoffeeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CartRepository {
    private final CartDao cartDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final CoffeeRepository coffeeRepository;

    public CartRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        cartDao = db.cartDao();
        coffeeRepository = new CoffeeRepository();
    }

    public LiveData<List<CartItem>> getAllCartItems() {
        MediatorLiveData<List<CartItem>> result = new MediatorLiveData<>();

        result.addSource(cartDao.getAllCartItems(), entities -> {
            executor.execute(() -> {
                List<CartItem> items = new ArrayList<>();
                for (CartItemEntity entity : entities) {
                    Coffee coffee = coffeeRepository.getCoffeeSync(entity.getCoffeeId());
                    items.add(new CartItem(
                            entity.getCoffeeId(),
                            entity.getSelectedSize(),
                            entity.getQuantity(),
                            entity.getPricePerOne(),
                            entity.isHasSugar(),
                            coffee
                    ));
                }
                result.postValue(items);
            });
        });

        return result;
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
                        item.getLocalizedName(),
                        item.getImageUrl(),
                        item.getSelectedSize(),
                        item.getQuantity(),
                        item.getPricePerOne(),
                        item.isHasSugar()
                ));
            }
        });
    }

    public void removeFromCart(CartItem item) {
        executor.execute(() -> {
            CartItemEntity entity = cartDao.getCartItem(item.getCoffeeId(), item.getSelectedSize());
            if (entity != null) {
                cartDao.deleteCartItem(entity);
            }
        });
    }

    public void clearCart() {
        executor.execute(cartDao::clearCart);
    }
}