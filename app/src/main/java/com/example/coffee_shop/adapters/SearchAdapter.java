package com.example.coffee_shop.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffee_shop.R;
import com.example.coffee_shop.core.utils.AnimationUtils;
import com.example.coffee_shop.data.models.Coffee;
import com.example.coffee_shop.databinding.ItemSearchCoffeeBinding;
import com.example.coffee_shop.ui.cart.AddToCartBottomSheetFragment;
import com.example.coffee_shop.ui.shared.SharedCoffeeViewModel;

public class SearchAdapter extends ListAdapter<Coffee, SearchAdapter.CoffeeViewHolder> {
    private static SharedCoffeeViewModel sharedViewModel;
    private static OnItemActionListener actionListener;

    public SearchAdapter(SharedCoffeeViewModel sharedViewModel) {
        super(DIFF_CALLBACK);
        this.sharedViewModel = sharedViewModel;
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

    public void setOnItemActionListener(OnItemActionListener listener) {
        this.actionListener = listener;
    }

    static class CoffeeViewHolder extends RecyclerView.ViewHolder {
        private final ItemSearchCoffeeBinding binding;

        public CoffeeViewHolder(@NonNull ItemSearchCoffeeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            // Ініціалізація слухачів у конструкторі, щоб вони не дублювалися
            binding.addToCartButton.setOnClickListener(v -> {
                Coffee coffee = binding.getCoffee();
                if (coffee != null) {
                    FragmentActivity activity = (FragmentActivity) binding.getRoot().getContext();
                    AddToCartBottomSheetFragment fragment = AddToCartBottomSheetFragment.newInstance(coffee);
                    fragment.show(activity.getSupportFragmentManager(), "AddToCartBottomSheet");
                }
            });

            binding.btnFavorite.setOnClickListener(v -> {
                Coffee coffee = binding.getCoffee();
                if (coffee != null && actionListener != null) {
                    actionListener.onToggleFavorite(coffee);
                    AnimationUtils.animateFavorite(v);
                }
            });
        }

        public void bind(Coffee coffee) {
            binding.setCoffee(coffee);
            binding.executePendingBindings();

            // Спостереження за станом улюбленого
            FragmentActivity activity = (FragmentActivity) binding.getRoot().getContext();
            sharedViewModel.isFavorite(coffee).observe(activity, isFavorite -> {
                if (isFavorite) {
                    binding.btnFavorite.setImageResource(R.drawable.fav_icon); // Заповнене серце
                } else {
                    binding.btnFavorite.setImageResource(R.drawable.ic_favorite_border); // Порожнє серце
                }
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