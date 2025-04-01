package com.example.coffee_shop.ui.profile;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop.data.models.UserProfile;
import com.example.coffee_shop.data.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileViewModel extends AndroidViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<UserProfile> userProfile = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final String currentUserId;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        this.userRepository = new UserRepository(application.getApplicationContext()); // Отримуємо Context
        this.currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        loadUserInfo();
    }

    public void loadUserInfo() {
        LiveData<UserProfile> profileLiveData = userRepository.getUserProfile(currentUserId);
        profileLiveData.observeForever(userProfile::setValue);
    }

    public void updateUserProfile(String username, String password) {
        userRepository.updateUserProfile(currentUserId, username, password, new OnProfileUpdateCallback() {
            @Override
            public void onSuccess() {
                loadUserInfo();
            }

            @Override
            public void onError(String errorMessage) {
                ProfileViewModel.this.errorMessage.setValue(errorMessage);
            }
        });
    }

    public void updateProfileImage(Uri imageUri, OnImageUploadCallback callback) {
        userRepository.uploadProfileImage(currentUserId, imageUri, callback);
    }

    public void updateShippingAddress(String fullName, String address, String city, String zipCode) {
        userRepository.updateShippingAddress(currentUserId, fullName, address, city, zipCode, new OnProfileUpdateCallback() {
            @Override
            public void onSuccess() {
                loadUserInfo();
            }

            @Override
            public void onError(String errorMessage) {
                ProfileViewModel.this.errorMessage.setValue(errorMessage);
            }
        });
    }

    public LiveData<UserProfile> getUserProfile() {
        return userProfile;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public interface OnImageUploadCallback {
        void onSuccess(String imageUrl);
        void onError(String errorMessage);
    }

    public interface OnProfileUpdateCallback {
        void onSuccess();
        void onError(String errorMessage);
    }
}