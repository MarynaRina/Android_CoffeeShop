package com.example.coffee_shop.presentation.common.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class SharedCoffeeViewModelFactory implements ViewModelProvider.Factory {
    private final Application application;

    public SharedCoffeeViewModelFactory(Application application) {
        this.application = application;
    }



    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SharedCoffeeViewModel.class)) {
            return (T) new SharedCoffeeViewModel(application);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}