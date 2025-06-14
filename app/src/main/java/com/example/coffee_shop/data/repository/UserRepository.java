package com.example.coffee_shop.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.cloudinary.android.callback.ErrorInfo;
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
import java.util.Map;

public class UserRepository {
    private final FirebaseDataSource firebaseDataSource;
    private final CloudinaryDataSource cloudinaryDataSource;
    private final Context context;

    public UserRepository(Context context) {
        this.context = context.getApplicationContext();
        // Ініціалізуємо FirebaseDataSource та CloudinaryDataSource
        this.firebaseDataSource = new FirebaseDataSource();
        this.cloudinaryDataSource = new CloudinaryDataSource(CloudinaryManager.getInstance(context));
    }

    // Отримання профілю користувача
    public LiveData<UserProfile> getUserProfile(String userId) {
        MutableLiveData<UserProfile> userProfileLiveData = new MutableLiveData<>();

        firebaseDataSource.getUserProfile(userId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("DEBUG", "onDataChange() called, snapshot.exists(): " + snapshot.exists());
                if (snapshot.exists()) {
                    UserProfile userProfile = snapshot.getValue(UserProfile.class);
                    Log.d("DEBUG", "Loaded profile: " + userProfile);
                    userProfileLiveData.setValue(userProfile);
                } else {
                    Log.w("DEBUG", "Snapshot for user profile does not exist");
                    userProfileLiveData.setValue(null);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                userProfileLiveData.setValue(null);
            }
        });
        return userProfileLiveData;
    }

    // Перевірка, чи є кава улюбленою
    public void isCoffeeFavorite(String userId, String coffeeId, OnFavoriteCheckCallback callback) {
        firebaseDataSource.isCoffeeFavorite(userId, coffeeId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                callback.onResult(snapshot.exists());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }

    public interface OnFavoriteCheckCallback {
        void onResult(boolean isFavorite);
        void onError(String error);
    }

    // Додавання кави до улюблених
// Додавання кави до улюблених
    public void addCoffeeToFavorites(String userId, Coffee coffee, OnOperationCallback callback) {
        firebaseDataSource.getDatabaseReference()
                .child("Users")
                .child(userId)
                .child("favorites")
                .child(coffee.getId())
                .setValue(coffee)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Firebase", "Кава успішно додана в улюблені!");
                        callback.onSuccess();
                    } else {
                        Log.e("Firebase", "Помилка при додаванні кави в улюблені: " + task.getException().getMessage());
                        callback.onError(task.getException().getMessage());
                    }
                });
    }
    // Отримання списку улюблених кав
    public LiveData<List<Coffee>> getFavorites(String userId) {
        MutableLiveData<List<Coffee>> favoritesLiveData = new MutableLiveData<>();

        firebaseDataSource.getFavorites(userId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Coffee> favoritesList = new ArrayList<>();
                for (DataSnapshot coffeeSnapshot : snapshot.getChildren()) {
                    Coffee coffee = coffeeSnapshot.getValue(Coffee.class);
                    if (coffee != null) {
                        favoritesList.add(coffee);
                    }
                }
                favoritesLiveData.setValue(favoritesList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                favoritesLiveData.setValue(null);
            }
        });
        return favoritesLiveData;
    }

    // Видалення кави з улюблених
    public void removeCoffeeFromFavorites(String userId, String coffeeId, OnOperationCallback callback) {
        firebaseDataSource.removeCoffeeFromFavorites(userId, coffeeId)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Firebase", "Кава успішно видалена з улюблених!");
                        callback.onSuccess();
                    } else {
                        Log.e("Firebase", "Помилка при видаленні кави з улюблених: " + task.getException().getMessage());
                        callback.onError(task.getException().getMessage());
                    }
                });
    }

    public interface OnOperationCallback {
        void onSuccess();
        void onError(String error);
    }

    // Оновлення профілю користувача
    public void updateUserProfile(String userId, UserProfile userProfile, OnOperationCallback callback) {
        firebaseDataSource.updateUserProfile(userId, userProfile)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Firebase", "Профіль користувача успішно оновлено!");
                        callback.onSuccess();
                    } else {
                        Log.e("Firebase", "Помилка при оновленні профілю: " + task.getException().getMessage());
                        callback.onError(task.getException().getMessage());
                    }
                });
    }

    // Завантаження зображення профілю
    public LiveData<String> uploadProfileImage(String userId, Uri imageUri) {
        MutableLiveData<String> imageUrlLiveData = new MutableLiveData<>();
        cloudinaryDataSource.uploadImage(imageUri, new CloudinaryDataSource.CustomUploadCallback() {
            @Override
            public void onStart(String requestId) {
                // Запуск завантаження
            }
            @Override
            public void onProgress(String requestId, long bytes, long totalBytes) {
                // Процес завантаження
            }
            @Override
            public void onSuccess(String imageUrl) {
                firebaseDataSource.updateUserProfileImage(userId, imageUrl)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("Firebase", "URL зображення профілю успішно оновлено!");
                                imageUrlLiveData.setValue(imageUrl);
                            } else {
                                Log.e("Firebase", "Помилка при оновленні URL зображення: " + task.getException().getMessage());
                                imageUrlLiveData.setValue(null);
                            }
                        });
            }
            @Override
            public void onError(String error) {
                Log.e("Cloudinary", "Помилка при завантаженні зображення: " + error);
                imageUrlLiveData.setValue(null);
            }
        });
        return imageUrlLiveData;
    }

    public void updateUsernameOnly(String userId, String username, OnOperationCallback callback) {
        firebaseDataSource.getDatabaseReference()
                .child("Users")
                .child(userId)
                .child("username")
                .setValue(username)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onError(task.getException().getMessage());
                    }
                });
    }

    // Оновлення адреси доставки користувача
    public void updateShippingAddress(String userId, String address, OnOperationCallback callback) {
        firebaseDataSource.updateShippingAddress(userId, address)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Firebase", "Адреса доставки успішно оновлена!");
                        callback.onSuccess();
                    } else {
                        Log.e("Firebase", "Помилка при оновленні адреси доставки: " + task.getException().getMessage());
                        callback.onError(task.getException().getMessage());
                    }
                });
    }

    // Add this method to your UserRepository class
    public void saveLanguagePreference(String languageCode, OnOperationCallback callback) {
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("language", languageCode);
            editor.apply();
            callback.onSuccess();
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }

    // Add this to retrieve the current language
    public String getCurrentLanguage() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("language", "en"); // Default to English
    }

    private void postToMainThread(Runnable runnable) {
        new android.os.Handler(android.os.Looper.getMainLooper()).post(runnable);
    }
}