package com.example.coffee_shop.utils;

import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

public class AnimationUtils {
    public static void pressReleasingAnimation(View view) {
        view.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(200)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> view.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(150)
                        .setInterpolator(new DecelerateInterpolator())
                        .start())
                .start();
    }

    public static void highlightsAnimation(View view) {
        view.animate()
                .scaleX(1.1f)
                .scaleY(1.1f)
                .setDuration(200)
                .setInterpolator(new OvershootInterpolator())
                .withEndAction(() -> view.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(150)
                        .setInterpolator(new DecelerateInterpolator())
                        .start())
                .start();
    }
}
