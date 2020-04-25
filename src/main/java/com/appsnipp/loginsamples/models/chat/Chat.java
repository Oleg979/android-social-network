package com.appsnipp.loginsamples.models.chat;

public class Chat {
    private String _id;
    private String user1;
    private String user2;
    private Message[] messages;

    public Chat(String id, String user1, String user2, Message[] messages) {
        this._id = id;
        this.user1 = user1;
        this.user2 = user2;
        this.messages = messages;
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getUser1() {
        return user1;
    }

    public void setUser1(String user1) {
        this.user1 = user1;
    }

    public String getUser2() {
        return user2;
    }

    public void setUser2(String user2) {
        this.user2 = user2;
    }

    public Message[] getMessages() {
        return messages;
    }

    public void setMessages(Message[] messages) {
        this.messages = messages;
    }
}
