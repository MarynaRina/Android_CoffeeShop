package com.example.coffee_shop.presentation.profile.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.coffee_shop.R;
import com.example.coffee_shop.data.models.UserProfile;
import com.example.coffee_shop.presentation.profile.viewmodel.ProfileViewModel;
import com.google.android.material.snackbar.Snackbar;

public class EditProfileDialogFragment extends DialogFragment {

    private EditText etUsername;
    private EditText etPassword;
    private ProfileViewModel viewModel;

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inf,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inf.inflate(R.layout.dialog_edit_profile, container, false);

        viewModel  = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        etUsername = root.findViewById(R.id.edit_username);
        etPassword = root.findViewById(R.id.edit_password);
        Button btnSave = root.findViewById(R.id.btn_save);

        UserProfile p = viewModel.getUserProfile().getValue();
        if (p != null) etUsername.setText(p.getUsername());

        btnSave.setOnClickListener(v -> onSave(root));

        viewModel.getToastMessage()
                .observe(getViewLifecycleOwner(),
                        msg -> Snackbar.make(root, msg, Snackbar.LENGTH_LONG).show());

        return root;
    }

    private void onSave(View root) {
        String newUser = etUsername.getText().toString().trim();
        String newPass = etPassword.getText().toString().trim();

        if (newUser.isEmpty()) {
            return;
        }

        viewModel.updateUserProfile(newUser);
        if (!newPass.isEmpty()) viewModel.updatePassword(newPass);

        dismiss();
    }
}