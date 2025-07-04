package com.example.coffee_shop.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop.data.models.Coffee;
import com.example.coffee_shop.data.models.UserProfile;
import com.example.coffee_shop.data.remote.CloudinaryDataSource;
import com.example.coffee_shop.data.remote.FirebaseDataSource;
import com.example.coffee_shop.core.managers.CloudinaryManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    private final FirebaseDataSource firebase;
    private final CloudinaryDataSource cloudinary;
    private final Context ctx;

    public UserRepository(Context context) {
        ctx        = context.getApplicationContext();
        firebase   = new FirebaseDataSource();
        cloudinary = new CloudinaryDataSource(CloudinaryManager.getInstance(context));
    }

    public LiveData<UserProfile> getUserProfile(String uid) {
        MutableLiveData<UserProfile> data = new MutableLiveData<>();
        firebase.getUserProfile(uid, new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot s) {
                data.setValue(s.exists() ? s.getValue(UserProfile.class) : null);
            }
            @Override public void onCancelled(@NonNull DatabaseError e) { data.setValue(null); }
        });
        return data;
    }

    public void isCoffeeFavorite(String uid, String cid, OnFavoriteCheck cb) {
        firebase.isCoffeeFavorite(uid, cid, new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot s) { cb.onResult(s.exists()); }
            @Override public void onCancelled(@NonNull DatabaseError e) { cb.onError(e.getMessage()); }
        });
    }

    public interface OnFavoriteCheck { void onResult(boolean fav); void onError(String e); }

    public void addCoffeeToFavorites(String uid, Coffee coffee, OnOp cb) {
        firebase.getDatabaseReference()
                .child("Users").child(uid).child("favorites").child(coffee.getId())
                .setValue(coffee)
                .addOnCompleteListener(t -> {
                    if (t.isSuccessful()) cb.onSuccess();
                    else cb.onError(t.getException().getMessage());
                });
    }

    public LiveData<List<Coffee>> getFavorites(String uid) {
        MutableLiveData<List<Coffee>> data = new MutableLiveData<>();
        firebase.getFavorites(uid, new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot s) {
                List<Coffee> list = new ArrayList<>();
                for (DataSnapshot snap : s.getChildren()) {
                    Coffee c = snap.getValue(Coffee.class);
                    if (c != null) list.add(c);
                }
                data.setValue(list);
            }
            @Override public void onCancelled(@NonNull DatabaseError e) { data.setValue(null); }
        });
        return data;
    }

    public void removeCoffeeFromFavorites(String uid, String cid, OnOp cb) {
        firebase.removeCoffeeFromFavorites(uid, cid)
                .addOnCompleteListener(t -> {
                    if (t.isSuccessful()) cb.onSuccess();
                    else cb.onError(t.getException().getMessage());
                });
    }

    public interface OnOp { void onSuccess(); void onError(String e); }

    public void updateUserProfile(String uid, UserProfile profile, OnOp cb) {
        firebase.updateUserProfile(uid, profile)
                .addOnCompleteListener(t -> {
                    if (t.isSuccessful()) cb.onSuccess();
                    else cb.onError(t.getException().getMessage());
                });
    }

    public LiveData<String> uploadProfileImage(String uid, Uri uri) {
        MutableLiveData<String> urlLive = new MutableLiveData<>();
        cloudinary.uploadImage(uri, new CloudinaryDataSource.CustomUploadCallback() {
            @Override public void onStart(String id) {}
            @Override public void onProgress(String id,long b,long t){}
            @Override public void onSuccess(String url) {
                firebase.updateUserProfileImage(uid, url)
                        .addOnCompleteListener(t -> urlLive.setValue(t.isSuccessful() ? url : null));
            }
            @Override public void onError(String e) { urlLive.setValue(null); }
        });
        return urlLive;
    }

    public void updateUsernameOnly(String uid, String name, OnOp cb) {
        firebase.getDatabaseReference().child("Users").child(uid).child("username")
                .setValue(name)
                .addOnCompleteListener(t -> {
                    if (t.isSuccessful()) cb.onSuccess();
                    else cb.onError(t.getException().getMessage());
                });
    }

    public void updateShippingAddress(String uid, String fullName, String address, String city, String zip, String phone, OnOp cb) {
        Map<String, Object> shippingMap = new HashMap<>();
        shippingMap.put("fullName", fullName);
        shippingMap.put("address", address);
        shippingMap.put("city", city);
        shippingMap.put("phoneNumber", phone);
        shippingMap.put("zip", zip);

        firebase.getDatabaseReference()
                .child("Users")
                .child(uid)
                .child("shippingAddress")
                .setValue(shippingMap)
                .addOnCompleteListener(t -> {
                    if (t.isSuccessful()) cb.onSuccess();
                    else cb.onError(t.getException().getMessage());
                });
    }

    public void saveLanguagePreference(String code, OnOp cb) {
        try {
            SharedPreferences p = ctx.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
            p.edit().putString("language", code).apply();
            cb.onSuccess();
        } catch (Exception e) { cb.onError(e.getMessage()); }
    }

    public String getCurrentLanguage() {
        return ctx.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                .getString("language", "en");
    }
}