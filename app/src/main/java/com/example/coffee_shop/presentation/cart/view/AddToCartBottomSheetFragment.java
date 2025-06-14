package com.example.coffee_shop.presentation.cart.view;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.coffee_shop.MyApp;
import com.example.coffee_shop.R;
import com.example.coffee_shop.data.models.CartItem;
import com.example.coffee_shop.databinding.BottomSheetAddToCartBinding;
import com.example.coffee_shop.data.models.Coffee;
import com.example.coffee_shop.presentation.common.viewmodel.SharedCoffeeViewModel;
import com.example.coffee_shop.presentation.common.viewmodel.SharedCoffeeViewModelFactory;
import com.example.coffee_shop.presentation.cart.viewmodel.CartViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.example.coffee_shop.core.utils.AnimationUtils;

public class AddToCartBottomSheetFragment extends BottomSheetDialogFragment {

    private static final String ARG_COFFEE = "coffee";
    private static final String ARG_SHOW_FAVORITE = "show_fav_button";

    private Coffee coffee;
    private boolean showFavoriteButton = true;

    private BottomSheetAddToCartBinding binding;
    private CartViewModel cartViewModel;
    private SharedCoffeeViewModel sharedViewModel;

    public static AddToCartBottomSheetFragment newInstance(Coffee coffee, boolean showFavoriteButton) {
        AddToCartBottomSheetFragment fragment = new AddToCartBottomSheetFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_COFFEE, coffee);
        args.putBoolean(ARG_SHOW_FAVORITE, showFavoriteButton);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BottomSheetAddToCartBinding.inflate(inflater, container, false);

        if (getArguments() != null) {
            coffee = (Coffee) getArguments().getSerializable(ARG_COFFEE);
            showFavoriteButton = getArguments().getBoolean(ARG_SHOW_FAVORITE, true);
            updateUI();
        }

        sharedViewModel = new ViewModelProvider(
                MyApp.getInstance(),
                new SharedCoffeeViewModelFactory(MyApp.getInstance())
        ).get(SharedCoffeeViewModel.class);

        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        cartViewModel.reset();

        observeFavorite();
        setupUI();
        observeCartState();

        return binding.getRoot();
    }

    private void observeFavorite() {
        if (!showFavoriteButton) {
            binding.btnFavorite.setVisibility(View.GONE);
            return;
        }

        sharedViewModel.isFavorite(coffee).observe(getViewLifecycleOwner(), isFav -> {
            boolean favorite = Boolean.TRUE.equals(isFav);
            int iconRes = favorite ? R.drawable.fav_icon : R.drawable.ic_favorite_border;
            binding.btnFavorite.setImageResource(iconRes);
        });
    }

    private void setupUI() {
        binding.btnClose.setOnClickListener(v -> animateAndDismiss());

        binding.btnAddToCart.setOnClickListener(v -> {
            if (coffee != null) {
                int quantity = cartViewModel.getQuantity().getValue() != null ? cartViewModel.getQuantity().getValue() : 1;
                String size = cartViewModel.getSelectedSize().getValue() != null ? cartViewModel.getSelectedSize().getValue() : "S";

                CartItem item = new CartItem(
                        coffee.getId(),
                        coffee.getName(),
                        coffee.getImageUrl(),
                        size,
                        quantity,
                        coffee.getPrice(),
                        binding.btnSugar.isChecked()
                );

                cartViewModel.addToCart(item);
            }
            animateAndDismiss();
        });

        if (showFavoriteButton) {
            binding.btnFavorite.setOnClickListener(v -> {
                sharedViewModel.toggleFavorite(coffee);
                AnimationUtils.animateFavorite(v);
            });
        }

        binding.btnIncrease.setOnClickListener(v -> cartViewModel.increaseQuantity());
        binding.btnDecrease.setOnClickListener(v -> cartViewModel.decreaseQuantity());

        binding.btnSizeS.setOnClickListener(v -> cartViewModel.selectSize("S"));
        binding.btnSizeM.setOnClickListener(v -> cartViewModel.selectSize("M"));
        binding.btnSizeL.setOnClickListener(v -> cartViewModel.selectSize("L"));
    }

    private void observeCartState() {
        cartViewModel.getQuantity().observe(getViewLifecycleOwner(), quantity ->
                binding.textQuantity.setText(String.valueOf(quantity))
        );

        cartViewModel.getSelectedSize().observe(getViewLifecycleOwner(), size -> {
            binding.btnSizeS.setChecked("S".equals(size));
            binding.btnSizeM.setChecked("M".equals(size));
            binding.btnSizeL.setChecked("L".equals(size));
        });
    }

    private void updateUI() {
        if (coffee != null) {
            binding.textTitle.setText(coffee.getName());
            binding.textDescription.setText(coffee.getDescription());
            Glide.with(requireContext())
                    .load(coffee.getImageUrl())
                    .into(binding.imageProduct);
        }
    }

    private void animateAndDismiss() {
        View dialogView = getDialog() != null && getDialog().getWindow() != null
                ? getDialog().getWindow().getDecorView()
                : binding.getRoot();

        AnimationUtils.animateSlideDownAndDismiss(dialogView, this::dismiss);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog d = (BottomSheetDialog) dialogInterface;
            FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                bottomSheet.setBackground(null);
            }
        });
        return dialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}