package com.example.coffee_shop.core.utils;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

public class AnimationUtils {
    public static void animateSlideDownAndDismiss(View view, Runnable onEnd) {
        view.animate()
                .translationY(view.getHeight())  // з'їзд донизу
                .alpha(0f)                       // трохи фейду
                .setDuration(400)
                .withEndAction(onEnd)
                .start();
    }

    public static void animateSlideUp(View view) {
        view.setTranslationY(view.getHeight()); // стартуємо знизу
        view.animate()
                .translationY(0)   // виїжджає вгору до позиції 0
                .alpha(1f)         // стає повністю видимим
                .setDuration(600)
                .setInterpolator(new OvershootInterpolator(1.2f))
                .start();
    }
    public static void animateFavorite(View view) {
        view.animate()
                .scaleX(1.3f)
                .scaleY(1.3f)
                .setDuration(150)
                .withEndAction(() -> {
                    view.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(150)
                            .start();
                })
                .start();
    }
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

    public static void animatePopup(View view) {
        view.setAlpha(0f);
        view.setScaleX(0.8f);
        view.setScaleY(0.8f);

        view.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(300)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    public static void animatePopupClose(View view, Runnable onEndAction) {
        view.animate()
                .alpha(0f)
                .scaleX(0.8f)
                .scaleY(0.8f)
                .setDuration(300)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(onEndAction)
                .start();
    }

    public static void bounceAnimation(View view) {
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

    public static void moveUnderline(View underline, int startX, int endX, int duration) {
        ValueAnimator animator = ValueAnimator.ofInt(startX, endX);
        animator.setDuration(duration);
        animator.setInterpolator(new AccelerateDecelerateInterpolator()); // Плавний перехід

        animator.addUpdateListener(animation -> {
            int animatedValue = (int) animation.getAnimatedValue();
            underline.setTranslationX(animatedValue); // Переміщення підкреслення
        });

        animator.start();
    }
}
