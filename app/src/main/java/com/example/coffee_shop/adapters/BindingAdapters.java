package com.example.coffee_shop.adapters;

import android.util.Log;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class BindingAdapters {
    @BindingAdapter("imageUrl")
    public static void loadImage(ImageView view, String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Log.d("Glide", "Loading image: " + imageUrl); // Перевіряємо, чи приходить URL

            Glide.with(view.getContext())
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(view);
        } else {
            Log.e("Glide", "Image URL is null or empty!");
        }
    }
}