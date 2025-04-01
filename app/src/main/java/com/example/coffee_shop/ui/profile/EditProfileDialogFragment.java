package com.example.coffee_shop.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.coffee_shop.R;
import com.example.coffee_shop.data.models.UserProfile;

public class EditProfileDialogFragment extends DialogFragment {
    private EditText editUsername, editPassword;
    private ProfileViewModel profileViewModel;
    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_edit_profile, container, false);
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        editUsername = view.findViewById(R.id.edit_username);
        editPassword = view.findViewById(R.id.edit_password);
        Button btnSave = view.findViewById(R.id.btn_save);

        UserProfile userProfile = profileViewModel.getUserProfile().getValue();
        if (userProfile != null) {
            editUsername.setText(userProfile.getUsername());
        }


        btnSave.setOnClickListener(v -> {
            String newUsername = editUsername.getText().toString().trim();
            String newPassword = editPassword.getText().toString().trim();

            if (!newUsername.isEmpty()) {
                profileViewModel.updateUserProfile(newUsername, newPassword);
                dismiss();
            } else {
                Toast.makeText(getContext(), "Username cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        profileViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}