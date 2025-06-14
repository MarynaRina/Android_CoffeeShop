package com.example.coffee_shop.presentation.common.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffee_shop.R;
import com.example.coffee_shop.core.utils.AnimationUtils;
import com.example.coffee_shop.data.models.Coffee;
import com.example.coffee_shop.databinding.ItemSearchCoffeeBinding;
import com.example.coffee_shop.presentation.common.viewmodel.SharedCoffeeViewModel;

public class SearchAdapter extends ListAdapter<Coffee, SearchAdapter.CoffeeViewHolder> {
    private final SharedCoffeeViewModel sharedViewModel;
    private final LifecycleOwner lifecycleOwner;
    private OnItemActionListener actionListener;

    public SearchAdapter(SharedCoffeeViewModel sharedViewModel, LifecycleOwner lifecycleOwner) {
        super(DIFF_CALLBACK);
        this.sharedViewModel = sharedViewModel;
        this.lifecycleOwner = lifecycleOwner;
    }

    public void setOnItemActionListener(OnItemActionListener listener) {
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public CoffeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemSearchCoffeeBinding binding = ItemSearchCoffeeBinding.inflate(inflater, parent, false);
        return new CoffeeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CoffeeViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class CoffeeViewHolder extends RecyclerView.ViewHolder {
        private final ItemSearchCoffeeBinding binding;

        public CoffeeViewHolder(@NonNull ItemSearchCoffeeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Coffee coffee) {
            binding.setCoffee(coffee);
            binding.executePendingBindings();

            binding.addToCartButton.setOnClickListener(v -> {
                if (actionListener != null) actionListener.onAddToCart(coffee);
            });

            binding.btnFavorite.setOnClickListener(v -> {
                if (actionListener != null) actionListener.onToggleFavorite(coffee);
                AnimationUtils.animateFavorite(v);
            });

            sharedViewModel.isFavorite(coffee).observe(lifecycleOwner, isFavorite -> {
                binding.btnFavorite.setImageResource(Boolean.TRUE.equals(isFavorite)
                        ? R.drawable.fav_icon
                        : R.drawable.ic_favorite_border);
            });
        }
    }

    private static final DiffUtil.ItemCallback<Coffee> DIFF_CALLBACK = new DiffUtil.ItemCallback<Coffee>() {
        @Override
        public boolean areItemsTheSame(@NonNull Coffee oldItem, @NonNull Coffee newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Coffee oldItem, @NonNull Coffee newItem) {
            return oldItem.equals(newItem);
        }
    };

    public interface OnItemActionListener {
        void onAddToCart(Coffee coffee);
        void onToggleFavorite(Coffee coffee);
    }
}