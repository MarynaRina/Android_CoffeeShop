package com.example.coffee_shop;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;

import com.example.coffee_shop.utils.AnimationUtils;

import android.widget.Toast;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.coffee_shop.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tv_title), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        binding.signupLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        binding.btnLogin.setOnClickListener(v -> {
            AnimationUtils.pressReleasingAnimation(v);

            if (binding.emailEdit.getText().toString().isEmpty() || binding.passwordEdit.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            } else {

                FirebaseAuth.getInstance().signInWithEmailAndPassword(binding.emailEdit.getText().toString(), binding.passwordEdit.getText().toString())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }
}
