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
import com.example.coffee_shop.presentation.profile.viewmodel.ProfileViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ShippingAddressDialogFragment extends DialogFragment {

    private EditText etFullName, etAddress, etCity, etZip, etPhone;
    private ProfileViewModel viewModel;
    private View root;

    @Override public void onStart() {
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

        root = inf.inflate(R.layout.dialog_shipping_address, container, false);

        viewModel  = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        etFullName = root.findViewById(R.id.edit_full_name);
        etAddress  = root.findViewById(R.id.edit_address);
        etCity     = root.findViewById(R.id.edit_city);
        etZip      = root.findViewById(R.id.edit_zip_code);
        etPhone    = root.findViewById(R.id.edit_phone);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .child(user.getUid())
                    .child("shippingAddress");

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        etFullName.setText(snapshot.child("fullName").getValue(String.class));
                        etAddress.setText(snapshot.child("address").getValue(String.class));
                        etCity.setText(snapshot.child("city").getValue(String.class));
                        etZip.setText(snapshot.child("zip").getValue(String.class));
                        etPhone.setText(snapshot.child("phoneNumber").getValue(String.class));
                    }
                }

                @Override public void onCancelled(@NonNull DatabaseError error) {}
            });
        }

        Button btnSave = root.findViewById(R.id.btn_save_address);
        btnSave.setOnClickListener(v -> onSave());

        viewModel.getToastMessage()
                .observe(getViewLifecycleOwner(),
                        msg -> Snackbar.make(root, msg, Snackbar.LENGTH_LONG).show());

        return root;
    }

    private void onSave() {
        String fn  = etFullName.getText().toString().trim();
        String ad  = etAddress .getText().toString().trim();
        String ct  = etCity    .getText().toString().trim();
        String zp  = etZip     .getText().toString().trim();
        String ph = etPhone   .getText().toString().trim();

        if (fn.isEmpty() || ad.isEmpty() || ct.isEmpty() || zp.isEmpty() || ph.isEmpty()) {
            return;
        }

        viewModel.updateShippingAddress(fn, ad, ct, zp, ph);
        dismiss();
    }
}