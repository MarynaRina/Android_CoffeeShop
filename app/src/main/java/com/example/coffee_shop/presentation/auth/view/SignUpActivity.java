package com.example.coffee_shop.presentation.auth.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.coffee_shop.databinding.ActivitySignUpBinding;
import com.example.coffee_shop.presentation.auth.viewmodel.SignUpViewModel;
import com.example.coffee_shop.presentation.main.view.MainActivity;

public class SignUpActivity extends AppCompatActivity {
    private ActivitySignUpBinding binding;
    private SignUpViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);

        setupListeners();
        observeViewModel();
    }

    private void setupListeners() {
        binding.btnSignup.setOnClickListener(v -> {
            String email = binding.emailEdit.getText().toString().trim();
            String password = binding.passwordEdit.getText().toString().trim();
            String username = binding.usernameEdit.getText().toString().trim();
            viewModel.signUp(email, password, username);
        });

        binding.loginLink.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });
    }

    private void observeViewModel() {
        viewModel.getSignUpSuccess().observe(this, success -> {
            if (success) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });

        viewModel.getSignUpError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}