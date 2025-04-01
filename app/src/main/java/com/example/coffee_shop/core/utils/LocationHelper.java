package com.example.coffee_shop.core.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class LocationHelper {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private final Activity activity;
    private final LocationListener listener;
    private final FusedLocationProviderClient fusedLocationClient;

    public interface LocationListener {
        void onLocationRetrieved(String city, String country);
        void onLocationError(String errorMessage);
    }

    public LocationHelper(Activity activity, LocationListener listener) {
        this.activity = activity;
        this.listener = listener;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
    }

    public void requestLocation() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLastLocation();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            listener.onLocationError("Доступ до локації відхилено");
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(activity, location -> {
            if (location != null) {
                getAddressFromLocation(location);
            } else {
                requestNewLocation();
            }
        }).addOnFailureListener(e -> listener.onLocationError("Помилка отримання локації: " + e.getMessage()));
    }

    private void requestNewLocation() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            listener.onLocationError("Доступ до локації відхилено");
            return;
        }

        LocationRequest locationRequest = new LocationRequest.Builder(10000)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build();

        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                fusedLocationClient.removeLocationUpdates(this);

                if (!locationResult.getLocations().isEmpty()) {
                    getAddressFromLocation(Objects.requireNonNull(locationResult.getLastLocation()));
                } else {
                    listener.onLocationError("Не вдалося отримати актуальну локацію");
                }
            }
        }, Looper.getMainLooper());
    }

    private void getAddressFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                String city = addresses.get(0).getLocality();
                String country = addresses.get(0).getCountryName();
                listener.onLocationRetrieved(city, country);
            } else {
                listener.onLocationError("Адреса не знайдена");
            }
        } catch (IOException e) {
            listener.onLocationError("Помилка отримання адреси: " + e.getMessage());
        }
    }
}