package com.example.coffee_shop.presentation.common.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop.data.models.Coffee;
import com.example.coffee_shop.data.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SharedCoffeeViewModel extends AndroidViewModel {
    private final UserRepository userRepository;
    private final String userId;
    private final Map<String, MutableLiveData<Boolean>> favoriteLiveDataMap = new HashMap<>();
    private final MediatorLiveData<Boolean> dummyLiveData = new MediatorLiveData<>();
    private final MutableLiveData<List<Coffee>> favoriteCoffees = new MutableLiveData<>(new ArrayList<>());

    public SharedCoffeeViewModel(@NonNull Application application) {
        super(application);
        this.userRepository = new UserRepository(application.getApplicationContext());
        this.userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        loadInitialFavorites();
    }

    private void loadInitialFavorites() {
        userRepository.getFavorites(userId).observeForever(favorites -> {
            if (favorites != null) {
                List<String> favoriteIds = new ArrayList<>();
                for (Coffee c : favorites) {
                    if (c.getId() != null) favoriteIds.add(c.getId());
                    getOrCreateFavoriteLiveData(c.getId()).setValue(true); // Позначаємо як улюблене
                }
                favoriteCoffees.setValue(favorites);

                for (Map.Entry<String, MutableLiveData<Boolean>> entry : favoriteLiveDataMap.entrySet()) {
                    if (!favoriteIds.contains(entry.getKey())) {
                        entry.getValue().setValue(false);
                    }
                }
            } else {
                favoriteCoffees.setValue(new ArrayList<>());
                for (MutableLiveData<Boolean> liveData : favoriteLiveDataMap.values()) {
                    liveData.setValue(false);
                }
            }
        });
    }

    public LiveData<Boolean> isFavorite(Coffee coffee) {
        if (coffee == null || coffee.getId() == null) return dummyLiveData;
        return getOrCreateFavoriteLiveData(coffee.getId());
    }

    public void toggleFavorite(Coffee coffee) {
        if (coffee == null || coffee.getId() == null) return;

        Boolean isFav = getOrCreateFavoriteLiveData(coffee.getId()).getValue();
        if (Boolean.TRUE.equals(isFav)) {
            removeCoffeeFromFavorites(coffee);
        } else {
            addCoffeeToFavorites(coffee);
        }
    }

    public LiveData<List<Coffee>> getAllFavoriteCoffees() {
        return favoriteCoffees;
    }
    private MutableLiveData<Boolean> getOrCreateFavoriteLiveData(String coffeeId) {
        if (!favoriteLiveDataMap.containsKey(coffeeId)) {
            favoriteLiveDataMap.put(coffeeId, new MutableLiveData<>(false));
        }
        return favoriteLiveDataMap.get(coffeeId);
    }

    public void addCoffeeToFavorites(Coffee coffee) {
        if (coffee == null || coffee.getId() == null) return;

        userRepository.addCoffeeToFavorites(userId, coffee, new UserRepository.OnOp() {
            @Override
            public void onSuccess() {
                getOrCreateFavoriteLiveData(coffee.getId()).setValue(true);
            }

            @Override
            public void onError(String error) {
                Log.e("SharedVM", "Помилка при додаванні в улюблене: " + error);
                getOrCreateFavoriteLiveData(coffee.getId()).setValue(false);
            }
        });
    }

    public void removeCoffeeFromFavorites(Coffee coffee) {
        if (coffee == null || coffee.getId() == null) return;

        userRepository.removeCoffeeFromFavorites(userId, coffee.getId(), new UserRepository.OnOp() {
            @Override
            public void onSuccess() {
                getOrCreateFavoriteLiveData(coffee.getId()).setValue(false);
            }

            @Override
            public void onError(String error) {
                Log.e("SharedVM", "Помилка при видаленні з улюбленого: " + error);
                getOrCreateFavoriteLiveData(coffee.getId()).setValue(true);
            }
        });
    }
}