package com.example.coffee_shop.presentation.cart.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.coffee_shop.R;
import com.example.coffee_shop.data.models.CartItem;
import com.example.coffee_shop.data.models.ShippingAddress;
import com.example.coffee_shop.databinding.FragmentCartBinding;
import com.example.coffee_shop.presentation.cart.viewmodel.CartViewModel;
import com.example.coffee_shop.presentation.common.adapters.CartAdapter;
import com.example.coffee_shop.presentation.order.OrderSuccessFragment;

import java.util.List;

public class CartFragment extends Fragment {
    private FragmentCartBinding binding;
    private CartViewModel cartViewModel;
    private CartAdapter cartAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cartViewModel = new ViewModelProvider(
                requireActivity(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())
        ).get(CartViewModel.class);

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            binding.getRoot().setPadding(
                    binding.getRoot().getPaddingLeft(),
                    binding.getRoot().getPaddingTop(),
                    binding.getRoot().getPaddingRight(),
                    systemBars.bottom
            );

            return insets;
        });

        setupRecyclerView();
        setupButtons();
        observeCart();
    }

    private void setupButtons() {
        binding.btnClearCart.setOnClickListener(v -> cartViewModel.clearCart());

        binding.btnCheckout.setOnClickListener(v -> {
            if (cartAdapter.getItemCount() > 0) {
                double total = calculateTotal(cartAdapter.getCurrentList());

                cartViewModel.placeOrder(total, new CartViewModel.OrderCallback() {
                    @Override
                    public void onSuccess(String orderId, String fullName, String phoneNumber, String fullAddress, ShippingAddress address) {
                        Toast.makeText(requireContext(), "Order placed successfully!", Toast.LENGTH_SHORT).show();

                        OrderSuccessFragment fragment = OrderSuccessFragment.newInstance(orderId, address, total);

                        requireActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container, fragment)
                                .addToBackStack(null)
                                .commit();
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(requireContext(), "Failed to place order: " + message, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(requireContext(), "Your cart is empty!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private double calculateTotal(List<CartItem> items) {
        double total = 0.0;
        for (CartItem item : items) {
            total += item.getTotalPrice();
        }
        return total;
    }

    private void setupRecyclerView() {
        cartAdapter = new CartAdapter(item -> cartViewModel.deleteCartItem(item));
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(cartAdapter);
    }

    private void observeCart() {
        cartViewModel.getCartItems().observe(getViewLifecycleOwner(), items -> {
            cartAdapter.submitList(items);
            updateUI(items);
        });
    }

    private void updateUI(List<CartItem> items) {
        double total = 0.0;
        for (CartItem item : items) {
            total += item.getTotalPrice();
        }
        binding.totalPrice.setText(String.format("₴%.2f", total));

        boolean hasItems = !items.isEmpty();
        binding.btnClearCart.setEnabled(hasItems);
        binding.btnCheckout.setEnabled(hasItems);
        binding.emptyCartMessage.setVisibility(hasItems ? View.GONE : View.VISIBLE);
        binding.recyclerView.setVisibility(hasItems ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}