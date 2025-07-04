package com.example.coffee_shop.presentation.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.coffee_shop.R;
import com.example.coffee_shop.databinding.FragmentOrderSuccessBinding;
import com.example.coffee_shop.data.models.ShippingAddress;
import com.example.coffee_shop.presentation.cart.view.CartFragment;
import com.example.coffee_shop.presentation.home.view.HomeFragment;

public class OrderSuccessFragment extends Fragment {

    private FragmentOrderSuccessBinding binding;

    private static final String ARG_NAME = "name";
    private static final String ARG_PHONE = "phone";
    private static final String ARG_ADDRESS = "address";
    private static final String ARG_CITY = "city";
    private static final String ARG_ZIP = "zip";
    private static final String ARG_TOTAL = "total";
    private static final String ARG_ORDER_ID = "orderId";

    public static OrderSuccessFragment newInstance(String orderId, ShippingAddress address, double total) {
        OrderSuccessFragment fragment = new OrderSuccessFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ORDER_ID, orderId);
        args.putString(ARG_NAME, address.getFullName());
        args.putString(ARG_PHONE, address.getPhoneNumber());
        args.putString(ARG_ADDRESS, address.getAddress());
        args.putString(ARG_CITY, address.getCity());
        args.putString(ARG_ZIP, address.getZip());
        args.putDouble(ARG_TOTAL, total);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOrderSuccessBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            String orderId = getArguments().getString(ARG_ORDER_ID);
            String name = getArguments().getString(ARG_NAME);
            String phone = getArguments().getString(ARG_PHONE);
            String address = getArguments().getString(ARG_ADDRESS);
            String city = getArguments().getString(ARG_CITY);
            String zip = getArguments().getString(ARG_ZIP);
            String shortOrderId = orderId.substring(0, 8).toUpperCase();
            double total = getArguments().getDouble(ARG_TOTAL);

            binding.setOrderId("Замовлення №" + shortOrderId);
            binding.setFullName(name);
            binding.setPhoneNumber(phone);
            binding.setAddress(address + ", " + city + " " + zip);
            binding.setTotalPrice(String.format("₴%.2f", total));
        }

        binding.btnBackHome.setOnClickListener(v ->
                requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new HomeFragment())
                        .addToBackStack(null)
                        .commit()
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}