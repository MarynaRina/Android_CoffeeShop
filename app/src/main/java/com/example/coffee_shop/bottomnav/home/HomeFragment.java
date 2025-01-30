package com.example.coffee_shop.bottomnav.home;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.coffee_shop.R;
import com.example.coffee_shop.adapters.CoffeeAdapter;
import com.example.coffee_shop.databinding.FragmentHomeBinding;
import com.example.coffee_shop.models.Coffee;
import com.example.coffee_shop.utils.GridSpacingItemDecoration;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private DatabaseReference coffeeRef;
    private CoffeeAdapter coffeeAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        // Ініціалізація посилання на Firebase Realtime Database
        coffeeRef = FirebaseDatabase.getInstance().getReference("Coffee");

        // Ініціалізація RecyclerView
        coffeeAdapter = new CoffeeAdapter(new ArrayList<>());
        binding.recyclerView.setAdapter(coffeeAdapter);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        int spacingInPixels = GridSpacingItemDecoration.dpToPx(16);
        binding.recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, spacingInPixels, true));

        setupCategorySelection();

        // За замовчуванням показуємо "Hot Coffee" і переміщаємо підкреслення
        binding.coffeeCategory.post(() -> moveUnderline(binding.categoryHot));
        filterByCategory("hot");

        return binding.getRoot();
    }

    private void setupCategorySelection() {
        binding.categoryHot.setOnClickListener(view -> {
            moveUnderline(view);
            filterByCategory("hot");
        });

        binding.categoryCold.setOnClickListener(view -> {
            moveUnderline(view);
            filterByCategory("cold");
        });

        binding.categoryOther.setOnClickListener(view -> {
            moveUnderline(view);
            filterByCategory("other");
        });
    }

    private void moveUnderline(View selectedCategory) {
        View underline = binding.categoryUnderline;

        // Визначаємо координати центру
        int targetX = selectedCategory.getLeft() + (selectedCategory.getWidth() / 2) - (underline.getWidth() / 2);

        // Анімація плавного перепливання
        ObjectAnimator animator = ObjectAnimator.ofFloat(underline, "translationX", underline.getTranslationX(), targetX);
        animator.setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    private void filterByCategory(String category) {
        coffeeRef.addListenerForSingleValueEvent(new ValueEventListener() { // Змінено на SingleValueEvent (щоб не було дублікатів)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Coffee> filteredList = new ArrayList<>();
                for (DataSnapshot coffeeSnapshot : snapshot.getChildren()) {
                    Coffee coffee = coffeeSnapshot.getValue(Coffee.class);
                    if (coffee != null && category.equalsIgnoreCase(coffee.getCategory())) {
                        filteredList.add(coffee);
                    }
                }
                coffeeAdapter.updateData(filteredList);

                if (filteredList.isEmpty()) {
                    Toast.makeText(requireContext(), "No coffee available in this category", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}