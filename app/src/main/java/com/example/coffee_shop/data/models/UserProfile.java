package com.example.coffee_shop.data.models;

import androidx.annotation.NonNull;

public class UserProfile {

    private String username;
    private String email;
    private String profileImage;
    private ShippingAddress shippingAddress;

    public UserProfile() { }

    public UserProfile(String username,
                       String email,
                       String profileImage,
                       ShippingAddress shippingAddress) {

        this.username        = username;
        this.email           = email;
        this.profileImage    = profileImage;
        this.shippingAddress = shippingAddress;
    }

    public UserProfile(UserProfile other) {
        this.username        = other.username;
        this.email           = other.email;
        this.profileImage    = other.profileImage;
        this.shippingAddress = other.shippingAddress;
    }

    public String getUsername()                { return username; }
    public void   setUsername(String v)        { username = v;    }

    public String getEmail()                   { return email;    }
    public void   setEmail(String v)           { email = v;       }

    public String getProfileImage()            { return profileImage; }
    public void   setProfileImage(String v)    { profileImage = v;    }

    public ShippingAddress getShippingAddress()         { return shippingAddress; }
    public void            setShippingAddress(ShippingAddress v) { shippingAddress = v; }

    @NonNull @Override
    public String toString() {
        return "UserProfile{" +
                "username='"        + username        + '\'' +
                ", email='"         + email           + '\'' +
                ", profileImage='"  + profileImage    + '\'' +
                ", shippingAddress=" + (shippingAddress != null ? shippingAddress.toString() : "null") +
                '}';
    }
}