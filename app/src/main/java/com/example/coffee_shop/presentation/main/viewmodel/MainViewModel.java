package com.example.coffee_shop.presentation.main.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.coffee_shop.R;
import com.example.coffee_shop.presentation.cart.view.CartFragment;
import com.example.coffee_shop.presentation.favorite.view.FavoritesFragment;
import com.example.coffee_shop.presentation.home.view.HomeFragment;
import com.example.coffee_shop.presentation.profile.view.ProfileFragment;
import com.google.firebase.auth.FirebaseAuth;

public class MainViewModel extends AndroidViewModel {
    private final FirebaseAuth firebaseAuth;
    private final MutableLiveData<Boolean> isUserAuthenticated = new MutableLiveData<>();
    private final MutableLiveData<FragmentState> selectedFragmentState = new MutableLiveData<>();
    private final MutableLiveData<NavStyle> navStyle = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        this.firebaseAuth = FirebaseAuth.getInstance();
        checkUserAuthentication();
    }

    public LiveData<Boolean> getIsUserAuthenticated() {
        return isUserAuthenticated;
    }

    public LiveData<FragmentState> getSelectedFragmentState() {
        return selectedFragmentState;
    }

    public LiveData<NavStyle> getNavStyle() {
        return navStyle;
    }

    private void checkUserAuthentication() {
        isUserAuthenticated.setValue(firebaseAuth.getCurrentUser() != null);
    }

    public void selectFragment(int itemId) {
        FragmentState fragmentState = new FragmentState();
        NavStyle style = new NavStyle();

        if (itemId == R.id.home) {
            fragmentState.fragment = new HomeFragment();
            style.isHomeFragment = true;
        } else if (itemId == R.id.cart) {
            fragmentState.fragment = new CartFragment();
            style.isHomeFragment = true;
        } else if (itemId == R.id.favorite) {
            fragmentState.fragment = new FavoritesFragment();
            style.isHomeFragment = true;
        } else if (itemId == R.id.profile) {
            fragmentState.fragment = new ProfileFragment();
            style.isHomeFragment = false;
        } else {
            fragmentState.fragment = new HomeFragment();
            style.isHomeFragment = true;
        }

        selectedFragmentState.setValue(fragmentState);
        navStyle.setValue(style);
    }


    public static class FragmentState {
        public Fragment fragment;
    }

    public static class NavStyle {
        public boolean isHomeFragment;
    }
}