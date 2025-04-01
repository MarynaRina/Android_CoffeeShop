package com.example.coffee_shop.data.repository;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.coffee_shop.core.managers.CloudinaryManager;
import com.example.coffee_shop.data.models.Coffee;
import com.example.coffee_shop.data.models.UserProfile;
import com.example.coffee_shop.ui.profile.ProfileViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRepository {
    private final FirebaseDatabase firebaseDatabase;
    private final CloudinaryManager cloudinaryManager;
    private final Context context;

    public UserRepository(Context context) {
        this.context = context.getApplicationContext(); // Зберігаємо контекст
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        this.cloudinaryManager = CloudinaryManager.getInstance(context); // Ініціалізація CloudinaryManager з контекстом
    }

    // Отримання профілю користувача
    public LiveData<UserProfile> getUserProfile(String userId) {
        MutableLiveData<UserProfile> userProfileLiveData = new MutableLiveData<>();

        firebaseDatabase.getReference().child("Users").child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            UserProfile userProfile = snapshot.getValue(UserProfile.class);
                            userProfileLiveData.setValue(userProfile);
                        } else {
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

    // Додавання кави до улюблених
    public void addCoffeeToFavorites(String userId, Coffee coffee) {
        DatabaseReference favoritesRef = firebaseDatabase.getReference().child("Users").child(userId).child("favorites");

        // Перевіряємо, чи кава вже є в улюблених
        favoritesRef.orderByChild("id").equalTo(coffee.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Якщо кава вже є, не додаємо її
                    Log.d("Firebase", "Кава вже є у списку улюблених.");
                } else {
                    // Якщо кави немає, додаємо її
                    favoritesRef.child(coffee.getId()).setValue(coffee)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d("Firebase", "Кава успішно додана в улюблені!");
                                } else {
                                    Log.e("Firebase", "Помилка при додаванні кави в улюблені: " + task.getException().getMessage());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Помилка при перевірці улюблених: " + error.getMessage());
            }
        });
    }

    // Отримання списку улюблених кав
    public LiveData<List<Coffee>> getFavorites(String userId) {
        MutableLiveData<List<Coffee>> favoritesLiveData = new MutableLiveData<>();

        firebaseDatabase.getReference().child("Users").child(userId).child("favorites")
                .addValueEventListener(new ValueEventListener() {
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
                            // Відповідно до вимог, потрібно реалізувати цей метод
                            favoritesLiveData.setValue(null);
                            // Можна також додати обробку помилки
                            // Log.e("UserRepository", "Error fetching data: " + error.getMessage());
                    }

                });

        return favoritesLiveData;
    }
    private void postToMainThread(Runnable runnable) {
        new android.os.Handler(android.os.Looper.getMainLooper()).post(runnable);
    }

    public void updateUserProfile(String userId, String username, String password, ProfileViewModel.OnProfileUpdateCallback callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("username", username);

        // Оновлюємо дані в базі даних
        firebaseDatabase.getReference().child("Users").child(userId)
                .updateChildren(updates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        postToMainThread(callback::onSuccess);
                    } else {
                        postToMainThread(() -> callback.onError("Failed to update profile: " + task.getException().getMessage()));
                    }
                });

        // Логіка для оновлення паролю (якщо він наданий)
        if (password != null && !password.isEmpty()) {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser != null) {
                firebaseUser.updatePassword(password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                postToMainThread(callback::onSuccess);
                            } else {
                                postToMainThread(() -> callback.onError("Failed to update password: " + task.getException().getMessage()));
                            }
                        });
            } else {
                postToMainThread(() -> callback.onError("User not authenticated"));
            }
        }
    }

    // Завантаження зображення профілю
    public void uploadProfileImage(String currentUserId, Uri imageUri, ProfileViewModel.OnImageUploadCallback callback) {
        // Перетворення Uri в byte[]
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }
            byte[] imageData = byteArrayOutputStream.toByteArray();
            inputStream.close();

            // Параметри для завантаження на Cloudinary
            Map<String, Object> options = new HashMap<>();
            options.put("folder", "user_avatars");
            options.put("public_id", "user_" + System.currentTimeMillis());
            options.put("resource_type", "auto");

            // Завантаження зображення
            cloudinaryManager.upload(imageData, options, new UploadCallback() {
                @Override
                public void onStart(String requestId) {
                    // Запуск завантаження
                }

                @Override
                public void onProgress(String requestId, long bytes, long totalBytes) {
                    // Процес завантаження
                }

                @Override
                public void onSuccess(String requestId, Map resultData) {
                    String imageUrl = (String) resultData.get("secure_url"); // Отримуємо URL зображення з Cloudinary
                    saveImageUrlToDatabase(currentUserId, imageUrl, callback); // Зберігаємо URL в базі даних
                }

                @Override
                public void onError(String requestId, ErrorInfo error) {
                    callback.onError("Cloudinary upload error: " + error.getDescription()); // Обробка помилки
                }

                @Override
                public void onReschedule(String requestId, ErrorInfo error) {
                    // Якщо завантаження перенесено
                }
            });
        } catch (IOException e) {
            callback.onError("Error converting image to byte array: " + e.getMessage());
        }
    }

    // Збереження URL зображення профілю в Firebase
    private void saveImageUrlToDatabase(String userId, String imageUrl, ProfileViewModel.OnImageUploadCallback callback) {
        firebaseDatabase.getReference().child("Users").child(userId)
                .child("profileImage").setValue(imageUrl)
                .addOnSuccessListener(aVoid -> callback.onSuccess(imageUrl))
                .addOnFailureListener(e -> callback.onError("Failed to save image URL: " + e.getMessage()));
    }


    // Видалення кави з улюбленого
    public void removeCoffeeFromFavorites(String userId, String coffeeId) {
        firebaseDatabase.getReference().child("Users").child(userId).child("favorites")
                .orderByChild("id").equalTo(coffeeId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot coffeeSnapshot : snapshot.getChildren()) {
                            coffeeSnapshot.getRef().removeValue(); // Видалити каву
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Логіка обробки помилок
                    }
                });
    }
    public void updateShippingAddress(String userId, String fullName, String address, String city, String zipCode, ProfileViewModel.OnProfileUpdateCallback callback) {
        // Створюємо мапу для збереження даних адреси
        Map<String, String> addressMap = new HashMap<>();
        addressMap.put("fullName", fullName);
        addressMap.put("address", address);
        addressMap.put("city", city);
        addressMap.put("zipCode", zipCode);

        // Оновлюємо дані в Firebase
        firebaseDatabase.getReference().child("Users").child(userId)
                .child("shippingAddress").setValue(addressMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        postToMainThread(callback::onSuccess); // Викликаємо onSuccess на головному потоці
                    } else {
                        postToMainThread(() -> callback.onError("Failed to update shipping address: " + task.getException().getMessage()));
                    }
                });
    }
}