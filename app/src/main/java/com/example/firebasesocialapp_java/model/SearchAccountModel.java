package com.example.firebasesocialapp_java.model;

public class SearchAccountModel {

    private String uid;
    private String imageUrl;
    private Boolean isFollowing;
    private String name;

    public SearchAccountModel() {
    }

    public SearchAccountModel(String uid, String imgUrl, String name) {
        this.uid = uid;
        this.imageUrl = imgUrl;
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Boolean getFollowing() {
        return isFollowing;
    }

    public String getName() {
        return name;
    }
}
