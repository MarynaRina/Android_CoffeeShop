package com.example.coffee_shop.presentation.profile.view;

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
import com.example.coffee_shop.presentation.profile.viewmodel.ProfileViewModel;

public class ShippingAddressDialogFragment extends DialogFragment {
    private EditText editFullName, editAddress, editCity, editZipCode;
    private ProfileViewModel profileViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_shipping_address, container, false);
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        editFullName = view.findViewById(R.id.edit_full_name);
        editAddress = view.findViewById(R.id.edit_address);
        editCity = view.findViewById(R.id.edit_city);
        editZipCode = view.findViewById(R.id.edit_zip_code);
        Button btnSaveAddress = view.findViewById(R.id.btn_save_address);

        btnSaveAddress.setOnClickListener(v -> {
            String fullName = editFullName.getText().toString().trim();
            String address = editAddress.getText().toString().trim();
            String city = editCity.getText().toString().trim();
            String zipCode = editZipCode.getText().toString().trim();

            if (fullName.isEmpty() || address.isEmpty() || city.isEmpty() || zipCode.isEmpty()) {
                Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            profileViewModel.updateShippingAddress(fullName, address, city, zipCode);
            dismiss();
        });

        profileViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}