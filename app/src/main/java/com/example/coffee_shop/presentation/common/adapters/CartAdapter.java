package com.example.coffee_shop.presentation.common.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffee_shop.data.models.CartItem;
import com.example.coffee_shop.databinding.ItemCartBinding;

public class CartAdapter extends ListAdapter<CartItem, CartAdapter.CartViewHolder> {

    private CartItemDeleteListener deleteListener;

    public interface CartItemDeleteListener {
        void onDeleteCartItem(CartItem item);
    }

    public CartAdapter(CartItemDeleteListener deleteListener) {
        super(DIFF_CALLBACK);
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemCartBinding binding = ItemCartBinding.inflate(inflater, parent, false);
        return new CartViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        private final ItemCartBinding binding;

        public CartViewHolder(ItemCartBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(CartItem item) {
            binding.setItem(item);
            binding.setOnDeleteClick(v -> {
                if (deleteListener != null) {
                    deleteListener.onDeleteCartItem(item);
                }
            });
            Context context = binding.getRoot().getContext();

            binding.itemSize.setText(item.getLocalizedSizeText(context));
            binding.itemSugar.setText(item.getLocalizedSugarText(context));
            binding.executePendingBindings();
        }
    }

    private static final DiffUtil.ItemCallback<CartItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<CartItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull CartItem oldItem, @NonNull CartItem newItem) {
            return oldItem.getCoffeeId().equals(newItem.getCoffeeId())
                    && oldItem.getSelectedSize().equals(newItem.getSelectedSize());
        }

        @Override
        public boolean areContentsTheSame(@NonNull CartItem oldItem, @NonNull CartItem newItem) {
            return oldItem.getCoffeeId().equals(newItem.getCoffeeId()) &&
                    oldItem.getLocalizedName().equals(newItem.getLocalizedName()) &&
                    oldItem.getSelectedSize().equals(newItem.getSelectedSize()) &&
                    oldItem.getQuantity() == newItem.getQuantity() &&
                    Double.compare(oldItem.getTotalPrice(), newItem.getTotalPrice()) == 0 &&
                    oldItem.isHasSugar() == newItem.isHasSugar();
        }
    };
}