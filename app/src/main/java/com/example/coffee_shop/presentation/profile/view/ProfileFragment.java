package com.example.coffee_shop.presentation.profile.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.coffee_shop.R;
import com.example.coffee_shop.databinding.FragmentProfileBinding;
import com.example.coffee_shop.presentation.auth.view.WelcomeActivity;
import com.example.coffee_shop.presentation.profile.viewmodel.ProfileViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private ProfileViewModel profileViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ініціалізація ProfileViewModel із LifecycleOwner
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        profileViewModel.loadUserInfo(getViewLifecycleOwner());

        setupObservers();
        setupUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        profileViewModel.loadUserInfo(getViewLifecycleOwner()); // 🔁 Оновлення профілю
        updateBottomNavigation(requireActivity());
    }

    private void setupObservers() {
        profileViewModel.getUserProfile().observe(getViewLifecycleOwner(), userProfile -> {
            if (userProfile != null) {
                binding.usernameEdit.setText(userProfile.getUsername());
                Log.d("ProfileFragment", "Image URL: " + userProfile.getProfileImage());
                if (userProfile.getProfileImage() != null && !userProfile.getProfileImage().isEmpty()) {
                    Glide.with(requireContext())
                            .load(userProfile.getProfileImage())
                            .circleCrop()
                            .into(binding.profileImage);
                }
            }
        });

        profileViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        profileViewModel.getImageUploadResult().observe(getViewLifecycleOwner(), imageUrl -> {
            if (imageUrl != null) {
                Toast.makeText(requireContext(), "Profile image updated!", Toast.LENGTH_SHORT).show();
                Glide.with(requireContext())
                        .load(imageUrl)
                        .circleCrop()
                        .into(binding.profileImage);
            }
        });
    }

    private void setupUI() {
        binding.profileImage.setOnClickListener(v -> openGallery());
        binding.btnSignOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(requireActivity(), WelcomeActivity.class));
            requireActivity().finish();
        });
        binding.menuChangePassword.setOnClickListener(v -> showLanguageSelectionDialog());

        binding.menuEditProfile.setOnClickListener(v -> showEditProfileDialog());
        binding.menuShippingAddress.setOnClickListener(v -> showShippingAddressDialog());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    profileViewModel.updateProfileImage(imageUri, getViewLifecycleOwner());
                }
            });

    private void showEditProfileDialog() {
        EditProfileDialogFragment dialogFragment = new EditProfileDialogFragment();
        dialogFragment.show(getParentFragmentManager(), "EditProfileDialog");
    }

    private void showShippingAddressDialog() {
        ShippingAddressDialogFragment dialogFragment = new ShippingAddressDialogFragment();
        dialogFragment.show(getParentFragmentManager(), "ShippingAddressDialog");
    }

    private void showLanguageSelectionDialog() {
        LanguageDialogFragment dialogFragment = new LanguageDialogFragment();
        dialogFragment.show(getParentFragmentManager(), "LanguageDialog");
    }

    private void updateBottomNavigation(Activity activity) {
        BottomNavigationView bottomNav = activity.findViewById(R.id.bottom_nav);
        bottomNav.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.btn_color));
        bottomNav.setItemIconTintList(ContextCompat.getColorStateList(requireContext(), R.color.bottom_nav_icon_colors));
        bottomNav.setItemTextColor(ContextCompat.getColorStateList(requireContext(), R.color.bottom_nav_icon_colors));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}