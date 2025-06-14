package com.example.coffee_shop.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CartDao {
    @Query("SELECT * FROM cart_items")
    LiveData<List<CartItemEntity>> getAllCartItems();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCartItem(CartItemEntity item);

    @Delete
    void deleteCartItem(CartItemEntity item);

    @Query("DELETE FROM cart_items")
    void clearCart();

    @Query("SELECT * FROM cart_items WHERE coffeeId = :id AND selectedSize = :size")
    CartItemEntity getCartItem(String id, String size);

    @Query("UPDATE cart_items SET quantity = :quantity, totalPrice = :quantity * pricePerOne WHERE coffeeId = :id AND selectedSize = :size")
    void updateQuantity(String id, String size, int quantity);
}