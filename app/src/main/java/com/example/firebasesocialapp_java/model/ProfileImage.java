package com.example.firebasesocialapp_java.model;

public class ProfileImage {

    private String imageUrl;
    private String metaData;

    public ProfileImage(){}

    public ProfileImage(String imageUrl, String metaData) {
        this.imageUrl = imageUrl;
        this.metaData = metaData;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getMetaData() {
        return metaData;
    }

    public void setMetaData(String metaData) {
        this.metaData = metaData;
    }
}
