package com.example.coffee_shop.presentation.common.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.coffee_shop.data.models.CartItem;
import com.example.coffee_shop.databinding.ItemCartBinding;

public class CartAdapter extends ListAdapter<CartItem, CartAdapter.CartViewHolder> {
    private CartItemDeleteListener deleteListener;

    public CartAdapter() {
        super(DIFF_CALLBACK);
    }

    public interface CartItemDeleteListener {
        void onDeleteCartItem(CartItem item);
    }

    public CartAdapter(CartItemDeleteListener deleteListener) {
        super(DIFF_CALLBACK);
        this.deleteListener = deleteListener;
    }
    public void setDeleteListener(CartItemDeleteListener listener) {
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemCartBinding binding = ItemCartBinding.inflate(inflater, parent, false);
        return new CartViewHolder(binding, deleteListener);
    }


    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        holder.bind(getItem(position));

    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        private final ItemCartBinding binding;
        private final CartItemDeleteListener deleteListener;

        public CartViewHolder(ItemCartBinding binding, CartItemDeleteListener deleteListener) {
            super(binding.getRoot());
            this.binding = binding;
            this.deleteListener = deleteListener; // Store the listener from parameter
        }

        public void bind(CartItem item) {
            binding.itemTitle.setText(item.getCoffeeName());
            binding.itemQuantity.setText("x" + item.getQuantity());
            binding.itemSize.setText("Size: " + item.getSelectedSize());
            binding.itemPrice.setText(String.format("$%.2f", item.getTotalPrice()));
            binding.itemSugar.setText(item.isHasSugar() ? "With sugar" : "Without sugar");

            binding.btnDelete.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onDeleteCartItem(item);
                }
            });

            Glide.with(binding.getRoot().getContext())
                    .load(item.getCoffeeImageUrl())
                    .into(binding.itemImage);
        }
    }
    private static final DiffUtil.ItemCallback<CartItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<CartItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull CartItem oldItem, @NonNull CartItem newItem) {
            return oldItem.getCoffeeId().equals(newItem.getCoffeeId()) && oldItem.getSelectedSize().equals(newItem.getSelectedSize());
        }

        @Override
        public boolean areContentsTheSame(@NonNull CartItem oldItem, @NonNull CartItem newItem) {
            return oldItem.getCoffeeId().equals(newItem.getCoffeeId()) &&
                    oldItem.getCoffeeName().equals(newItem.getCoffeeName()) &&
                    oldItem.getSelectedSize().equals(newItem.getSelectedSize()) &&
                    oldItem.getQuantity() == newItem.getQuantity() &&
                    Double.compare(oldItem.getTotalPrice(), newItem.getTotalPrice()) == 0 &&
                    oldItem.isHasSugar() == newItem.isHasSugar();
        }
    };
}