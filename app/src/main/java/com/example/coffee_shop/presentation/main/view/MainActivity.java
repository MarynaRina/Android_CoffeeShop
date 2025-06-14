package com.example.coffee_shop.presentation.main.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.coffee_shop.R;
import com.example.coffee_shop.core.utils.AnimationUtils;
import com.example.coffee_shop.databinding.ActivityMainBinding;
import com.example.coffee_shop.presentation.auth.view.WelcomeActivity;
import com.example.coffee_shop.presentation.main.viewmodel.MainViewModel;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        setupBottomNavigation();
        applyBottomNavBackground();
        observeViewModel();

        if (savedInstanceState == null) {
            binding.bottomNav.setSelectedItemId(R.id.home);
        }
    }

    private void setupBottomNavigation() {
        binding.bottomNav.setOnItemSelectedListener(item -> {
            viewModel.selectFragment(item.getItemId());
            View iconView = binding.bottomNav.findViewById(item.getItemId());
            if (iconView != null) {
                AnimationUtils.highlightsAnimation(iconView);
            }
            return true;
        });
    }

    private void applyBottomNavBackground() {
        MaterialShapeDrawable shapeDrawable = new MaterialShapeDrawable();
        shapeDrawable.setShapeAppearanceModel(
                shapeDrawable.getShapeAppearanceModel()
                        .toBuilder()
                        .setTopLeftCorner(CornerFamily.ROUNDED, 40f)
                        .setTopRightCorner(CornerFamily.ROUNDED, 40f)
                        .build()
        );
        shapeDrawable.setFillColor(ContextCompat.getColorStateList(this, R.color.bg));
        binding.bottomNav.setBackground(shapeDrawable);
    }

    private void applyBottomNavStyle(boolean isHomeFragment) {
        if (isHomeFragment) {
            binding.bottomNav.setBackgroundColor(ContextCompat.getColor(this, R.color.bg));
            binding.bottomNav.setItemIconTintList(ContextCompat.getColorStateList(this, R.color.bottom_nav_home));
        } else {
            binding.bottomNav.setBackgroundColor(ContextCompat.getColor(this, R.color.bg_light));
            binding.bottomNav.setItemIconTintList(ContextCompat.getColorStateList(this, R.color.bottom_nav_others));
        }
    }

    private void observeViewModel() {
        // Перевірка авторизації
        viewModel.getIsUserAuthenticated().observe(this, isAuthenticated -> {
            if (!isAuthenticated) {
                startActivity(new Intent(this, WelcomeActivity.class));
                finish();
            }
        });

        // Оновлення фрагмента
        viewModel.getSelectedFragmentState().observe(this, fragmentState -> {
            if (fragmentState != null && fragmentState.fragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(binding.fragmentContainer.getId(), fragmentState.fragment)
                        .commit();
            }
        });

        // Оновлення стилю навігації
        viewModel.getNavStyle().observe(this, navStyle -> {
            if (navStyle != null) {
                applyBottomNavStyle(navStyle.isHomeFragment);
            }
        });
    }
}