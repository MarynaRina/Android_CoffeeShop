package com.example.coffee_shop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.coffee_shop.databinding.ActivitySignUpBinding;
import com.example.coffee_shop.utils.AnimationUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
    private ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
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

        TextView loginLink = findViewById(R.id.login_link);
        loginLink.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        binding.btnSignup.setOnClickListener(v -> {
            AnimationUtils.pressReleasingAnimation(v);

            if (binding.emailEdit.getText().toString().isEmpty() || binding.passwordEdit.getText().toString().isEmpty() || binding.usernameEdit.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(binding.emailEdit.getText().toString(), binding.passwordEdit.getText().toString())
                        .addOnCompleteListener(
                                task -> {
                                    if (task.isSuccessful()) {
                                        HashMap<String, String> userInfo = new HashMap<>();
                                        userInfo.put("password", binding.passwordEdit.getText().toString());
                                        userInfo.put("email", binding.emailEdit.getText().toString());
                                        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userInfo);
                                        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                    }
                                }
                        );
            }
        });

    }
}