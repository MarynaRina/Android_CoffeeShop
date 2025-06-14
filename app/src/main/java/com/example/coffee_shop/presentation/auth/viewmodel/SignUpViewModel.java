package com.example.coffee_shop.presentation.auth.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop.data.models.UserProfile;
import com.example.coffee_shop.data.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpViewModel extends AndroidViewModel {
    private final FirebaseAuth firebaseAuth;
    private final UserRepository userRepository;
    private final MutableLiveData<Boolean> signUpSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> signUpError = new MutableLiveData<>();

    public SignUpViewModel(@NonNull Application application) {
        super(application);
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.userRepository = new UserRepository(application);
    }

    public LiveData<Boolean> getSignUpSuccess() {
        return signUpSuccess;
    }

    public LiveData<String> getSignUpError() {
        return signUpError;
    }

    public void signUp(String email, String password, String username) {
        if (email == null || email.trim().isEmpty()) {
            signUpError.setValue("Email cannot be empty");
            return;
        }
        if (password == null || password.trim().isEmpty()) {
            signUpError.setValue("Password cannot be empty");
            return;
        }
        if (username == null || username.trim().isEmpty()) {
            signUpError.setValue("Username cannot be empty");
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            // Створюємо профіль користувача
                            UserProfile userProfile = new UserProfile();
                            userProfile.setUsername(username);
                            userProfile.setEmail(email);

                            // Зберігаємо профіль у Firebase Realtime Database
                            String userId = firebaseUser.getUid();
                            FirebaseDatabase.getInstance().getReference()
                                    .child("Users")
                                    .child(userId)
                                    .setValue(userProfile)
                                    .addOnCompleteListener(saveTask -> {
                                        if (saveTask.isSuccessful()) {
                                            signUpSuccess.setValue(true);
                                        } else {
                                            signUpError.setValue("Failed to save user profile: " + saveTask.getException().getMessage());
                                        }
                                    });
                        } else {
                            signUpError.setValue("User not found after sign-up");
                        }
                    } else {
                        signUpError.setValue("Sign-up failed: " + task.getException().getMessage());
                    }
                });
    }
}