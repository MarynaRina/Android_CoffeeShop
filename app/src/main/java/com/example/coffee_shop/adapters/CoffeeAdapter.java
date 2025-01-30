package com.example.coffee_shop.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffee_shop.databinding.ItemCoffeeBinding;
import com.example.coffee_shop.models.Coffee;

import java.util.List;

public class CoffeeAdapter extends RecyclerView.Adapter<CoffeeAdapter.CoffeeViewHolder> {
    private List<Coffee> coffeeList;

    public CoffeeAdapter() {}

    public CoffeeAdapter(List<Coffee> coffeeList) {
        this.coffeeList = coffeeList;
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


    static class CoffeeViewHolder extends RecyclerView.ViewHolder {
        private final ItemCoffeeBinding binding;

        public CoffeeViewHolder(@NonNull ItemCoffeeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Coffee coffee) {
            binding.setCoffee(coffee);
            binding.executePendingBindings();
        }
    }
}