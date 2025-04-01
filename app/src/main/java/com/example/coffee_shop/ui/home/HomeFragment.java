package com.example.coffee_shop.ui.home;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.coffee_shop.ui.search.SearchActivity;
import com.example.coffee_shop.adapters.CoffeeAdapter;
import com.example.coffee_shop.databinding.FragmentHomeBinding;
import com.example.coffee_shop.core.utils.GridSpacingItemDecoration;
import com.example.coffee_shop.ui.shared.SharedCoffeeViewModel;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private CoffeeAdapter coffeeAdapter;
    private SharedCoffeeViewModel sharedViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedCoffeeViewModel.class);

        coffeeAdapter = new CoffeeAdapter(
                new ArrayList<>(),
                getContext(),
                getChildFragmentManager(),
                sharedViewModel
        );
        setupRecyclerView();
        setupObservers();
        setupUI();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        coffeeAdapter = new CoffeeAdapter(new ArrayList<>(), getContext(), getChildFragmentManager(), sharedViewModel);
        binding.recyclerView.setAdapter(coffeeAdapter);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        int spacingInPixels = GridSpacingItemDecoration.dpToPx(16);
        binding.recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, spacingInPixels, true));
    }

    private void setupObservers() {
        homeViewModel.getCoffeeList().observe(getViewLifecycleOwner(), coffees -> {
            if (coffees != null) {
                coffeeAdapter.updateData(coffees);
            }
        });

        homeViewModel.getUserProfileImage().observe(getViewLifecycleOwner(), photoUrl -> {
            if (photoUrl != null && !photoUrl.isEmpty()) {
                Glide.with(requireContext()).load(photoUrl).into(binding.profileImage);
            }
        });

        homeViewModel.getUserLocation().observe(getViewLifecycleOwner(), location -> {
            if (location != null) {
                binding.currentAddress.setText(location);
            }
        });

    }

    private void setupUI() {
        binding.searchIcon.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
        });

        setupCategorySelection();
        binding.coffeeCategory.post(() -> moveUnderline(binding.categoryHot));
        homeViewModel.loadCoffeeData("hot"); // Початкове завантаження категорії "hot"

        homeViewModel.loadUserLocation(requireActivity());
    }

    private void setupCategorySelection() {
        binding.categoryHot.setOnClickListener(view -> {
            moveUnderline(view);
            homeViewModel.loadCoffeeData("hot");
        });

        binding.categoryCold.setOnClickListener(view -> {
            moveUnderline(view);
            homeViewModel.loadCoffeeData("cold");
        });

        binding.categoryOther.setOnClickListener(view -> {
            moveUnderline(view);
            homeViewModel.loadCoffeeData("other");
        });
    }



    private void moveUnderline(View selectedCategory) {
        View underline = binding.categoryUnderline;
        int targetX = selectedCategory.getLeft() + (selectedCategory.getWidth() / 2) - (underline.getWidth() / 2);
        ObjectAnimator animator = ObjectAnimator.ofFloat(underline, "translationX", underline.getTranslationX(), targetX);
        animator.setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }
}