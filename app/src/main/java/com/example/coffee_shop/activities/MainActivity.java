package com.example.coffee_shop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.coffee_shop.R;
import com.example.coffee_shop.ui.cart.CartFragment;
import com.example.coffee_shop.ui.favorite.FavoritesFragment;
import com.example.coffee_shop.ui.home.HomeFragment;
import com.example.coffee_shop.ui.profile.ProfileFragment;
import com.example.coffee_shop.databinding.ActivityMainBinding;
import com.example.coffee_shop.core.utils.AnimationUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
            return;
        }

        applyBottomNavBackground();

        binding.bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = getFragment(item);
            if (selectedFragment != null) {
                View iconView = binding.bottomNav.findViewById(item.getItemId());
                if (iconView != null) {
                    AnimationUtils.highlightsAnimation(iconView);
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(binding.fragmentContainer.getId(), selectedFragment)
                        .commit();
                applyBottomNavStyle(selectedFragment);
            }
            applyBottomNavBackground();
            return true;
        });

        if (savedInstanceState == null) {
            applyBottomNavStyle(new HomeFragment());
            binding.bottomNav.setSelectedItemId(R.id.home);
        }
    }


    @Nullable
    private static Fragment getFragment(MenuItem item) {
        Fragment selectedFragment = null;

        if (item.getItemId() == R.id.home) {
            selectedFragment = new HomeFragment();
        } else if (item.getItemId() == R.id.profile) {
            selectedFragment = new ProfileFragment();
        } else if (item.getItemId() == R.id.cart) {
            selectedFragment = new CartFragment();
        } else if (item.getItemId() == R.id.favorite) {
            selectedFragment = new FavoritesFragment();
        }
        return selectedFragment;
    }
    private void applyBottomNavStyle(Fragment fragment) {
        BottomNavigationView bottomNav = binding.bottomNav;

        if (fragment instanceof HomeFragment) {
            bottomNav.setBackgroundColor(ContextCompat.getColor(this, R.color.bg));
            bottomNav.setItemIconTintList(ContextCompat.getColorStateList(this, R.color.bottom_nav_home));
        } else {
            bottomNav.setBackgroundColor(ContextCompat.getColor(this, R.color.btn_color));
            bottomNav.setItemIconTintList(ContextCompat.getColorStateList(this, R.color.bottom_nav_others));
        }
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
        binding.bottomNav.setBackground(shapeDrawable);
    }
}