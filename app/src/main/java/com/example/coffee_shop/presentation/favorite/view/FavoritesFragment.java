package com.example.coffee_shop.presentation.favorite.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.coffee_shop.MyApp;
import com.example.coffee_shop.core.utils.GridSpacingItemDecoration;
import com.example.coffee_shop.data.models.Coffee;
import com.example.coffee_shop.databinding.FragmentFavoritesBinding;
import com.example.coffee_shop.presentation.cart.view.AddToCartBottomSheetFragment;
import com.example.coffee_shop.presentation.common.adapters.FavoritesAdapter;
import com.example.coffee_shop.presentation.common.viewmodel.SharedCoffeeViewModel;
import com.example.coffee_shop.presentation.common.viewmodel.SharedCoffeeViewModelFactory;

public class FavoritesFragment extends Fragment implements FavoritesAdapter.OnItemActionListener {
    private FragmentFavoritesBinding binding;
    private SharedCoffeeViewModel sharedViewModel;
    private FavoritesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedViewModel = new ViewModelProvider(
                MyApp.getInstance(),
                new SharedCoffeeViewModelFactory(MyApp.getInstance())
        ).get(SharedCoffeeViewModel.class);

        setupRecyclerView();
        observeFavorites();
    }

    private void observeFavorites() {
        sharedViewModel.getAllFavoriteCoffees().observe(getViewLifecycleOwner(), favorites -> {
            adapter.submitList(favorites);
            binding.emptyState.setVisibility(favorites == null || favorites.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    private void setupRecyclerView() {
        adapter = new FavoritesAdapter(sharedViewModel, getViewLifecycleOwner());
        adapter.setOnItemActionListener(this);

        binding.recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        int spacingInPixels = GridSpacingItemDecoration.dpToPx(16);
        binding.recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, spacingInPixels, true));
        binding.recyclerView.setAdapter(adapter);
    }

    @Override
    public void onToggleFavorite(Coffee coffee) {
        sharedViewModel.toggleFavorite(coffee);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClick(Coffee coffee) {
        AddToCartBottomSheetFragment.newInstance(coffee, false)
                .show(getParentFragmentManager(), null);
    }
}