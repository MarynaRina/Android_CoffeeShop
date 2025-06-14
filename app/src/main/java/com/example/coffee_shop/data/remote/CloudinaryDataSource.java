package com.example.coffee_shop.data.remote;

import android.net.Uri;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.coffee_shop.core.managers.CloudinaryManager;
import java.util.Map;

public class CloudinaryDataSource {

    private final CloudinaryManager cloudinaryManager;

    public CloudinaryDataSource(CloudinaryManager cloudinaryManager) {
        this.cloudinaryManager = cloudinaryManager;
    }

    public void uploadImage(Uri imageUri, final CustomUploadCallback callback) {
        MediaManager.get().upload(imageUri)
                // You can add parameters here (e.g., folder, public_id, resource_type, etc.)
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        callback.onStart(requestId);
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        callback.onProgress(requestId, bytes, totalBytes);
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        // Change the key "url" to "secure_url" if you need a secure URL:
                        String imageUrl = (String) resultData.get("secure_url");
                        if (imageUrl == null) {
                            imageUrl = (String) resultData.get("url");
                        }
                        callback.onSuccess(imageUrl);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        callback.onError(error.getDescription());
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        callback.onError(error.getDescription());
                    }
                })
                .dispatch();
    }

    // Define your own callback interface to decouple from Cloudinary's callback
    public interface CustomUploadCallback {
        void onStart(String requestId);
        void onProgress(String requestId, long bytes, long totalBytes);
        void onSuccess(String imageUrl);
        void onError(String error);
    }
}