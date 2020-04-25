package com.appsnipp.loginsamples.models.chat;

public class Message {
    private String text;
    private String from;
    private String to;
    private String createdAt;

    public Message(String text, String from, String to, String createdAt) {
        this.text = text;
        this.from = from;
        this.to = to;
        this.createdAt = createdAt;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
