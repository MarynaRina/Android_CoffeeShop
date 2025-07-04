package com.example.coffee_shop.presentation.profile.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.coffee_shop.R;
import com.example.coffee_shop.databinding.FragmentProfileBinding;
import com.example.coffee_shop.presentation.auth.view.WelcomeActivity;
import com.example.coffee_shop.presentation.profile.viewmodel.ProfileViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel       viewModel;

    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), r -> {
                if (r.getResultCode() == Activity.RESULT_OK && r.getData() != null) {
                    Uri uri = r.getData().getData();
                    if (uri != null) viewModel.updateProfileImage(uri);
                }
            });

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inf,
                             @Nullable ViewGroup c,
                             @Nullable Bundle s) {

        binding  = FragmentProfileBinding.inflate(inf, c, false);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        observeData();
        initClicks();
        return binding.getRoot();
    }

    private void observeData() {
        viewModel.getUserProfile().observe(getViewLifecycleOwner(), p -> {
            if (p == null) return;
            binding.usernameEdit.setText(p.getUsername());
            if (p.getProfileImage() != null && !p.getProfileImage().isEmpty()) {
                Glide.with(this).load(p.getProfileImage()).circleCrop().into(binding.profileImage);
            }
        });

        viewModel.getToastMessage().observe(getViewLifecycleOwner(),
                msg -> Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show());

        viewModel.getImageUploadResult().observe(getViewLifecycleOwner(), url -> {
            if (url != null) Glide.with(this).load(url).circleCrop().into(binding.profileImage);
        });

        viewModel.getLanguageSaved().observe(getViewLifecycleOwner(),
                lang -> requireActivity().recreate());
    }

    private void initClicks() {
        binding.profileImage     .setOnClickListener(v -> openGallery());
        binding.menuEditProfile  .setOnClickListener(v -> new EditProfileDialogFragment()
                .show(getParentFragmentManager(), "EditProfileDialog"));
        binding.menuShippingAddress.setOnClickListener(v -> new ShippingAddressDialogFragment()
                .show(getParentFragmentManager(), "ShippingAddressDialog"));
        binding.menuChangePassword.setOnClickListener(v -> new LanguageDialogFragment()
                .show(getParentFragmentManager(), "LanguageDialog"));

        binding.btnSignOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(requireContext(), WelcomeActivity.class));
            requireActivity().finish();
        });
    }

    private void openGallery() {
        Intent pick = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(pick);
    }

    private void updateBottomNav(Activity act) {
        BottomNavigationView nav = act.findViewById(R.id.bottom_nav);
        if (nav == null) return;
        nav.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.btn_color));
        nav.setItemIconTintList(ContextCompat.getColorStateList(requireContext(), R.color.bottom_nav_icon_colors));
        nav.setItemTextColor   (ContextCompat.getColorStateList(requireContext(), R.color.bottom_nav_icon_colors));
    }

    @Override public void onResume() {
        super.onResume();
        updateBottomNav(requireActivity());
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}