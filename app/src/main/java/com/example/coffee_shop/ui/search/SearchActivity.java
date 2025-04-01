package com.example.coffee_shop.ui.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.coffee_shop.adapters.SearchAdapter;
import com.example.coffee_shop.core.utils.AnimationUtils;
import com.example.coffee_shop.data.models.Coffee;
import com.example.coffee_shop.databinding.ActivitySearchBinding;
import com.example.coffee_shop.ui.shared.SharedCoffeeViewModel;

public class SearchActivity extends AppCompatActivity {
    private ActivitySearchBinding binding;
    private SearchAdapter adapter;
    private SearchViewModel viewModel;
    private SharedCoffeeViewModel sharedViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Ініціалізація ViewModels
        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        sharedViewModel = new ViewModelProvider(this).get(SharedCoffeeViewModel.class);
        setupRecyclerView();
        observeViewModel();
        setupListeners();

        viewModel.loadCoffees();
    }

    private void setupRecyclerView() {
        adapter = new SearchAdapter(sharedViewModel); // Передаємо sharedViewModel в адаптер
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        adapter.setOnItemActionListener(new SearchAdapter.OnItemActionListener() {
            @Override
            public void onAddToCart(Coffee coffee) {
                // Логіка додавання до кошика перенесена в адаптер
            }

            @Override
            public void onToggleFavorite(Coffee coffee) {
                sharedViewModel.toggleFavorite(coffee); // Перемикаємо улюблене

            }
        });
    }

    private void observeViewModel() {
        viewModel.getFilteredCoffees().observe(this, adapter::submitList);
    }

    private void setupListeners() {
        binding.searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.backButton.setOnClickListener(v -> finish());
    }
}