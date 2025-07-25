package com.example.coffee_shop;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

public class MyApp extends Application implements ViewModelStoreOwner {
    private static MyApp instance;
    private final ViewModelStore viewModelStore = new ViewModelStore();

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        String lang = getSharedPreferences("AppPrefs", MODE_PRIVATE)
                .getString("language", "en");

        AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags(lang));
    }

    public static MyApp getInstance() {
        return instance;
    }

    @NonNull @Override
    public ViewModelStore getViewModelStore() {
        return viewModelStore;
    }
}