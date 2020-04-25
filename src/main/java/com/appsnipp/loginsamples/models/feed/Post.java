package com.appsnipp.loginsamples.models.feed;

public class Post {
    private String _id;
    private String username;
    private String title;
    private String text;
    private String[] likes;
    private String creationDate;

    public Post(String _id, String username, String title, String text, String[] likes, String creationDate) {
        this._id = _id;
        this.username = username;
        this.title = title;
        this.text = text;
        this.likes = likes;
        this.creationDate = creationDate;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String[] getLikes() {
        return likes;
    }

    public void setLikes(String[] likes) {
        this.likes = likes;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }
}
