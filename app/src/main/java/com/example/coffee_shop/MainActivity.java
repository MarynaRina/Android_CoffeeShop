package com.example.coffee_shop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.coffee_shop.bottomnav.cart.CartFragment;
import com.example.coffee_shop.bottomnav.favorite.FavoriteFragment;
import com.example.coffee_shop.bottomnav.home.HomeFragment;
import com.example.coffee_shop.bottomnav.profile.ProfileFragment;
import com.example.coffee_shop.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
        } else {

            binding.bottomNav.setSelectedItemId(R.id.profile);

            Map<Integer, Fragment> fragmentMap = new HashMap<>();
            fragmentMap.put(R.id.home, new HomeFragment());
            fragmentMap.put(R.id.profile, new ProfileFragment());
            fragmentMap.put(R.id.cart, new CartFragment());
            fragmentMap.put(R.id.favorite, new FavoriteFragment());

            binding.bottomNav.setOnItemSelectedListener(item -> {
                Fragment fragment = fragmentMap.get(item.getItemId());
                if (fragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(binding.fragmentContainer.getId(), fragment)
                            .commit();

                }
                return true;
            });
        }

    }
}