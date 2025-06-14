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

import com.example.coffee_shop.databinding.FragmentCartBinding;
import com.example.coffee_shop.presentation.cart.viewmodel.CartViewModel;
import com.example.coffee_shop.presentation.common.adapters.CartAdapter;

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
        binding.btnClearCart.setOnClickListener(v -> {
            cartViewModel.clearCart();
        });

        binding.btnCheckout.setOnClickListener(v -> {
            if (cartAdapter.getItemCount() > 0) {
                Toast.makeText(requireContext(), "Order placed successfully!", Toast.LENGTH_SHORT).show();
                cartViewModel.clearCart();
            } else {
                Toast.makeText(requireContext(), "Your cart is empty!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView() {
        cartAdapter = new CartAdapter(item -> cartViewModel.deleteCartItem(item));
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(cartAdapter);
    }

    private void observeCart() {
        cartViewModel.getCartItems().observe(getViewLifecycleOwner(), items -> {
            cartAdapter.submitList(items);
            updateUI(items);  // Замінюємо метод для покращеного оновлення UI
        });
    }

    private void updateUI(java.util.List<com.example.coffee_shop.data.models.CartItem> items) {
        // Рахуємо загальну суму
        double total = 0.0;
        for (com.example.coffee_shop.data.models.CartItem item : items) {
            total += item.getTotalPrice();
        }
        binding.totalPrice.setText(String.format("$%.2f", total));

        // Remove this line - it creates an unused adapter
        // CartAdapter adapter = new CartAdapter(item -> cartViewModel.deleteCartItem(item));

        // Активуємо або деактивуємо кнопки в залежності від наявності товарів
        boolean hasItems = !items.isEmpty();
        binding.btnClearCart.setEnabled(hasItems);
        binding.btnCheckout.setEnabled(hasItems);

        // Показуємо/приховуємо повідомлення про порожній кошик
        binding.emptyCartMessage.setVisibility(hasItems ? View.GONE : View.VISIBLE);
        binding.recyclerView.setVisibility(hasItems ? View.VISIBLE : View.GONE);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}