package com.example.coffee_shop.core.managers;

import android.content.Context;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.UploadCallback;

import java.util.HashMap;
import java.util.Map;

public class CloudinaryManager {
    private static CloudinaryManager instance;

    private CloudinaryManager(Context context) {
        Context applicationContext = context.getApplicationContext();
        Map<String, Object> config = new HashMap<>();
        config.put("cloud_name", "dur0e2sni");
        config.put("api_key", "447978645624194");
        config.put("api_secret", "LAEPAz2rEdILE5CbbwiOKOPEYHg");
        MediaManager.init(applicationContext, config); // Ініціалізація MediaManager з Context
    }

    public static CloudinaryManager getInstance(Context context) {
        if (instance == null) {
            instance = new CloudinaryManager(context);
        }
        return instance;
    }

    public void upload(byte[] data, Map<String, Object> options, UploadCallback callback) {
        MediaManager.get().upload(data)
                .option("folder", options.get("folder"))
                .option("public_id", options.get("public_id"))
                .option("resource_type", options.get("resource_type"))
                .callback(callback)
                .dispatch();
    }
}