package com.example.coffee_shop.presentation.home.viewmodel;

import android.app.Activity;
import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop.presentation.common.adapters.CoffeeAdapter;
import com.example.coffee_shop.core.utils.LocationHelper;
import com.example.coffee_shop.data.models.Coffee;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    private final DatabaseReference coffeeRef = FirebaseDatabase.getInstance().getReference("Coffee");
    private final DatabaseReference userRef;
    private final MutableLiveData<List<Coffee>> coffeeList = new MutableLiveData<>();
    private final MutableLiveData<String> userProfileImage = new MutableLiveData<>();
    private final MutableLiveData<String> userLocation = new MutableLiveData<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userRef = (user != null) ? FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()) : null;

        loadUserProfile();
        loadCoffeeData("hot"); // Початкове завантаження категорії "hot"
    }

    public LiveData<List<Coffee>> getCoffeeList() {
        return coffeeList;
    }

    public LiveData<String> getUserProfileImage() {
        return userProfileImage;
    }

    public LiveData<String> getUserLocation() {
        return userLocation;
    }

    public void loadCoffeeData(String category) {
        coffeeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Coffee> filteredList = new ArrayList<>();
                for (DataSnapshot coffeeSnapshot : snapshot.getChildren()) {
                    Coffee coffee = coffeeSnapshot.getValue(Coffee.class);
                    if (coffee != null && category.equalsIgnoreCase(coffee.getCategory())) {
                        filteredList.add(coffee);
                    }
                }
                coffeeList.setValue(filteredList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to load coffee data: " + error.getMessage());
            }
        });
    }

    private void loadUserProfile() {
        if (userRef != null) {
            userRef.child("profileImage").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String photoUrl = snapshot.getValue(String.class);
                        if (photoUrl != null && !photoUrl.isEmpty()) {
                            userProfileImage.setValue(photoUrl);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Firebase", "Failed to load user profile: " + error.getMessage());
                }
            });
        }
    }

    public void loadUserLocation(Activity activity) {
        LocationHelper locationHelper = new LocationHelper(activity, new LocationHelper.LocationListener() {
            @Override
            public void onLocationRetrieved(String city, String country) {
                userLocation.setValue(city + ", " + country);
            }

            @Override
            public void onLocationError(String errorMessage) {
                Log.e("Location", "Failed to get location: " + errorMessage);
            }
        });

        locationHelper.requestLocation();
    }

    private final MutableLiveData<CoffeeAdapter> coffeeAdapter = new MutableLiveData<>();


    public LiveData<CoffeeAdapter> getCoffeeAdapter() {
        return coffeeAdapter;
    }
}