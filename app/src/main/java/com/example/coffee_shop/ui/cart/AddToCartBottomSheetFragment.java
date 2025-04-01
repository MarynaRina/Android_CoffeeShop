package com.example.coffee_shop.ui.cart;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.SavedStateViewModelFactory;
import androidx.lifecycle.ViewModelProvider;

import com.example.coffee_shop.R;
import com.example.coffee_shop.databinding.BottomSheetAddToCartBinding;
import com.example.coffee_shop.data.models.Coffee;
import com.example.coffee_shop.ui.shared.SharedCoffeeViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.example.coffee_shop.core.utils.AnimationUtils;

public class AddToCartBottomSheetFragment extends BottomSheetDialogFragment {
    private static final String ARG_COFFEE = "coffee";
    private Coffee coffee;
    private BottomSheetAddToCartBinding binding;
    private CartViewModel cartViewModel;
    private SharedCoffeeViewModel sharedViewModel;

    public static AddToCartBottomSheetFragment newInstance(Coffee coffee) {
        AddToCartBottomSheetFragment fragment = new AddToCartBottomSheetFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_COFFEE, coffee);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetAddToCartBinding.inflate(inflater, container, false);

        if (getArguments() != null) {
            coffee = (Coffee) getArguments().getSerializable(ARG_COFFEE);
            updateUI();
        }

        // Замінюємо старий метод на створення ViewModel за допомогою SavedStateViewModelFactory
        sharedViewModel = new ViewModelProvider(this, new SavedStateViewModelFactory(requireActivity().getApplication(), this)).get(SharedCoffeeViewModel.class);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        cartViewModel.reset();

        sharedViewModel.isFavorite(coffee).observe(getViewLifecycleOwner(), this::updateFavoriteButton);

        setupUI();
        observeViewModel();

        return binding.getRoot();
    }

    private void updateFavoriteButton(Boolean isFav) {
        int iconRes = Boolean.TRUE.equals(isFav) ? R.drawable.fav_icon : R.drawable.ic_favorite_border;
        binding.btnFavorite.setImageResource(iconRes);
    }

    private void setupUI() {
        binding.btnClose.setOnClickListener(v -> animateAndDismiss());
        binding.btnAddToCart.setOnClickListener(v -> animateAndDismiss());

        binding.btnFavorite.setOnClickListener(v -> {
            Boolean currentFav = sharedViewModel.isFavorite(coffee).getValue();
            if (Boolean.TRUE.equals(currentFav)) {
                sharedViewModel.removeCoffeeFromFavorites(coffee);
            } else {
                sharedViewModel.addCoffeeToFavorites(coffee);
            }
            AnimationUtils.animateFavorite(v);
        });

        binding.btnIncrease.setOnClickListener(v -> cartViewModel.increaseQuantity());
        binding.btnDecrease.setOnClickListener(v -> cartViewModel.decreaseQuantity());

        binding.btnSizeS.setOnClickListener(v -> cartViewModel.selectSize("S"));
        binding.btnSizeM.setOnClickListener(v -> cartViewModel.selectSize("M"));
        binding.btnSizeL.setOnClickListener(v -> cartViewModel.selectSize("L"));
    }

    private void observeViewModel() {
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
            Glide.with(requireContext()).load(coffee.getImageUrl()).into(binding.imageProduct);
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