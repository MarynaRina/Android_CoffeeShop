package com.example.coffee_shop;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

import java.util.Locale;

public class MyApp extends Application implements ViewModelStoreOwner {

    private static MyApp instance;
    private final ViewModelStore viewModelStore = new ViewModelStore();

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Set app language from preferences
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String lang = prefs.getString("language", "en");

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Resources res = getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.setLocale(locale);

        // Використовуйте createConfigurationContext замість застарілого updateConfiguration
        createConfigurationContext(config);

        // Але також залиште updateConfiguration для підтримки старіших пристроїв
        res.updateConfiguration(config, res.getDisplayMetrics());
    }

    public static MyApp getInstance() {
        return instance;
    }

    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        return viewModelStore;
    }
}