package com.example.coffee_shop.utils;

import com.cloudinary.Cloudinary;


import java.util.Map;

public class CloudinaryManager {
    private static Cloudinary cloudinary;

    public static Cloudinary getInstance() {
        if (cloudinary == null) {
            cloudinary = new Cloudinary(Map.of(
                    "cloud_name", "dur0e2sni",
                    "api_key", "447978645624194",
                    "api_secret", "LAEPAz2rEdILE5CbbwiOKOPEYHg"
            ));
        }
        return cloudinary;
    }
}