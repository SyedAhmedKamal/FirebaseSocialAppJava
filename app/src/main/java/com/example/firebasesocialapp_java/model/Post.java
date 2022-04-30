package com.example.firebasesocialapp_java.model;

public class Post {

    String timeStamp;
    String author;
    String uid;
    String imageUrl;
    String description;// nullable
    int likes = 0;

    public Post() {
    }

    // without description
    public Post(String timeStamp, String author, String uid, String imageUrl, int likes) {
        this.timeStamp = timeStamp;
        this.author = author;
        this.uid = uid;
        this.imageUrl = imageUrl;
        this.likes = likes;
    }

    public Post(String timeStamp, String author, String uid, String imageUrl, String description, int likes) {
        this.timeStamp = timeStamp;
        this.author = author;
        this.uid = uid;
        this.imageUrl = imageUrl;
        this.description = description;
        this.likes = likes;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getAuthor() {
        return author;
    }

    public String getUid() {
        return uid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public int getLikes() {
        return likes;
    }
}
