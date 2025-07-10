package com.example.coffee_shop.core.utils;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

public class LiveDataExtensions {
    public static <T> void observeOnce(LiveData<T> liveData, LifecycleOwner owner, Observer<T> observer) {
        liveData.observe(owner, new Observer<T>() {
            @Override
            public void onChanged(T t) {
                liveData.removeObserver(this);
                observer.onChanged(t);
            }
        });
    }
}