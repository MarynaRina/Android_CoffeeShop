package com.example.coffee_shop.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop.data.models.Coffee;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class CoffeeRepository {
    private final DatabaseReference coffeeRef;

    public CoffeeRepository() {
        coffeeRef = FirebaseDatabase.getInstance().getReference("Coffee");
    }
    public void getAllCoffees(ValueEventListener listener) {
        FirebaseDatabase.getInstance().getReference("Coffee")
                .addListenerForSingleValueEvent(listener);
    }
    public LiveData<List<Coffee>> getCoffeesByCategory(String category) {
        MutableLiveData<List<Coffee>> liveData = new MutableLiveData<>();

        coffeeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Coffee> filteredList = new ArrayList<>();
                for (DataSnapshot coffeeSnapshot : snapshot.getChildren()) {
                    Coffee coffee = coffeeSnapshot.getValue(Coffee.class);
                    if (coffee != null && coffee.getCategory().equalsIgnoreCase(category)) {
                        filteredList.add(coffee);
                    }
                }
                liveData.setValue(filteredList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                liveData.setValue(null);
            }
        });

        return liveData;
    }
    public Coffee getCoffeeSync(String coffeeId) {
        try {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Coffee").child(coffeeId);
            DataSnapshot snapshot = Tasks.await(ref.get());

            if (snapshot.exists()) {
                return snapshot.getValue(Coffee.class);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}