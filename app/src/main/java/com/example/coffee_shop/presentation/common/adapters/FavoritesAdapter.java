package com.example.coffee_shop.presentation.common.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffee_shop.R;
import com.example.coffee_shop.core.utils.AnimationUtils;
import com.example.coffee_shop.data.models.Coffee;
import com.example.coffee_shop.databinding.ItemFavoritesCoffeeBinding;
import com.example.coffee_shop.presentation.cart.view.AddToCartBottomSheetFragment;
import com.example.coffee_shop.presentation.common.viewmodel.SharedCoffeeViewModel;

public class FavoritesAdapter extends ListAdapter<Coffee, FavoritesAdapter.CoffeeViewHolder> {
    private final SharedCoffeeViewModel sharedViewModel;
    private final LifecycleOwner lifecycleOwner;
    private OnItemActionListener actionListener;

    public FavoritesAdapter(SharedCoffeeViewModel sharedViewModel, LifecycleOwner lifecycleOwner) {
        super(DIFF_CALLBACK);
        this.sharedViewModel = sharedViewModel;
        this.lifecycleOwner = lifecycleOwner;
    }

    @NonNull
    @Override
    public CoffeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemFavoritesCoffeeBinding binding = ItemFavoritesCoffeeBinding.inflate(inflater, parent, false);
        return new CoffeeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CoffeeViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public void setOnItemActionListener(OnItemActionListener listener) {
        this.actionListener = listener;
    }

    class CoffeeViewHolder extends RecyclerView.ViewHolder {
        private final ItemFavoritesCoffeeBinding binding;

        public CoffeeViewHolder(@NonNull ItemFavoritesCoffeeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.addToCartButton.setOnClickListener(v -> {
                Coffee coffee = binding.getCoffee();
                if (coffee != null) {
                    FragmentActivity activity = (FragmentActivity) binding.getRoot().getContext();
                    AddToCartBottomSheetFragment fragment = AddToCartBottomSheetFragment.newInstance(coffee, false); // <-- додано false
                    fragment.show(activity.getSupportFragmentManager(), "AddToCartBottomSheet");
                }
            });

            binding.btnFavorite.setOnClickListener(v -> {
                Coffee coffee = binding.getCoffee();
                if (coffee != null && actionListener != null) {
                    actionListener.onToggleFavorite(coffee);
                }
                AnimationUtils.animateFavorite(v);

            });
        }

        public void bind(Coffee coffee) {
            binding.setCoffee(coffee);
            binding.executePendingBindings();

            sharedViewModel.isFavorite(coffee).observe(lifecycleOwner, isFavorite -> {
                binding.btnFavorite.setImageResource(
                        Boolean.TRUE.equals(isFavorite)
                                ? R.drawable.fav_icon
                                : R.drawable.ic_favorite_border
                );
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
        void onToggleFavorite(Coffee coffee);

        void onItemClick(Coffee coffee);
    }
}