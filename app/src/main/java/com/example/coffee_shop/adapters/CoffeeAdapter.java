package com.example.coffee_shop.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffee_shop.databinding.ItemCoffeeBinding;
import com.example.coffee_shop.data.models.Coffee;
import com.example.coffee_shop.ui.cart.AddToCartBottomSheetFragment;
import com.example.coffee_shop.ui.shared.SharedCoffeeViewModel;

import java.util.List;

public class CoffeeAdapter extends RecyclerView.Adapter<CoffeeAdapter.CoffeeViewHolder> {
    private final List<Coffee> coffeeList;
    private final FragmentManager fragmentManager;
    private final Context context;
    private final SharedCoffeeViewModel sharedViewModel;


    public CoffeeAdapter(List<Coffee> coffeeList, Context context, FragmentManager fragmentManager, SharedCoffeeViewModel sharedViewModel) {
        this.coffeeList = coffeeList;
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.sharedViewModel = sharedViewModel;
    }

    @NonNull
    @Override
    public CoffeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemCoffeeBinding binding = ItemCoffeeBinding.inflate(inflater, parent, false);
        return new CoffeeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CoffeeViewHolder holder, int position) {
        Coffee coffee = coffeeList.get(position);
        holder.bind(coffee);
    }

    @Override
    public int getItemCount() {
        return coffeeList != null ? coffeeList.size() : 0;
    }

    public void updateData(List<Coffee> newCoffeeList) {
        coffeeList.clear();
        if (newCoffeeList != null) {
            coffeeList.addAll(newCoffeeList);
        }
        notifyDataSetChanged();
    }

    class CoffeeViewHolder extends RecyclerView.ViewHolder {
        private final ItemCoffeeBinding binding;

        public CoffeeViewHolder(@NonNull ItemCoffeeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Coffee coffee) {
            binding.setCoffee(coffee);
            binding.executePendingBindings();

            binding.addToCartButton.setOnClickListener(v -> {
                AddToCartBottomSheetFragment bottomSheet = AddToCartBottomSheetFragment.newInstance(coffee);
                bottomSheet.show(fragmentManager, bottomSheet.getTag());
            });
        }
    }
}