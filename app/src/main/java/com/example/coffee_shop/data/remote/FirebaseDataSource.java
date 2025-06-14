package com.example.coffee_shop.data.remote;

import com.example.coffee_shop.data.models.Coffee;
import com.example.coffee_shop.data.models.UserProfile;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseDataSource {
    private final DatabaseReference databaseReference;

    public FirebaseDataSource() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void getUserProfile(String userId, ValueEventListener listener) {
        databaseReference.child("Users").child(userId).addListenerForSingleValueEvent(listener);
    }

    public void isCoffeeFavorite(String userId, String coffeeId, ValueEventListener listener) {
        databaseReference.child("Users").child(userId).child("favorites").child(coffeeId)
                .addListenerForSingleValueEvent(listener);
    }

    public Task<Void> addCoffeeToFavorites(String userId, Coffee coffee) {
        return databaseReference.child("Users").child(userId).child("favorites")
                .child(coffee.getId()).setValue(coffee);
    }

    public Task<Void> removeCoffeeFromFavorites(String userId, String coffeeId) {
        return databaseReference.child("Users").child(userId).child("favorites").child(coffeeId)
                .removeValue();
    }

    public void getFavorites(String userId, ValueEventListener listener) {
        databaseReference.child("Users").child(userId).child("favorites")
                .addValueEventListener(listener);
    }

    public Task<Void> updateUserProfile(String userId, UserProfile userProfile) {
        return databaseReference.child("Users").child(userId).setValue(userProfile);
    }

    public Task<Void> updateUserProfileImage(String userId, String imageUrl) {
        return databaseReference.child("Users").child(userId).child("profileImage")
                .setValue(imageUrl);
    }

    public Task<Void> updateShippingAddress(String userId, String address) {
        return databaseReference.child("Users").child(userId).child("shippingAddress")
                .setValue(address);
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }
}
