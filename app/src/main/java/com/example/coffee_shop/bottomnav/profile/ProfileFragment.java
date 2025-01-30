package com.example.coffee_shop.bottomnav.profile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.coffee_shop.R;
import com.example.coffee_shop.WelcomeActivity;
import com.example.coffee_shop.databinding.FragmentProfileBinding;
import com.example.coffee_shop.utils.AnimationUtils;
import com.example.coffee_shop.utils.CloudinaryManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;

    private String currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        loadUserInfo();

        binding.profileImage.setOnClickListener(v -> openGallery());
        binding.btnSignOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(requireActivity(), WelcomeActivity.class));
            requireActivity().finish();
        });

        binding.menuEditProfile.setOnClickListener(v -> showEditProfileDialog());
        binding.menuShippingAddress.setOnClickListener(v -> showShippingAddressDialog());

        return binding.getRoot();
    }
    @Override
    public void onResume() {
        super.onResume();

        BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_nav);

        bottomNav.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.btn_color));

        ColorStateList defaultIconColorStateList = ContextCompat.getColorStateList(requireContext(), R.color.bottom_nav_icon_colors);
        bottomNav.setItemIconTintList(defaultIconColorStateList);

        bottomNav.setItemTextColor(defaultIconColorStateList);
    }

    private void loadUserInfo() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId)
                .get().addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        String username = snapshot.child("username").getValue(String.class);
                        String profileImage = snapshot.child("profileImage").getValue(String.class);

                        binding.usernameEdit.setText(username);

                        if (profileImage != null && !profileImage.isEmpty()) {
                            Glide.with(binding.profileImage.getContext())
                                    .load(profileImage)
                                    .circleCrop()
                                    .into(binding.profileImage);
                        }
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    uploadImageToCloudinary(imageUri);
                }
            });

    private void uploadImageToCloudinary(Uri imageUri) {
        new Thread(() -> {
            Bitmap bitmap = null;
            ByteArrayOutputStream baos = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);

                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                byte[] data = baos.toByteArray();

                Map<String, String> options = new HashMap<>();
                options.put("folder", "user_avatars");
                options.put("public_id", "user_" + System.currentTimeMillis());

                Map uploadResult = CloudinaryManager.getInstance().uploader().upload(data, options);
                String imageUrl = (String) uploadResult.get("secure_url");

                requireActivity().runOnUiThread(() -> {
                    if (imageUrl != null) {
                        saveImageUrlToDatabase(imageUrl);
                    } else {
                        Toast.makeText(requireContext(), "Image upload failed", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Error uploading image: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } finally {
                if (bitmap != null) {
                    bitmap.recycle();
                }
                if (baos != null) {
                    try {
                        baos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    private void saveImageUrlToDatabase(String imageUrl) {
        FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId)
                .child("profileImage").setValue(imageUrl)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Profile image updated!", Toast.LENGTH_SHORT).show();
                    loadUserInfo();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to update profile image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void showEditProfileDialog() {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View view = inflater.inflate(R.layout.dialog_edit_profile, null);

        EditText editUsername = view.findViewById(R.id.edit_username);
        EditText editPassword = view.findViewById(R.id.edit_password);
        Button btnSave = view.findViewById(R.id.btn_save);

        editUsername.setText(binding.usernameEdit.getText().toString());

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(view)
                .setCancelable(false)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            View dialogRoot = Objects.requireNonNull(dialog.getWindow()).getDecorView();
            AnimationUtils.animatePopup(dialogRoot);

            dialogRoot.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    View dialogContent = view.findViewById(R.id.dialog_edit);
                    if (dialogContent != null && isTouchInsideView(event, dialogContent)) {
                        AnimationUtils.animatePopupClose(dialogRoot, dialog::dismiss);
                        v.performClick();
                        return true;
                    }
                }
                return false;
            });
        });

        btnSave.setOnClickListener(v -> {
            String newUsername = editUsername.getText().toString().trim();
            String newPassword = editPassword.getText().toString().trim();

            if (!newUsername.isEmpty()) {
                View dialogRoot = Objects.requireNonNull(dialog.getWindow()).getDecorView();
                AnimationUtils.animatePopupClose(dialogRoot, () -> {
                    updateProfileInDatabase(newUsername, newPassword);
                    dialog.dismiss();
                });
            } else {
                Toast.makeText(requireContext(), "Username cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
    }

    private void updateProfileInDatabase(String username, String password) {
        String currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId)
                .child("username").setValue(username)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        binding.usernameEdit.setText(username);
                        Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                    }
                });

        if (!password.isEmpty()) {
            FirebaseAuth.getInstance().getCurrentUser().updatePassword(password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(requireContext(), "Password updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "Failed to update password", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void showShippingAddressDialog() {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View view = inflater.inflate(R.layout.dialog_shipping_address, null);

        EditText editFullName = view.findViewById(R.id.edit_full_name);
        EditText editAddress = view.findViewById(R.id.edit_address);
        EditText editCity = view.findViewById(R.id.edit_city);
        EditText editZipCode = view.findViewById(R.id.edit_zip_code);
        Button btnSaveAddress = view.findViewById(R.id.btn_save_address);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(view)
                .setCancelable(false)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            View dialogRoot = Objects.requireNonNull(dialog.getWindow()).getDecorView();
            AnimationUtils.animatePopup(dialogRoot);

            dialogRoot.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    View dialogContent = view.findViewById(R.id.dialog_content);
                    if (dialogContent != null && isTouchInsideView(event, dialogContent)) {
                        AnimationUtils.animatePopupClose(dialogRoot, dialog::dismiss);
                        v.performClick();
                        return true;
                    }
                }
                return false;
            });
        });

        btnSaveAddress.setOnClickListener(v -> {
            String newFullName = editFullName.getText().toString().trim();
            String newAddress = editAddress.getText().toString().trim();
            String newCity = editCity.getText().toString().trim();
            String newZipCode = editZipCode.getText().toString().trim();

            if (newFullName.isEmpty() || newAddress.isEmpty() || newCity.isEmpty() || newZipCode.isEmpty()) {
                Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            View dialogRoot = Objects.requireNonNull(dialog.getWindow()).getDecorView();
            AnimationUtils.animatePopupClose(dialogRoot, () -> {
                String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                Map<String, String> addressMap = new HashMap<>();
                addressMap.put("fullName", newFullName);
                addressMap.put("address", newAddress);
                addressMap.put("city", newCity);
                addressMap.put("zipCode", newZipCode);

                FirebaseDatabase.getInstance().getReference()
                        .child("Users")
                        .child(currentUserId)
                        .child("shippingAddress")
                        .setValue(addressMap)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(requireContext(), "Address updated successfully", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(requireContext(), "Failed to update address: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            });
        });

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
    }

    private boolean isTouchInsideView(MotionEvent event, View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        float x = event.getRawX();
        float y = event.getRawY();
        return !(x > location[0]) || !(x < location[0] + view.getWidth()) ||
                !(y > location[1]) || !(y < location[1] + view.getHeight());
    }

}
