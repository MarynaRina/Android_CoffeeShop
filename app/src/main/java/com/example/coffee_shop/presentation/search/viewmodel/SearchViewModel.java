package com.example.coffee_shop.presentation.search.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.coffee_shop.data.models.Coffee;
import com.example.coffee_shop.data.repository.CoffeeRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchViewModel extends ViewModel {
    private final CoffeeRepository repository;
    private final MutableLiveData<List<Coffee>> allCoffees = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Coffee>> filteredCoffees = new MutableLiveData<>(new ArrayList<>());

    public SearchViewModel() {
        repository = new CoffeeRepository();
    }

    public LiveData<List<Coffee>> getFilteredCoffees() {
        return filteredCoffees;
    }

    public void loadCoffees() {
        repository.getAllCoffees(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Coffee> result = new ArrayList<>();
                for (DataSnapshot coffeeSnap : snapshot.getChildren()) {
                    Coffee coffee = coffeeSnap.getValue(Coffee.class);
                    if (coffee != null) result.add(coffee);
                }
                allCoffees.setValue(result);
                filteredCoffees.setValue(result);
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    public void filter(String query) {
        List<Coffee> source = allCoffees.getValue();
        if (source == null) return;

        if (query == null || query.trim().isEmpty()) {
            filteredCoffees.setValue(source);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            List<Coffee> filtered = new ArrayList<>();
            for (Coffee c : source) {
                if (c.getName().toLowerCase().contains(lowerCaseQuery)) {
                    filtered.add(c);
                }
            }
            filteredCoffees.setValue(filtered);
        }
    }
}