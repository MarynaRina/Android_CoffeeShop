package com.example.coffee_shop.data.remote;

import android.net.Uri;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.coffee_shop.core.managers.CloudinaryManager;
import java.util.Map;

public class CloudinaryDataSource {

    public CloudinaryDataSource(CloudinaryManager cloudinaryManager) {
    }

    public void uploadImage(Uri imageUri, final CustomUploadCallback callback) {
        MediaManager.get().upload(imageUri)
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

    public interface CustomUploadCallback {
        void onStart(String requestId);
        void onProgress(String requestId, long bytes, long totalBytes);
        void onSuccess(String imageUrl);
        void onError(String error);
    }
}