package com.example.coffee_shop.presentation.profile.viewmodel;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop.data.models.UserProfile;
import com.example.coffee_shop.data.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileViewModel extends AndroidViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<UserProfile> userProfile = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> imageUploadResult = new MutableLiveData<>();
    private final String currentUserId;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        this.userRepository = new UserRepository(application.getApplicationContext());
        this.currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void loadUserInfo(LifecycleOwner lifecycleOwner) {
        userRepository.getUserProfile(currentUserId)
                .observe(lifecycleOwner, profile -> {
                    Log.d("ProfileVM", "Профіль завантажено: " + profile);
                    userProfile.setValue(profile);
                });
    }

    public void updateAppLanguage(String languageCode) {
        // Remember the selected language in SharedPreferences
        userRepository.saveLanguagePreference(languageCode, new UserRepository.OnOperationCallback() {
            @Override
            public void onSuccess() {
                Log.d("ProfileVM", "Language preference saved: " + languageCode);
                // Notify UI that language was updated successfully
                MutableLiveData<String> languageUpdateResult = new MutableLiveData<>();
                languageUpdateResult.setValue(languageCode);
            }

            @Override
            public void onError(String error) {
                errorMessage.setValue(error);
            }
        });
    }

    public void updateUserProfile(String username) {
        if (username == null || username.trim().isEmpty()) {
            errorMessage.setValue("Ім’я не може бути порожнім");
            return;
        }

        userRepository.updateUsernameOnly(currentUserId, username, new UserRepository.OnOperationCallback() {
            @Override
            public void onSuccess() {
                Log.d("ProfileVM", "Ім’я оновлено!");
                // Локально оновлюємо LiveData
                UserProfile profile = userProfile.getValue();
                if (profile != null) {
                    profile.setUsername(username);
                    userProfile.setValue(profile);
                }
            }

            @Override
            public void onError(String error) {
                Log.e("ProfileVM", "Помилка при оновленні імені: " + error);
                errorMessage.setValue(error);
            }
        });
    }
    public void updateProfileImage(Uri imageUri, LifecycleOwner lifecycleOwner) {
        userRepository.uploadProfileImage(currentUserId, imageUri)
                .observe(lifecycleOwner, imageUrl -> {
                    if (imageUrl != null) {
                        UserProfile currentProfile = userProfile.getValue();
                        if (currentProfile != null) {
                            currentProfile.setProfileImage(imageUrl);

                            userRepository.updateUserProfile(currentUserId, currentProfile, new UserRepository.OnOperationCallback() {
                                @Override
                                public void onSuccess() {
                                    Log.d("ProfileVM", "Зображення оновлено!");
                                    userProfile.setValue(currentProfile);
                                    imageUploadResult.setValue(imageUrl);
                                }

                                @Override
                                public void onError(String error) {
                                    errorMessage.setValue(error);
                                }
                            });
                        }
                    } else {
                        errorMessage.setValue("Не вдалося завантажити зображення");
                    }
                });
    }

    public void updatePassword(String newPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.updatePassword(newPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("ProfileVM", "Пароль оновлено успішно!");
                        } else {
                            errorMessage.setValue("Помилка оновлення пароля: " + task.getException().getMessage());
                        }
                    });
        }
    }

    public void updateShippingAddress(String fullName, String address, String city, String zipCode) {
        String fullAddress = fullName + ", " + address + ", " + city + ", " + zipCode;
        UserProfile currentProfile = userProfile.getValue();

        if (currentProfile == null) {
            errorMessage.setValue("Профіль ще не завантажено");
            return;
        }

        currentProfile.setShippingAddress(fullAddress);

        userRepository.updateShippingAddress(currentUserId, fullAddress, new UserRepository.OnOperationCallback() {
            @Override
            public void onSuccess() {
                Log.d("ProfileVM", "Адреса доставки оновлена");
                userProfile.setValue(currentProfile);
            }

            @Override
            public void onError(String error) {
                errorMessage.setValue(error);
            }
        });
    }

    public LiveData<UserProfile> getUserProfile() {
        return userProfile;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<String> getImageUploadResult() {
        return imageUploadResult;
    }
}