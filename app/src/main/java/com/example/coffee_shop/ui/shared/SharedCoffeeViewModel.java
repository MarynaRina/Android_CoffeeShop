package com.example.coffee_shop.ui.shared;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop.data.models.Coffee;
import com.example.coffee_shop.data.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SharedCoffeeViewModel extends AndroidViewModel {
    private final UserRepository userRepository;
    private final Map<String, MutableLiveData<Boolean>> favoriteLiveDataMap = new HashMap<>();

    public SharedCoffeeViewModel(@NonNull Application application) {
        super(application);
        this.userRepository = new UserRepository(application);
        initializeFavorites();
    }

    private void initializeFavorites() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRepository.getFavorites(userId).observeForever(favorites -> {
            if (favorites != null) {
                for (Coffee coffee : favorites) {
                    getOrCreateFavoriteLiveData(coffee.getId()).setValue(true);
                }
            }
        });
    }

    // Оптимізований метод для переключення улюбленого
    public void toggleFavorite(Coffee coffee) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        MutableLiveData<Boolean> liveData = getOrCreateFavoriteLiveData(coffee.getId());

        Boolean isCurrentlyFavorite = liveData.getValue();
        if (isCurrentlyFavorite == null) {
            isCurrentlyFavorite = false;
        }

        if (isCurrentlyFavorite) {
            userRepository.removeCoffeeFromFavorites(userId, coffee.getId());
            liveData.setValue(false);
        } else {
            userRepository.addCoffeeToFavorites(userId, coffee);
            liveData.setValue(true);
        }
    }

    private boolean isFavorite(Coffee coffee, List<Coffee> favorites) {
        for (Coffee fav : favorites) {
            if (fav.getId().equals(coffee.getId())) {
                return true;
            }
        }
        return false;
    }

    public LiveData<Boolean> isFavorite(Coffee coffee) {
        return getOrCreateFavoriteLiveData(coffee.getId());
    }

    private MutableLiveData<Boolean> getOrCreateFavoriteLiveData(String coffeeId) {
        if (!favoriteLiveDataMap.containsKey(coffeeId)) {
            MutableLiveData<Boolean> liveData = new MutableLiveData<>(false);
            favoriteLiveDataMap.put(coffeeId, liveData);
        }
        return favoriteLiveDataMap.get(coffeeId);
    }

    public void addCoffeeToFavorites(Coffee coffee) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRepository.addCoffeeToFavorites(userId, coffee);
        getOrCreateFavoriteLiveData(coffee.getId()).setValue(true);
    }

    public void removeCoffeeFromFavorites(Coffee coffee) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRepository.removeCoffeeFromFavorites(userId, coffee.getId());
        getOrCreateFavoriteLiveData(coffee.getId()).setValue(false);
    }
}