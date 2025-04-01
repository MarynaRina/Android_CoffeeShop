package com.example.coffee_shop.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.coffee_shop.databinding.ActivityWelcomeBinding;
import com.example.coffee_shop.core.utils.AnimationUtils;

public class WelcomeActivity extends AppCompatActivity {
    private ActivityWelcomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);

        binding.btnStart.setOnClickListener(v -> {
            AnimationUtils.pressReleasingAnimation(v);

            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}