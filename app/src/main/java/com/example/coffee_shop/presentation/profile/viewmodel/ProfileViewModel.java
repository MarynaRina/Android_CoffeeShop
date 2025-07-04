package com.example.coffee_shop.presentation.profile.viewmodel;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.coffee_shop.data.models.UserProfile;
import com.example.coffee_shop.data.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.atomic.AtomicBoolean;

public class ProfileViewModel extends AndroidViewModel {

    private final UserRepository userRepository;
    private final MutableLiveData<UserProfile> userProfile = new MutableLiveData<>();
    private final SingleLiveEvent<String> toastMessage = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> languageSaved = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> imageUpdated = new SingleLiveEvent<>();
    private final String currentUserId;
    private Observer<UserProfile> repoObserver;

    public ProfileViewModel(@NonNull Application app) {
        super(app);
        FirebaseUser cu = FirebaseAuth.getInstance().getCurrentUser();
        if (cu == null) {
            toastMessage.setValue("User not signed in");
            currentUserId = null;
            userRepository = null;
            return;
        }
        currentUserId = cu.getUid();
        userRepository = new UserRepository(app.getApplicationContext());
        repoObserver = profile -> userProfile.postValue(profile);
        userRepository.getUserProfile(currentUserId).observeForever(repoObserver);
    }

    public LiveData<UserProfile> getUserProfile()        { return userProfile; }
    public LiveData<String>      getToastMessage()       { return toastMessage; }
    public LiveData<String>      getLanguageSaved()      { return languageSaved; }
    public LiveData<String>      getImageUploadResult()  { return imageUpdated; }

    public void updateAppLanguage(@NonNull String code) {
        if (userRepository == null) return;
        userRepository.saveLanguagePreference(code, new UserRepository.OnOp() {
            @Override public void onSuccess() { languageSaved.setValue(code); }
            @Override public void onError(String err) { toastMessage.setValue(err); }
        });
    }

    public void updateUserProfile(String username) {
        if (username.trim().isEmpty()) { toastMessage.setValue("Ім’я не може бути порожнім"); return; }
        UserProfile cur = userProfile.getValue();
        if (cur == null) return;
        UserProfile optimistic = new UserProfile(cur);
        optimistic.setUsername(username);
        userProfile.setValue(optimistic);
        userRepository.updateUsernameOnly(currentUserId, username, new UserRepository.OnOp() {
            @Override public void onSuccess() { }
            @Override public void onError(String err) {
                userProfile.postValue(cur);
                toastMessage.postValue(err);
            }
        });
    }

    public void updateProfileImage(@NonNull Uri imageUri) {
        if (userRepository == null) return;
        LiveData<String> task = userRepository.uploadProfileImage(currentUserId, imageUri);
        Observer<String> once = new Observer<String>() {
            @Override public void onChanged(String url) {
                task.removeObserver(this);
                if (url == null) { toastMessage.setValue("Не вдалося завантажити зображення"); return; }
                UserProfile p = userProfile.getValue();
                if (p != null) { p.setProfileImage(url); userProfile.postValue(p); }
                imageUpdated.setValue(url);
            }
        };
        task.observeForever(once);
    }

    public void updatePassword(@NonNull String newPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) { toastMessage.setValue("User not signed in"); return; }
        user.updatePassword(newPassword).addOnCompleteListener(task ->
                toastMessage.setValue(task.isSuccessful()
                        ? "Password updated successfully"
                        : "Помилка: " + (task.getException() != null ? task.getException().getMessage() : "Unknown")));
    }

    public void updateShippingAddress(String fullName, String address, String city, String zip, String phone) {
        if (userRepository == null) return;
        UserProfile p = userProfile.getValue();
        if (p == null) { toastMessage.setValue("Профіль ще не завантажено"); return; }

        userRepository.updateShippingAddress(currentUserId, fullName, address, city, zip, phone, new UserRepository.OnOp() {
            @Override public void onSuccess() { userProfile.postValue(p); }
            @Override public void onError(String err) { toastMessage.setValue(err); }
        });
    }

    @Override protected void onCleared() {
        if (userRepository != null && repoObserver != null) {
            userRepository.getUserProfile(currentUserId).removeObserver(repoObserver);
        }
    }

    public static class SingleLiveEvent<T> extends MutableLiveData<T> {
        private final AtomicBoolean pending = new AtomicBoolean(false);
        @MainThread @Override
        public void observe(@NonNull androidx.lifecycle.LifecycleOwner owner,
                            @NonNull androidx.lifecycle.Observer<? super T> obs) {
            super.observe(owner, t -> {
                if (pending.compareAndSet(true, false)) obs.onChanged(t);
            });
        }
        @MainThread @Override
        public void setValue(T t) {
            pending.set(true);
            super.setValue(t);
        }
    }
}